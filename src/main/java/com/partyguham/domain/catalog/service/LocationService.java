package com.partyguham.domain.catalog.service;

import com.partyguham.domain.catalog.entity.Location;
import com.partyguham.domain.catalog.reader.LocationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationReader locationReader;

    public List<Location> getByProvince(String province) {

        return locationReader.readByProvince(province);
    }
}