/**
 * 찜 목록 페이지
 */

/**
 * 페이지 초기화
 */
export async function initWishlist() {
    await loadWishlist();
}

/**
 * 찜 목록 로드
 */
async function loadWishlist() {
    const container = document.getElementById('wishlistContainer');
    const emptyMessage = document.getElementById('emptyMessage');

    try {
        const response = await fetch('/api/wishlist');

        if (!response.ok) {
            throw new Error('찜 목록 조회 실패');
        }

        const data = await response.json();
        const wishlist = data.wishlist;

        if (wishlist.length === 0) {
            container.classList.add('hidden');
            emptyMessage.classList.remove('hidden');
            return;
        }

        container.innerHTML = wishlist.map(item => `
            <div class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition">
                <div class="flex">
                    <!-- 포스터 -->
                    <div class="w-32 h-48 bg-gray-200 flex-shrink-0">
                        ${item.poster ?
                            `<img src="${item.poster}" alt="${item.title}" class="w-full h-full object-cover">` :
                            `<img src="/images/irani12.png" alt="${item.title}" class="w-full h-full object-cover opacity-50">`
                        }
                    </div>

                    <!-- 영화 정보 -->
                    <div class="flex-1 p-4 flex flex-col justify-between">
                        <div>
                            <a href="/movies/${item.movieId}" class="text-lg font-bold text-gray-900 hover:text-blue-600">
                                ${item.title}
                            </a>
                            <div class="mt-2 space-y-1 text-sm text-gray-600">
                                <div>장르: ${item.genre || '정보 없음'}</div>
                                <div>개봉일: ${item.repRlsDate || '정보 없음'}</div>
                            </div>
                        </div>

                        <div class="flex items-center justify-between mt-3">
                            <span class="text-xs text-gray-500">찜한 날짜: ${formatDate(item.createdAt)}</span>
                            <button onclick="window.removeWishlist(${item.movieId})"
                                    class="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition text-sm">
                                찜 해제
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `).join('');

        container.classList.remove('hidden');
        emptyMessage.classList.add('hidden');

    } catch (error) {
        console.error('찜 목록 조회 중 오류:', error);
        container.innerHTML = `
            <div class="text-center py-8 text-red-500">
                찜 목록을 불러오는 중 오류가 발생했습니다.
            </div>
        `;
    }
}

/**
 * 날짜 포맷팅
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * 찜 해제
 */
window.removeWishlist = async function(movieId) {
    if (!confirm('찜 목록에서 제거하시겠습니까?')) {
        return;
    }

    try {
        const response = await fetch(`/api/wishlist/${movieId}`, {
            method: 'POST'
        });

        if (!response.ok) {
            throw new Error('찜 해제 실패');
        }

        alert('찜 목록에서 제거되었습니다.');
        await loadWishlist(); // 목록 새로고침

    } catch (error) {
        console.error('찜 해제 중 오류:', error);
        alert('찜 해제 중 오류가 발생했습니다.');
    }
};
