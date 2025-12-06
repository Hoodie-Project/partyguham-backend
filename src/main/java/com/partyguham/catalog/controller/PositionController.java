package com.partyguham.catalog.controller;

import com.partyguham.catalog.dto.response.PositionResponse;
import com.partyguham.catalog.service.PositionService;
import com.partyguham.common.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/positions")
public class PositionController {

    private final PositionService positionService;

    @GetMapping
    public List<PositionResponse> getByMain(@RequestParam String main) {
        return positionService.getByMain(main);
    }
}