// Watch History API 호출 함수 모음

/**
 * 관람기록 목록 조회
 */
export async function fetchWatchHistory() {
  try {
    const response = await fetch('/api/watch-history/my');
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('관람기록 조회 실패:', error);
    throw error;
  }
}

/**
 * 관람기록 추가
 * @param {number} movieId - 영화 ID
 * @param {object} data - 관람 데이터
 */
export async function addWatchHistory(movieId, data) {
  try {
    const response = await fetch('/api/watch-history', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ movieId, ...data }),
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('관람기록 추가 실패:', error);
    throw error;
  }
}

/**
 * 관람기록 삭제
 * @param {number} historyId - 관람기록 ID
 */
export async function deleteWatchHistory(historyId) {
  try {
    const response = await fetch(`/api/watch-history/${historyId}`, {
      method: 'DELETE',
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.ok;
  } catch (error) {
    console.error('관람기록 삭제 실패:', error);
    throw error;
  }
}
