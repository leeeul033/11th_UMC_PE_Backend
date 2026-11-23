package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberUpdateRequest(

        @Schema(description = "2자 이상 12자 이하의 변경할 닉네임", example = "영화왕제민")
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(
                min = 2,
                max = 12,
                message = "닉네임은 2자 이상 12자 이하여야 합니다."
        )
        String nickname

) {
}
