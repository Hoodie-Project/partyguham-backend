package com.partyguham.domain.catalog.controller;

import com.partyguham.domain.catalog.entity.Location;
import com.partyguham.domain.catalog.service.LocationService;
import com.partyguham.global.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public List<Location> findByProvince(@RequestParam(name = "province", required = false) String province) {
        return locationService.getByProvince(province); // 특정 조회
    }
}