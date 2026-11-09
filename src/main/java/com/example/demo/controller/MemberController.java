package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.EmailCheckResponse;
import com.example.demo.dto.MemberInfoResponse;
import com.example.demo.dto.MemberResponse;
import com.example.demo.dto.MemberUpdateRequest;
import com.example.demo.dto.NicknameCheckResponse;
import com.example.demo.dto.SignUpRequest;
import com.example.demo.interceptor.AuthInterceptor;
import com.example.demo.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> getMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId
    ) {
        MemberInfoResponse response = memberService.getMyInfo(memberId);

        return ResponseEntity.ok(
                ApiResponse.ok("회원 정보를 조회했습니다.", response)
        );
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<MemberInfoResponse>> updateMyInfo(
            @RequestAttribute(AuthInterceptor.MEMBER_ID) Long memberId,
            @Valid @RequestBody MemberUpdateRequest request
    ) {
        MemberInfoResponse response = memberService.updateMyInfo(
                memberId,
                request
        );

        return ResponseEntity.ok(
                ApiResponse.ok("회원 정보를 수정했습니다.", response)
        );
    }

    @GetMapping("/nickname/{nickname}")
    public NicknameCheckResponse checkNickname(
            @PathVariable("nickname") String nickname
    ) {
        return memberService.checkNickname(nickname);
    }

    @GetMapping("/email/{email}")
    public EmailCheckResponse checkEmail(
            @PathVariable("email") String email
    ) {
        return memberService.checkEmail(email);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        MemberResponse response = memberService.signUp(request);

        return ResponseEntity.status(201)
                .body(ApiResponse.created(
                        "회원가입에 성공했습니다.",
                        response
                ));
    }
}