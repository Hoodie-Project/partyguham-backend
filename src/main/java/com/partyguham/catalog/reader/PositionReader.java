package com.partyguham.catalog.reader;

import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.exception.CatalogErrorCode;
import com.partyguham.catalog.repository.PositionRepository;
import com.partyguham.common.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PositionReader {
    private final PositionRepository positionRepository;

    public Position read(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.POSITION_NOT_FOUND));
    }
}
