package com.example.demo.controller;

import com.example.demo.config.SwaggerConfig;
import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateRatingRequest;
import com.example.demo.dto.RatingResponse;
import com.example.demo.dto.UpdateRatingRequest;
import com.example.demo.interceptor.AuthInterceptor;
import com.example.demo.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "평점", description = "영화 평점 등록, 수정, 삭제, 조회 API")
public class RatingController {

    private final RatingService ratingService;

    @Operation(
            summary = "회원별 평점 조회",
            description = "특정 회원이 작성한 평점 목록을 조회합니다. 토큰 없이 호출할 수 있는 공개 API입니다."
    )
    @GetMapping("/members/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(
            @PathVariable("memberId") Long memberId
    ) {
        return ratingService.getRatingsByMember(memberId);
    }

    @Operation(
            summary = "평점 등록",
            description = "로그인한 회원이 영화에 평점과 후기를 등록합니다. 토큰이 필요합니다. 실패: 401 AUTH401_2(토큰 없음), 409 RATING409_1(같은 영화에 이미 평점 등록)."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @PostMapping("/ratings")
    public ResponseEntity<ApiResponse<RatingResponse>> create(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody CreateRatingRequest request
    ) {
        RatingResponse response = ratingService.create(memberId, request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created(
                        "평점을 등록했습니다.",
                        response
                ));
    }

    @Operation(
            summary = "평점 수정",
            description = "로그인한 회원이 본인이 작성한 평점의 별점과 후기를 수정합니다. 토큰이 필요합니다. 실패: 401 AUTH401_2(토큰 없음), 403 RATING403_1(다른 회원의 평점)."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @PatchMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<RatingResponse>> update(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable("ratingId") Long ratingId,
            @Valid @RequestBody UpdateRatingRequest request
    ) {
        RatingResponse response = ratingService.update(
                memberId,
                ratingId,
                request
        );

        return ResponseEntity.ok(
                ApiResponse.ok("평점을 수정했습니다.", response)
        );
    }

    @Operation(
            summary = "평점 삭제",
            description = "로그인한 회원이 본인이 작성한 평점을 삭제합니다. 토큰이 필요합니다. 실패: 401 AUTH401_2(토큰 없음), 403 RATING403_1(다른 회원의 평점)."
    )
    @SecurityRequirement(name = SwaggerConfig.BEARER_AUTH)
    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @PathVariable("ratingId") Long ratingId
    ) {
        ratingService.delete(memberId, ratingId);

        return ResponseEntity.ok(
                ApiResponse.ok("평점을 삭제했습니다.", null)
        );
    }
}
