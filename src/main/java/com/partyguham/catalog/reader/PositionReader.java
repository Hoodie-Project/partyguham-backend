package com.partyguham.catalog.reader;

import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.exception.CatalogErrorCode;
import com.partyguham.catalog.repository.PositionRepository;
import com.partyguham.common.exception.BusinessException;
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

    /**
     * main 값이 Position에 존재하는지 검증
     */
    public void validateMainPositionExists(String main) {
        if (main != null && !main.isBlank() && positionRepository.findByMain(main).isEmpty()) {
            throw new BusinessException(CatalogErrorCode.INVALID_MAIN_POSITION);
        }
    }
}
