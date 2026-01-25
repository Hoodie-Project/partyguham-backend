package com.partyguham.domain.user.profile.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.partyguham.domain.user.profile.entity.Gender;
import jakarta.validation.constraints.AssertTrue;
import org.hibernate.validator.constraints.URL;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserProfileUpdateRequest {

    private Gender gender;
    private Boolean genderVisible;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    private Boolean birthVisible;

    private String portfolioTitle;
    @URL(message = "Invalid portfolio URL")
    private String portfolio;

    @AssertTrue(message = "적어도 하나의 수정할 정보가 필요합니다.")
    public boolean isUpdatePresent() {
        return gender != null || genderVisible != null ||
                birth != null || birthVisible != null ||
                portfolioTitle != null || portfolio != null;
    }
}