package com.partyguham.domain.user.account.dto.request;

import com.partyguham.domain.user.profile.entity.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class SignUpRequest {

//    @Schema(description = "닉네임 2자 이상 15자 이하", example = "nickname")
    @NotBlank(message = "{NotBlank.nickname}")
    @Size(min = 2, max = 15, message = "{Size.nickname}")
    private String nickname;

//    @Schema(description = "M: 남성, F: 여성, U: 미정", example = "M")
    @NotNull(message = "{NotNull.gender}")
    private Gender gender; // Enum으로 변경하여 @IsIn 효과를 자동으로 얻음

//    @Schema(description = "생년월일 (YYYY-MM-DD)", example = "2024-01-01")
    @NotNull(message = "{NotNull.birth}")
    @PastOrPresent(message = "{PastOrPresent.birth}") // 생일은 미래일 수 없음
    private LocalDate birth;
}