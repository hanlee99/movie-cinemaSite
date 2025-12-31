// 상태 관리
let allCinemas = [];
let filteredCinemas = [];
let currentRegion = '서울';
let currentBrand = 'all';
let currentSpecial = 'all';
let currentPage = 1;
const itemsPerPage = 20;

// 초기화
export function initCinemaList() {
    setupEventListeners();
    loadAllCinemas();
}

// 전체 극장 데이터 로드 (최초 1번)
async function loadAllCinemas() {
    try {
        const response = await fetch('/api/cinema/all'); // ← 여기
        allCinemas = await response.json();
        console.log('로드된 극장 수:', allCinemas.length); // 확인용
        console.log('첫 번째 극장:', allCinemas[0]); // 구조 확인
        applyFilters();
    } catch (error) {
        console.error('극장 데이터 로드 실패:', error);
    }
}

// 이벤트 리스너 설정
function setupEventListeners() {
    // 지역 필터
    document.querySelectorAll('[data-region]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('[data-region]').forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');

            currentRegion = e.target.dataset.region;
            applyFilters();
        });
    });

    // 브랜드 필터
    document.querySelectorAll('[data-brand]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('[data-brand]').forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');

            currentBrand = e.target.dataset.brand;
            applyFilters();
        });
    });

    // 특별관 필터
    document.querySelectorAll('[data-special]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            document.querySelectorAll('[data-special]').forEach(b => b.classList.remove('active'));
            e.target.classList.add('active');

            currentSpecial = e.target.dataset.special;
            applyFilters();
        });
    });
}

// 필터링 적용 (모든 필터를 프론트에서 처리)
function applyFilters() {
    filteredCinemas = allCinemas.filter(cinema => {
        // 지역 필터
        if (currentRegion !== 'all') {
            const regions = currentRegion.split('/');
            const hasRegion = regions.some(region =>
                cinema.classificationRegion && cinema.classificationRegion.includes(region)
            );
            if (!hasRegion) return false;
        }

        // 브랜드 필터
        if (currentBrand !== 'all' && cinema.brand !== currentBrand) {
            return false;
        }

        // 특별관 필터
        if (currentSpecial !== 'all') {
            if (!cinema.specialtyTheaters || !cinema.specialtyTheaters.includes(currentSpecial)) {
                return false;
            }
        }

        return true;
    });

    currentPage = 1;
    renderCinemas();
    renderPagination();
}

// 극장 카드 렌더링
function renderCinemas() {
    const container = document.getElementById('theater-results');

    const startIndex = (currentPage - 1) * itemsPerPage;
    const endIndex = startIndex + itemsPerPage;
    const cinemasToShow = filteredCinemas.slice(startIndex, endIndex);

    if (cinemasToShow.length === 0) {
        container.innerHTML = '<p style="text-align: center; padding: 40px; color: #666;">검색 결과가 없습니다.</p>';
        return;
    }

    container.innerHTML = cinemasToShow.map(cinema => `
        <div class="theater-card" style="border: 1px solid #ddd; padding: 20px; margin-bottom: 15px; border-radius: 8px; cursor: pointer; transition: box-shadow 0.2s;">
            <h3 style="font-size: 18px; font-weight: bold; margin-bottom: 10px;">
                ${cinema.cinemaName}
            </h3>
            <p style="color: #666; margin: 5px 0;">
                📍 ${cinema.streetAddress}
            </p>
            <p style="color: #666; margin: 5px 0;">
                🏢 ${cinema.brand}
            </p>
            ${cinema.specialtyTheaters && cinema.specialtyTheaters.length > 0 ?
                `<p style="color: #666; margin: 5px 0;">
                    🎬 ${cinema.specialtyTheaters.join(', ')}
                </p>`
                : ''
            }
        </div>
    `).join('');

    document.querySelectorAll('.theater-card').forEach(card => {
        card.addEventListener('mouseenter', (e) => {
            e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.1)';
        });
        card.addEventListener('mouseleave', (e) => {
            e.currentTarget.style.boxShadow = 'none';
        });
    });
}

// 페이지네이션 렌더링
function renderPagination() {
    const container = document.getElementById('pagination');
    const totalPages = Math.ceil(filteredCinemas.length / itemsPerPage);

    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    let html = '';

    if (currentPage > 1) {
        html += `<button onclick="window.goToPage(${currentPage - 1})"
                        style="padding: 8px 16px; margin: 0 5px; border: 1px solid #ddd;
                               border-radius: 4px; cursor: pointer; background: white;">
                    이전
                </button>`;
    }

    html += `<span style="margin: 0 15px; font-weight: 500;">${currentPage} / ${totalPages}</span>`;

    if (currentPage < totalPages) {
        html += `<button onclick="window.goToPage(${currentPage + 1})"
                        style="padding: 8px 16px; margin: 0 5px; border: 1px solid #ddd;
                               border-radius: 4px; cursor: pointer; background: white;">
                    다음
                </button>`;
    }

    container.innerHTML = html;
}

// 페이지 이동
window.goToPage = function(page) {
    currentPage = page;
    renderCinemas();
    renderPagination();
    window.scrollTo({ top: 0, behavior: 'smooth' });
};


