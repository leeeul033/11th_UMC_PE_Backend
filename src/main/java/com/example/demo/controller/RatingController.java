package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.CreateRatingRequest;
import com.example.demo.dto.RatingResponse;
import com.example.demo.dto.UpdateRatingRequest;
import com.example.demo.interceptor.AuthInterceptor;
import com.example.demo.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/members/{memberId}/ratings")
    public List<RatingResponse> getMemberRatings(
            @PathVariable("memberId") Long memberId
    ) {
        return ratingService.getRatingsByMember(memberId);
    }

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