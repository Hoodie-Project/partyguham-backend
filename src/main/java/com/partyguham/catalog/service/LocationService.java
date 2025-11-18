package com.partyguham.catalog.service;

import com.partyguham.catalog.entity.Location;
import com.partyguham.catalog.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<Location> getByProvince(String province) {
        return locationRepository.findByProvince(province);
    }
}