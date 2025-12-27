// 관람기록 페이지 로직
import { fetchWatchHistory, deleteWatchHistory } from '../api/watch-history-api.js';

export function initWatchHistory() {
  const loadingEl = document.getElementById('loading');
  const emptyEl = document.getElementById('empty');
  const historyListEl = document.getElementById('historyList');

  // 페이지 로드 시 관람기록 불러오기
  loadWatchHistoryData();

  async function loadWatchHistoryData() {
    try {
      const data = await fetchWatchHistory();

      if (loadingEl) loadingEl.style.display = 'none';

      if (data.length === 0) {
        if (emptyEl) emptyEl.style.display = 'block';
      } else {
        displayHistory(data);
      }
    } catch (error) {
      console.error('Error:', error);
      if (loadingEl) loadingEl.innerHTML = '데이터를 불러오는데 실패했습니다.';
    }
  }

  // 관람기록 표시
  function displayHistory(historyList) {
    if (!historyListEl) return;
    historyListEl.innerHTML = '';

    historyList.forEach(item => {
      const historyItem = document.createElement('div');
      historyItem.className = 'bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition';

      const posterUrl = item.posterUrl || '/images/no-poster.jpg';
      const movieTitle = item.movieTitle || '정보 없음';

      // 별점 표시
      const ratingStars = item.rating ? '★'.repeat(item.rating) + '☆'.repeat(5 - item.rating) : '';
      const ratingHtml = item.rating ? `
        <div class="flex items-center gap-1 mb-2">
          <span class="text-yellow-400 text-lg">${ratingStars}</span>
          <span class="text-sm text-gray-600">(${item.rating}/5)</span>
        </div>
      ` : '';

      // 극장 정보
      const cinemaHtml = item.cinemaName ? `
        <div class="flex items-center gap-2 text-sm text-gray-600 mb-1">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
          </svg>
          <span>${item.cinemaName}</span>
        </div>
      ` : '';

      // 상영 시간
      const showTimeHtml = item.showTime ? `
        <div class="flex items-center gap-2 text-sm text-gray-600 mb-1">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <span>${item.showTime.substring(0, 5)}</span>
        </div>
      ` : '';

      // 한줄평
      const commentHtml = item.comment ? `
        <div class="mt-3 p-3 bg-gray-50 rounded-lg">
          <p class="text-sm text-gray-700 italic line-clamp-2">"${item.comment}"</p>
        </div>
      ` : '';

      historyItem.innerHTML = `
        <div class="md:flex">
          <!-- 포스터 -->
          <div class="md:w-1/4 flex-shrink-0">
            <img src="${posterUrl}" alt="${movieTitle}"
                 class="w-full h-64 md:h-full object-cover cursor-pointer hover:opacity-90 transition"
                 onerror="this.src='/images/no-poster.jpg'"
                 data-movie-id="${item.movieId}">
          </div>

          <!-- 정보 -->
          <div class="md:w-3/4 p-6">
            <div class="flex items-start justify-between mb-3">
              <div>
                <h3 class="text-2xl font-bold text-gray-900 mb-2 cursor-pointer hover:text-blue-600 transition"
                    data-movie-id="${item.movieId}">${movieTitle}</h3>
                <div class="flex items-center gap-2 text-sm text-gray-500">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                  </svg>
                  <span>관람일: ${formatDate(item.watchedAt)}</span>
                </div>
              </div>
              <button class="text-red-600 hover:text-red-700 hover:bg-red-50 p-2 rounded-lg transition delete-btn"
                      data-id="${item.id}"
                      title="삭제">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                </svg>
              </button>
            </div>

            ${ratingHtml}

            <div class="mb-3">
              ${cinemaHtml}
              ${showTimeHtml}
            </div>

            ${commentHtml}
          </div>
        </div>
      `;

      // 영화 상세페이지로 이동 (포스터, 제목 클릭)
      const clickableElements = historyItem.querySelectorAll('[data-movie-id]');
      clickableElements.forEach(el => {
        el.onclick = (e) => {
          const movieId = el.dataset.movieId;
          if (movieId) {
            window.location.href = `/movies/${movieId}`;
          }
        };
      });

      // 삭제 버튼 이벤트
      const deleteBtn = historyItem.querySelector('.delete-btn');
      deleteBtn.onclick = async (e) => {
        e.stopPropagation();
        await handleDelete(item.id);
      };

      historyListEl.appendChild(historyItem);
    });
  }

  // 관람기록 삭제
  async function handleDelete(historyId) {
    if (!confirm('이 관람기록을 삭제하시겠습니까?')) {
      return;
    }

    try {
      await deleteWatchHistory(historyId);
      alert('삭제되었습니다.');
      loadWatchHistoryData(); // 목록 새로고침
    } catch (error) {
      console.error('Error:', error);
      alert('삭제에 실패했습니다.');
    }
  }

  // 날짜 포맷
  function formatDate(dateString) {
    const date = new Date(dateString);
    return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`;
  }
}
