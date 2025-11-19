package com.partyguham.catalog.controller;


import com.partyguham.catalog.entity.Location;
import com.partyguham.catalog.service.LocationService;
import com.partyguham.common.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/province/{province}")
    public List<Location> findByProvince(@PathVariable String province) {
        return locationService.getByProvince(province);
    }
}