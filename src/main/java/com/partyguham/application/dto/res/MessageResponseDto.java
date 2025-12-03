package com.partyguham.application.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class MessageResponseDto {
    private String message;
}