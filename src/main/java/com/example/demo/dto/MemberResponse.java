package com.example.demo.dto;

public record MemberResponse(
        Long memberId,
        String email,
        String nickname
) {
}