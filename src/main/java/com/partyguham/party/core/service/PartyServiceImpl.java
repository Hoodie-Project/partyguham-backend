package com.partyguham.party.core.service;

import com.partyguham.common.util.ImageUploader;
import com.partyguham.party.core.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.core.dto.party.request.PartyCreateRequestDto;
import com.partyguham.party.core.dto.party.response.*;
import com.partyguham.party.core.entity.Party;
import com.partyguham.party.core.entity.PartyType;
import com.partyguham.party.core.repository.PartyRepository;
import com.partyguham.party.core.repository.PartyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyServiceImpl implements PartyService {

    private final PartyRepository partyRepository;
    private final PartyTypeRepository partyTypeRepository;
    private final ImageUploader imageUploader;

    @Override
    @Transactional
    public PartyResponseDto createParty(PartyCreateRequestDto request, Long userId) {
        PartyType partyType = partyTypeRepository.findById(request.getPartyTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Party Type이 존재하지 않습니다: " + request.getPartyTypeId()));

        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = imageUploader.upload(request.getImage());
        }

        Party party = Party.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .partyType(partyType)
                .image(imageUrl)
                .build();

        partyRepository.save(party);

        // TODO: PartyUser 생성 (포지션사용)
        return null;
    }

    @Override
    public GetPartiesResponseDto getParties(GetPartiesRequestDto request) {
        return null;
    }

    @Override
    public GetPartyResponseDto getParty(Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("파티를 찾을 수 없습니다: " + partyId));

        // TODO: DTO 변환
        return null;
    }

    @Override
    public GetPartyUserResponseDto getPartyUsers(Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("파티를 찾을 수 없습니다: " + partyId));

        // TODO: PartyUser 조회
        // TODO: DTO 변환
        return null;
    }

    @Override
    public PartyAuthorityResponseDto getPartyAuthority(Long partyId, Long userId) {
        // 파티 존재 확인
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("파티를 찾을 수 없습니다: " + partyId));

        // TODO: PartyUser 조회 및 권한 확인
        // TODO: DTO 변환
        return null;
    }

    @Override
    public PartyTypeResponseDto getType() {
        List<PartyType> partyTypes = partyTypeRepository.findAll();

        // TODO: DTO 변환
        return null;
    }

    @Override
    public GetSearchResponseDto getSearch(int page, int limit, String titleSearch) {
        if (titleSearch == null || titleSearch.trim().isEmpty()) {
            return null;
        }

        List<Party> parties = partyRepository.findByTitleKeyword(titleSearch);

        return null;
    }

    @Override
    @Transactional
    public void leaveParty(Long partyId, Long userId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("파티를 찾을 수 없습니다: " + partyId));

    }

    @Override
    public PartyTypeResponseDto getPartyTypes() {
        return null;
    }

    @Override
    public GetSearchResponseDto searchParties(int page, int limit, String titleSearch) {
        return null;
    }
}