package com.partyguham.domain.catalog.reader;

import com.partyguham.domain.catalog.entity.Position;
import com.partyguham.domain.catalog.exception.CatalogErrorCode;
import com.partyguham.domain.catalog.repository.PositionRepository;
import com.partyguham.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PositionReader {
    private final PositionRepository positionRepository;

    public Position read(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(CatalogErrorCode.POSITION_NOT_FOUND));
    }

    public List<Position> readByMain(String main) {
        List<Position> positions;

        if (main == null || main.isBlank()) {
            positions = positionRepository.findAll();
        } else {
            positions = positionRepository.findByMain(main);
        }

        if (positions.isEmpty()) {
            throw new BusinessException(CatalogErrorCode.POSITION_NOT_FOUND);
        }

        return positions;
    }
}
