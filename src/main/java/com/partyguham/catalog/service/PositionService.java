package com.partyguham.catalog.service;

import com.partyguham.catalog.dto.response.PositionResponse;
import com.partyguham.catalog.reader.PositionReader;
import com.partyguham.catalog.repository.PositionRepository;
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