/**
 * 영화 상세 페이지 - 관람기록 추가 기능
 */

let currentMovieId = null;
let currentMovieTitle = null;
let selectedRating = null;

/**
 * 페이지 초기화
 */
export function initMovieDetail(movieId, movieTitle) {
    currentMovieId = movieId;
    currentMovieTitle = movieTitle;

    // 찜 버튼 초기화
    initWishlistButton();

    // 모달 관련 요소
    const modal = document.getElementById('watchHistoryModal');
    const addBtn = document.getElementById('addWatchHistoryBtn');
    const closeBtn = document.getElementById('closeModalBtn');
    const cancelBtn = document.getElementById('cancelBtn');
    const form = document.getElementById('watchHistoryForm');

    // 버튼 존재 확인 (로그인하지 않은 경우 버튼이 없을 수 있음)
    if (!addBtn) return;

    // 모달 열기
    addBtn.addEventListener('click', openModal);

    // 모달 닫기
    closeBtn.addEventListener('click', closeModal);
    cancelBtn.addEventListener('click', closeModal);

    // 모달 배경 클릭시 닫기
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            closeModal();
        }
    });

    // 별점 선택
    initRatingStars();

    // 극장 검색
    initCinemaSearch();

    // 한줄평 글자수 카운터
    initCommentCounter();

    // 폼 제출
    form.addEventListener('submit', handleSubmit);

    // 날짜 선택 버튼
    initDatePicker();

    // 시간 선택 버튼
    initTimePicker();
}

/**
 * 모달 열기
 */
function openModal() {
    const modal = document.getElementById('watchHistoryModal');
    const modalTitle = document.getElementById('modalMovieTitle');

    // 영화 제목 표시
    modalTitle.textContent = currentMovieTitle || '영화';

    // 오늘 날짜 기본값 설정
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('watchedAt').value = today;

    // 날짜 디스플레이에도 오늘 날짜 표시
    const watchedAtDisplay = document.getElementById('watchedAtDisplay');
    if (watchedAtDisplay) {
        const dateObj = new Date();
        const year = dateObj.getFullYear();
        const month = String(dateObj.getMonth() + 1).padStart(2, '0');
        const day = String(dateObj.getDate()).padStart(2, '0');
        watchedAtDisplay.value = `${year}-${month}-${day}`;
    }

    // 모달 표시
    modal.classList.remove('hidden');
    modal.classList.add('flex');
    modal.style.display = 'flex';

    // 폼 초기화 (단, 위에서 설정한 날짜는 유지)
    const currentDate = document.getElementById('watchedAt').value;
    const currentDateDisplay = document.getElementById('watchedAtDisplay').value;
    resetForm();
    document.getElementById('watchedAt').value = currentDate;
    document.getElementById('watchedAtDisplay').value = currentDateDisplay;
}

/**
 * 모달 닫기
 */
function closeModal() {
    const modal = document.getElementById('watchHistoryModal');
    modal.classList.add('hidden');
    modal.classList.remove('flex');
    modal.style.display = 'none';
    resetForm();
}

/**
 * 폼 초기화
 */
function resetForm() {
    document.getElementById('watchHistoryForm').reset();
    document.getElementById('cinemaId').value = '';
    document.getElementById('selectedCinema').textContent = '';
    document.getElementById('cinemaSearchResults').classList.add('hidden');
    selectedRating = null;

    // 별점 초기화
    document.querySelectorAll('.star-btn').forEach(star => {
        star.classList.remove('text-yellow-400');
        star.classList.add('text-gray-300');
    });

    // 글자수 카운터 초기화
    document.getElementById('commentCount').textContent = '0';

    // 날짜/시간 디스플레이 초기화
    const watchedAtDisplay = document.getElementById('watchedAtDisplay');
    const showTimeDisplay = document.getElementById('showTimeDisplay');
    if (watchedAtDisplay) watchedAtDisplay.value = '';
    if (showTimeDisplay) showTimeDisplay.value = '';
}

/**
 * 별점 선택 초기화
 */
function initRatingStars() {
    const stars = document.querySelectorAll('.star-btn');

    stars.forEach(star => {
        star.addEventListener('click', (e) => {
            e.preventDefault();
            const rating = parseInt(star.dataset.rating);
            selectedRating = rating;

            // 별점 시각화 업데이트
            stars.forEach((s, index) => {
                if (index < rating) {
                    s.classList.remove('text-gray-300');
                    s.classList.add('text-yellow-400');
                } else {
                    s.classList.remove('text-yellow-400');
                    s.classList.add('text-gray-300');
                }
            });

            // hidden input 업데이트
            document.getElementById('rating').value = rating;
        });

        // 호버 효과
        star.addEventListener('mouseenter', () => {
            const rating = parseInt(star.dataset.rating);
            stars.forEach((s, index) => {
                if (index < rating) {
                    s.classList.add('text-yellow-400');
                }
            });
        });

        star.addEventListener('mouseleave', () => {
            // 선택된 별점으로 복원
            stars.forEach((s, index) => {
                if (selectedRating && index < selectedRating) {
                    s.classList.add('text-yellow-400');
                    s.classList.remove('text-gray-300');
                } else {
                    s.classList.remove('text-yellow-400');
                    s.classList.add('text-gray-300');
                }
            });
        });
    });
}

/**
 * 극장 검색 초기화
 */
function initCinemaSearch() {
    const searchInput = document.getElementById('cinemaSearch');
    const resultsDiv = document.getElementById('cinemaSearchResults');
    let debounceTimer;

    searchInput.addEventListener('input', (e) => {
        const keyword = e.target.value.trim();

        // 디바운싱
        clearTimeout(debounceTimer);

        if (keyword.length < 2) {
            resultsDiv.classList.add('hidden');
            resultsDiv.innerHTML = '';
            return;
        }

        debounceTimer = setTimeout(() => {
            searchCinema(keyword);
        }, 300);
    });
}

/**
 * 극장 검색 API 호출
 */
async function searchCinema(keyword) {
    const resultsDiv = document.getElementById('cinemaSearchResults');

    try {
        const response = await fetch(`/api/cinema/search?keyword=${encodeURIComponent(keyword)}`);

        if (!response.ok) {
            throw new Error('극장 검색 실패');
        }

        const cinemas = await response.json();

        if (cinemas.length === 0) {
            resultsDiv.innerHTML = '<p class="text-sm text-gray-500 p-2">검색 결과가 없습니다.</p>';
            resultsDiv.classList.remove('hidden');
            return;
        }

        // 검색 결과 표시
        resultsDiv.innerHTML = cinemas.map(cinema => `
            <div class="cinema-result p-2 hover:bg-blue-50 cursor-pointer rounded border-b last:border-b-0"
                 data-id="${cinema.cinemaId}"
                 data-name="${(cinema.brand ? cinema.brand + " " : "") + cinema.cinemaName}">
                <p class="text-sm font-semibold text-gray-900">${cinema.brand ? cinema.brand + " " : ""}${cinema.cinemaName}</p>
                <p class="text-xs text-gray-500">${cinema.streetAddress || cinema.loadAddress || ''}</p>
            </div>
        `).join('');

        resultsDiv.classList.remove('hidden');

        // 극장 선택 이벤트
        resultsDiv.querySelectorAll('.cinema-result').forEach(item => {
            item.addEventListener('click', () => {
                selectCinema(item.dataset.id, item.dataset.name);
            });
        });

    } catch (error) {
        console.error('극장 검색 중 오류:', error);
        resultsDiv.innerHTML = '<p class="text-sm text-red-500 p-2">검색 중 오류가 발생했습니다.</p>';
        resultsDiv.classList.remove('hidden');
    }
}

/**
 * 극장 선택
 */
function selectCinema(cinemaId, cinemaName) {
    document.getElementById('cinemaId').value = cinemaId;
    document.getElementById('cinemaSearch').value = cinemaName;
    document.getElementById('selectedCinema').textContent = `선택된 극장: ${cinemaName}`;
    document.getElementById('cinemaSearchResults').classList.add('hidden');
}

/**
 * 한줄평 글자수 카운터
 */
function initCommentCounter() {
    const textarea = document.getElementById('comment');
    const counter = document.getElementById('commentCount');

    textarea.addEventListener('input', () => {
        const length = textarea.value.length;
        counter.textContent = length;

        if (length > 500) {
            counter.classList.add('text-red-500');
        } else {
            counter.classList.remove('text-red-500');
        }
    });
}

/**
 * 폼 제출 처리
 */
async function handleSubmit(e) {
    e.preventDefault();

    // 로그인 체크
    // API 호출 시 401 에러가 발생하면 로그인 페이지로 리다이렉트됨
    // 필수 필드 검증
    const watchedAt = document.getElementById('watchedAt').value;
    if (!watchedAt) {
        alert('관람 날짜를 입력해주세요.');
        return;
    }

    // 요청 데이터 구성
    const requestData = {
        movieId: currentMovieId,
        watchedAt: watchedAt,
        cinemaId: document.getElementById('cinemaId').value || null,
        cinemaName: document.getElementById('cinemaSearch').value || null,
        showTime: document.getElementById('showTime').value || null,
        rating: selectedRating,
        comment: document.getElementById('comment').value || null
    };

    try {
        const response = await fetch('/api/watch-history', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                alert("로그인이 필요합니다.");
                window.location.href = "/user/login";
                return;
            }
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || "관람기록 추가 실패");
        }
        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                alert("로그인이 필요합니다.");
                window.location.href = "/user/login";
                return;
            }
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || "관람기록 추가 실패");
        }
        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                alert("로그인이 필요합니다.");
                window.location.href = "/user/login";
                return;
            }
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || "관람기록 추가 실패");
        }
        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                alert("로그인이 필요합니다.");
                window.location.href = "/user/login";
                return;
            }
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || "관람기록 추가 실패");
        }

        const result = await response.json();

        // 성공 메시지
        alert('관람기록이 추가되었습니다!');

        // 모달 닫기
        closeModal();

        // 관람기록 페이지로 이동할지 물어보기
        if (confirm('내 관람기록 페이지로 이동하시겠습니까?')) {
            window.location.href = '/user/watch-history';
        }

    } catch (error) {
        console.error('관람기록 추가 중 오류:', error);
        alert(error.message || '관람기록 추가 중 오류가 발생했습니다.');
    }
}

/**
 * 날짜 선택 버튼 초기화
 */
function initDatePicker() {
    const datePickerBtn = document.getElementById('datePickerBtn');
    const watchedAtInput = document.getElementById('watchedAt');
    const watchedAtDisplay = document.getElementById('watchedAtDisplay');

    if (!datePickerBtn || !watchedAtInput || !watchedAtDisplay) return;

    // 버튼 클릭 시 날짜 선택 창 열기
    datePickerBtn.addEventListener('click', () => {
        watchedAtInput.showPicker();
    });

    // 디스플레이 입력란 클릭 시에도 날짜 선택 창 열기
    watchedAtDisplay.addEventListener('click', () => {
        watchedAtInput.showPicker();
    });

    // 날짜 선택 시 디스플레이 업데이트
    watchedAtInput.addEventListener('change', (e) => {
        const date = e.target.value;
        if (date) {
            // YYYY-MM-DD 형식을 보기 좋게 변환
            const dateObj = new Date(date);
            const year = dateObj.getFullYear();
            const month = String(dateObj.getMonth() + 1).padStart(2, '0');
            const day = String(dateObj.getDate()).padStart(2, '0');
            watchedAtDisplay.value = `${year}-${month}-${day}`;
        }
    });
}

/**
 * 시간 선택 버튼 초기화
 */
function initTimePicker() {
    const timePickerBtn = document.getElementById('timePickerBtn');
    const showTimeInput = document.getElementById('showTime');
    const showTimeDisplay = document.getElementById('showTimeDisplay');

    if (!timePickerBtn || !showTimeInput || !showTimeDisplay) return;

    // 버튼 클릭 시 시간 선택 창 열기
    timePickerBtn.addEventListener('click', () => {
        showTimeInput.showPicker();
    });

    // 디스플레이 입력란 클릭 시에도 시간 선택 창 열기
    showTimeDisplay.addEventListener('click', () => {
        showTimeInput.showPicker();
    });

    // 시간 선택 시 디스플레이 업데이트
    showTimeInput.addEventListener('change', (e) => {
        const time = e.target.value;
        if (time) {
            showTimeDisplay.value = time;
        }
    });
}

/**
 * 찜 버튼 초기화
 */
async function initWishlistButton() {
    const wishlistBtn = document.getElementById('toggleWishlistBtn');
    if (!wishlistBtn) return;

    const movieId = currentMovieId;

    // 현재 찜 상태 확인
    await checkWishlistStatus(movieId);

    // 찜 버튼 클릭 이벤트
    wishlistBtn.addEventListener('click', () => toggleWishlist(movieId));
}

/**
 * 찜 상태 확인
 */
async function checkWishlistStatus(movieId) {
    try {
        const response = await fetch(`/api/wishlist/${movieId}`);

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                return; // 로그인하지 않은 경우 기본 상태 유지
            }
            throw new Error('찜 상태 확인 실패');
        }

        const data = await response.json();
        updateWishlistUI(data.isWishlisted);
    } catch (error) {
        console.error('찜 상태 확인 중 오류:', error);
    }
}

/**
 * 찜 토글
 */
async function toggleWishlist(movieId) {
    try {
        const response = await fetch(`/api/wishlist/${movieId}`, {
            method: 'POST'
        });

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                alert('로그인이 필요합니다.');
                window.location.href = '/user/login';
                return;
            }
            throw new Error('찜 처리 실패');
        }

        const data = await response.json();
        updateWishlistUI(data.isWishlisted);

        // 성공 메시지 (선택사항)
        if (data.isWishlisted) {
            showToast('찜 목록에 추가되었습니다 ❤️');
        } else {
            showToast('찜 목록에서 제거되었습니다');
        }
    } catch (error) {
        console.error('찜 처리 중 오류:', error);
    }
}

/**
 * 찜 UI 업데이트
 */
function updateWishlistUI(isWishlisted) {
    const wishlistBtn = document.getElementById('toggleWishlistBtn');
    const wishlistIcon = document.getElementById('wishlistIcon');
    const wishlistText = document.getElementById('wishlistText');

    if (!wishlistBtn || !wishlistIcon || !wishlistText) return;

    if (isWishlisted) {
        // 찜한 상태
        wishlistBtn.classList.remove('bg-white', 'border-red-500', 'text-red-500');
        wishlistBtn.classList.add('bg-red-500', 'text-white', 'border-red-500');
        wishlistIcon.setAttribute('fill', 'currentColor');
        wishlistText.textContent = '찜 완료';
    } else {
        // 찜하지 않은 상태
        wishlistBtn.classList.remove('bg-red-500', 'text-white');
        wishlistBtn.classList.add('bg-white', 'border-red-500', 'text-red-500');
        wishlistIcon.setAttribute('fill', 'none');
        wishlistText.textContent = '찜';
    }
}

/**
 * 토스트 메시지 표시
 */
function showToast(message) {
    // 기존 토스트 제거
    const existingToast = document.getElementById('wishlistToast');
    if (existingToast) {
        existingToast.remove();
    }

    // 토스트 생성
    const toast = document.createElement('div');
    toast.id = 'wishlistToast';
    toast.className = 'fixed bottom-8 right-8 bg-gray-800 text-white px-6 py-3 rounded-lg shadow-lg z-50 transition-opacity duration-300';
    toast.textContent = message;
    document.body.appendChild(toast);

    // 3초 후 제거
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 2000);
}
