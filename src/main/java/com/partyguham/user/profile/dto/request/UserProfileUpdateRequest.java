package com.partyguham.user.profile.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserProfileUpdateRequest {

    @Pattern(regexp = "M|F|U", message = "gender must be M, F or U")
    private String gender;          // "M", "F", "U"
    private Boolean genderVisible;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    private Boolean birthVisible;

    private String portfolioTitle;
    @URL(message = "Invalid portfolio URL")
    private String portfolio;
}