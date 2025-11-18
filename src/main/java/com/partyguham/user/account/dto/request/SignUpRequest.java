package com.partyguham.user.account.dto.request;

import com.partyguham.user.profile.entity.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class SignUpRequest {

    private String nickname;
    private Gender gender;  // "M" / "F" / "U" 등
    private LocalDate birth;   // "1998-01-01"
}