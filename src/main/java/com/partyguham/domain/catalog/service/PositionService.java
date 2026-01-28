package com.partyguham.domain.catalog.service;

import com.partyguham.domain.catalog.dto.response.PositionResponse;
import com.partyguham.domain.catalog.reader.PositionReader;
import com.partyguham.domain.catalog.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PositionService {

    private final PositionReader positionReader;
    private final PositionRepository positionRepository;

    public List<PositionResponse> getByMain(String main) {
        return positionReader.readByMain(main).stream()
                .map(PositionResponse::from)
                .toList();
    }
}