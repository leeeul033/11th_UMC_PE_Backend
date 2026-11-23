package com.example.demo.service;

import com.example.demo.dto.CreateRatingRequest;
import com.example.demo.dto.UpdateRatingRequest;
import com.example.demo.entity.Member;
import com.example.demo.entity.Rating;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.RatingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    @DisplayName("같은 영화에 평점을 중복 등록하면 RATING409_1 예외를 던진다")
    void createRatingWhenDuplicated() {
        // given
        Member member = createMember(1L, "challenger@umc.com", "영화조아");
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        given(ratingRepository.existsByMember_MemberIdAndMovieId(1L, 550L)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> ratingService.create(
                1L,
                new CreateRatingRequest(550L, 5, "재밌게 봤습니다.")
        )).isInstanceOfSatisfying(ApiException.class, exception -> {
            assertThat(exception.getCode()).isEqualTo("RATING409_1");
            assertThat(exception.getStatus().value()).isEqualTo(409);
        });
    }

    @Test
    @DisplayName("다른 회원의 평점을 수정하면 RATING403_1 예외를 던지고 저장하지 않는다")
    void updateRatingWhenNotOwner() {
        // given
        Member owner = createMember(2L, "owner@umc.com", "평점주인");
        Rating rating = Rating.builder()
                .member(owner)
                .movieId(550L)
                .score(4)
                .comment("원래 후기")
                .build();

        given(ratingRepository.findById(10L)).willReturn(Optional.of(rating));

        // when & then
        assertThatThrownBy(() -> ratingService.update(
                1L,
                10L,
                new UpdateRatingRequest(5, "수정 시도")
        )).isInstanceOfSatisfying(ApiException.class, exception -> {
            assertThat(exception.getCode()).isEqualTo("RATING403_1");
            assertThat(exception.getStatus().value()).isEqualTo(403);
        });

        verify(ratingRepository, never()).save(rating);
    }

    private Member createMember(Long memberId, String email, String nickname) {
        Member member = Member.builder()
                .email(email)
                .password("암호화된비밀번호")
                .nickname(nickname)
                .build();
        ReflectionTestUtils.setField(member, "memberId", memberId);
        return member;
    }
}
