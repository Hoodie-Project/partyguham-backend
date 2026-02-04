package com.partyguham.domain.application.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class MessageResponse {
    private String message;
}