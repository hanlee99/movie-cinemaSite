// Movie API 호출 함수 모음

/**
 * 영화 목록 조회 (페이징)
 * @param {string} type - 'now-playing' | 'upcoming'
 * @param {number} page - 페이지 번호
 * @param {number} size - 페이지 크기
 */
export async function fetchMovieList(type, page = 0, size = 20) {
  const endpoint = type === 'now' ? '/api/movie/now-playing' : '/api/movie/upcoming';
  const url = `${endpoint}?page=${page}&size=${size}`;

  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('영화 목록 조회 실패:', error);
    throw error;
  }
}

/**
 * 영화 상세 정보 조회
 * @param {number} movieId - 영화 ID
 */
export async function fetchMovieDetail(movieId) {
  try {
    const response = await fetch(`/api/movie/${movieId}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('영화 상세 조회 실패:', error);
    throw error;
  }
}

/**
 * 영화 검색
 * @param {string} keyword - 검색 키워드
 */
export async function searchMovies(keyword) {
  try {
    const response = await fetch(`/search?keyword=${encodeURIComponent(keyword)}`);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error('영화 검색 실패:', error);
    throw error;
  }
}
