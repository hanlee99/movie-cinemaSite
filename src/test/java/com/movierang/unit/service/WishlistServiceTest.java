package com.movierang.unit.service;

import com.movierang.dto.wishlist.WishlistItemResponse;
import com.movierang.entity.MovieEntity;
import com.movierang.entity.MovieStats;
import com.movierang.entity.Wishlist;
import com.movierang.exception.MovieNotFoundException;
import com.movierang.repository.MovieRepository;
import com.movierang.repository.MovieStatsRepository;
import com.movierang.repository.WishlistRepository;
import com.movierang.service.WishlistService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * WishlistService 단위 테스트
 * - Mock을 사용한 비즈니스 로직 검증
 * - Repository 호출 검증
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistService 단위 테스트")
class WishlistServiceTest {

    @InjectMocks
    private WishlistService wishlistService;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieStatsRepository movieStatsRepository;

    @Nested
    @DisplayName("toggleWishlist 메서드")
    class ToggleWishlist {

        @Test
        @DisplayName("찜 추가 - 찜하지 않은 영화를 찜하면 true 반환")
        void 찜_추가_성공() {
            // given
            Long userId = 1L;
            Long movieId = 100L;

            given(movieRepository.existsById(movieId)).willReturn(true);
            given(wishlistRepository.findByUserIdAndMovieId(userId, movieId))
                    .willReturn(Optional.empty());
            given(movieStatsRepository.findById(movieId))
                    .willReturn(Optional.of(MovieStats.createDefault(movieId)));

            // when
            boolean result = wishlistService.toggleWishlist(userId, movieId);

            // then
            assertThat(result).isTrue();
            verify(wishlistRepository).save(any(Wishlist.class));
            verify(movieStatsRepository).save(any(MovieStats.class));
        }

        @Test
        @DisplayName("찜 취소 - 이미 찜한 영화를 다시 찜하면 false 반환")
        void 찜_취소_성공() {
            // given
            Long userId = 1L;
            Long movieId = 100L;
            Wishlist existingWishlist = Wishlist.builder()
                    .userId(userId)
                    .movieId(movieId)
                    .build();

            given(movieRepository.existsById(movieId)).willReturn(true);
            given(wishlistRepository.findByUserIdAndMovieId(userId, movieId))
                    .willReturn(Optional.of(existingWishlist));
            given(movieStatsRepository.findById(movieId))
                    .willReturn(Optional.of(MovieStats.createDefault(movieId)));

            // when
            boolean result = wishlistService.toggleWishlist(userId, movieId);

            // then
            assertThat(result).isFalse();
            verify(wishlistRepository).delete(existingWishlist);
            verify(movieStatsRepository).save(any(MovieStats.class));
        }

        @Test
        @DisplayName("존재하지 않는 영화 찜 시도 - MovieNotFoundException 발생")
        void 존재하지_않는_영화_찜_예외() {
            // given
            Long userId = 1L;
            Long movieId = 999L;

            given(movieRepository.existsById(movieId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> wishlistService.toggleWishlist(userId, movieId))
                    .isInstanceOf(MovieNotFoundException.class);

            verify(wishlistRepository, never()).save(any());
            verify(wishlistRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("isWishlisted 메서드")
    class IsWishlisted {

        @Test
        @DisplayName("찜한 영화 확인 - true 반환")
        void 찜한_영화_확인() {
            // given
            Long userId = 1L;
            Long movieId = 100L;

            given(wishlistRepository.existsByUserIdAndMovieId(userId, movieId))
                    .willReturn(true);

            // when
            boolean result = wishlistService.isWishlisted(userId, movieId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("찜하지 않은 영화 확인 - false 반환")
        void 찜하지_않은_영화_확인() {
            // given
            Long userId = 1L;
            Long movieId = 100L;

            given(wishlistRepository.existsByUserIdAndMovieId(userId, movieId))
                    .willReturn(false);

            // when
            boolean result = wishlistService.isWishlisted(userId, movieId);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getMyWishlist 메서드")
    class GetMyWishlist {

        @Test
        @DisplayName("찜 목록 조회 성공 - DTO 리스트 반환")
        void 찜_목록_조회_성공() {
            // given
            Long userId = 1L;

            Wishlist wishlist1 = createWishlist(1L, userId, 100L);
            Wishlist wishlist2 = createWishlist(2L, userId, 200L);
            List<Wishlist> wishlists = List.of(wishlist1, wishlist2);

            MovieEntity movie1 = createMovie(100L, "영화1", "poster1.jpg");
            MovieEntity movie2 = createMovie(200L, "영화2", "poster2.jpg");

            given(wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId))
                    .willReturn(wishlists);
            given(movieRepository.findAllById(List.of(100L, 200L)))
                    .willReturn(List.of(movie1, movie2));

            // when
            List<WishlistItemResponse> result = wishlistService.getMyWishlist(userId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTitle()).isEqualTo("영화1");
            assertThat(result.get(1).getTitle()).isEqualTo("영화2");
        }

        @Test
        @DisplayName("빈 찜 목록 조회 - 빈 리스트 반환")
        void 빈_찜_목록_조회() {
            // given
            Long userId = 1L;

            given(wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId))
                    .willReturn(List.of());

            // when
            List<WishlistItemResponse> result = wishlistService.getMyWishlist(userId);

            // then
            assertThat(result).isEmpty();
            verify(movieRepository, never()).findAllById(any());
        }

        @Test
        @DisplayName("삭제된 영화 필터링 - 존재하는 영화만 반환")
        void 삭제된_영화_필터링() {
            // given
            Long userId = 1L;

            Wishlist wishlist1 = createWishlist(1L, userId, 100L);
            Wishlist wishlist2 = createWishlist(2L, userId, 200L);  // 삭제된 영화
            List<Wishlist> wishlists = List.of(wishlist1, wishlist2);

            MovieEntity movie1 = createMovie(100L, "영화1", "poster1.jpg");
            // movie2는 DB에 없음 (삭제됨)

            given(wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId))
                    .willReturn(wishlists);
            given(movieRepository.findAllById(List.of(100L, 200L)))
                    .willReturn(List.of(movie1));  // movie1만 반환

            // when
            List<WishlistItemResponse> result = wishlistService.getMyWishlist(userId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getMovieId()).isEqualTo(100L);
        }
    }

    // 테스트 헬퍼 메서드
    private Wishlist createWishlist(Long id, Long userId, Long movieId) {
        return Wishlist.builder()
                .userId(userId)
                .movieId(movieId)
                .build();
    }

    private MovieEntity createMovie(Long id, String title, String poster) {
        return MovieEntity.builder()
                .id(id)
                .title(title)
                .poster(poster)
                .genre("액션")
                .repRlsDate("20240101")
                .build();
    }
}
