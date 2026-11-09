package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.entity.Member;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.MemberRepository;
import com.example.demo.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.UNAUTHORIZED,
                        "AUTH401_1",
                        "이메일 또는 비밀번호가 올바르지 않습니다."
                ));

        if (!passwordEncoder.matches(
                request.password(),
                member.getPassword()
        )) {
            throw new ApiException(
                    HttpStatus.UNAUTHORIZED,
                    "AUTH401_1",
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        String accessToken = jwtUtil.createAccessToken(
                member.getMemberId(),
                member.getEmail()
        );

        return new LoginResponse(
                member.getNickname(),
                accessToken
        );
    }
}