package com.partyguham.catalog.reader;

import com.partyguham.catalog.entity.Location;
import com.partyguham.catalog.exception.CatalogErrorCode;
import com.partyguham.catalog.repository.LocationRepository;
import com.partyguham.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LocationReader {
    private final LocationRepository locationRepository;

    public Location read(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.LOCATION_NOT_FOUND));
    }

    public List<Location> readByIds(List<Long> ids) {
        List<Location> locations = locationRepository.findAllById(ids);

        if (locations.size() != ids.size()) {
            throw new BusinessException(CatalogErrorCode.POSITION_NOT_FOUND);
        }

        return locations;
    }

    public List<Location> readByProvince(String province) {
        return locationRepository.findByProvince(province);
    }
}
