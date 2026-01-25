package com.partyguham.domain.catalog.reader;

import com.partyguham.domain.catalog.entity.Location;
import com.partyguham.domain.catalog.exception.CatalogErrorCode;
import com.partyguham.domain.catalog.repository.LocationRepository;
import com.partyguham.global.exception.BusinessException;
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

    /** null 값일 경우 전체 조회 */
    public List<Location> readByProvince(String province) {
        List<Location> locations;

        if (province == null || province.isBlank()) {
            // 1. 파라미터가 없으면 전체 조회
            locations = locationRepository.findAll();
        } else {
            // 2. 파라미터가 있으면 조건 조회
            locations = locationRepository.findByProvince(province);
        }

        if (locations.isEmpty()) {
            throw new BusinessException(CatalogErrorCode.POSITION_NOT_FOUND);
        }

        return locations;
    }
}
