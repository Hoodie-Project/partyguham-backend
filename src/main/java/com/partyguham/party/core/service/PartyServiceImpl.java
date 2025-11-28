package com.partyguham.party.core.service;

import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.repository.PositionRepository;
import com.partyguham.common.util.ImageUploader;
import com.partyguham.party.core.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.core.dto.party.request.GetPartyUsersRequestDto;
import com.partyguham.party.core.dto.party.request.PartyCreateRequestDto;
import com.partyguham.party.core.dto.party.response.*;
import com.partyguham.party.core.entity.Party;
import com.partyguham.party.core.entity.PartyAuthority;
import com.partyguham.party.core.entity.PartyType;
import com.partyguham.party.core.entity.PartyUser;
import com.partyguham.party.core.repository.PartyRepository;
import com.partyguham.party.core.repository.PartyTypeRepository;
import com.partyguham.party.core.repository.PartyUserRepository;
import com.partyguham.party.recruitment.entity.PartyRecruitment;
import com.partyguham.party.recruitment.repository.PartyRecruitmentRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.profile.repository.UserCareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyServiceImpl implements PartyService { //TODO: 예외처리필요

    private final PartyRepository partyRepository;
    private final PartyTypeRepository partyTypeRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final PartyUserRepository partyUserRepository;
    private final UserCareerRepository userCareerRepository;
    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final ImageUploader imageUploader;

    @Override
    @Transactional
    public PartyResponseDto createParty(PartyCreateRequestDto request, Long userId) {
        PartyType partyType = partyTypeRepository.findById(request.getPartyTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Party Type이 존재하지 않습니다: " + request.getPartyTypeId()));

        Position position = positionRepository.findById(request.getPositionId())
            .orElseThrow(() -> new IllegalArgumentException("Position이 존재하지 않습니다: " + request.getPositionId()));
        
        
         User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User가 존재하지 않습니다: " + userId));

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

        PartyUser masterUser = PartyUser.builder()
            .party(party)
            .user(user)
            .position(position)                   
            .authority(PartyAuthority.MASTER)
            .build();

        partyUserRepository.save(masterUser);

        return PartyResponseDto.of(party);
    }

    @Override
    public GetPartiesResponseDto getParties(GetPartiesRequestDto request) {
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getLimit(),
                Sort.by(
                        request.getOrder().equalsIgnoreCase("ASC")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        request.getSort()
                )
        );

        Page<Party> page = partyRepository.searchParties(request, pageable);

        List<PartiesDto> parties = page.getContent().stream()
                .map(PartiesDto::from)
                .toList();

        GetPartiesResponseDto response = GetPartiesResponseDto.builder()
                .total(page.getTotalElements())
                .parties(parties)
                .build();

        return response;
    }

    @Override
    public GetPartyResponseDto getParty(Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("파티를 찾을 수 없습니다: " + partyId));

        return GetPartyResponseDto.from(party);
    }

    @Override
    public GetPartyUserResponseDto getPartyUsers(GetPartyUsersRequestDto request, Long partyId) {
        // 파티 존재 확인
        partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("파티를 찾을 수 없습니다: " + partyId));

        // 기본값 적용
        request.applyDefaultValues();

        Pageable pageable = PageRequest.of(
            request.getPage() - 1,
            request.getLimit()
        );

        Page<PartyUser> page = partyUserRepository.findPartyUsers(
                partyId,
                request.getMain(),
                request.getNickname(),
                request.getSort(),
                request.getOrder(),
                pageable
        );

        // 권한별로 분리하고 DTO 변환 (UserCareer 포함)
        List<PartyUserDto> partyAdmin = page.getContent().stream()
                .filter(partyUser -> partyUser.getAuthority() == PartyAuthority.MASTER ||
                                    partyUser.getAuthority() == PartyAuthority.DEPUTY)
                .map(partyUser -> {
                    var userCareers = userCareerRepository.findByUser(partyUser.getUser());
                    return PartyUserDto.from(partyUser, userCareers);
                })
                .toList();

        List<PartyUserDto> partyUserList = page.getContent().stream()
                .filter(pu -> pu.getAuthority() == PartyAuthority.MEMBER)
                .map(pu -> {
                    var userCareers = userCareerRepository.findByUser(pu.getUser());
                    return PartyUserDto.from(pu, userCareers);
                })
                .toList();

        return GetPartyUserResponseDto.builder()
                .partyAdmin(partyAdmin)
                .partyUser(partyUserList)
                .build();
    }

    @Override
    public PartyAuthorityResponseDto getPartyAuthority(Long partyId, Long userId) {
        // 파티 존재 확인
        partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("파티를 찾을 수 없습니다: " + partyId));

        // PartyUser 조회
        PartyUser partyUser = partyUserRepository.findByPartyIdAndUserId(partyId, userId)
                .orElseThrow(() -> new IllegalArgumentException("파티원을 찾을 수 없습니다. 파티 ID: " + partyId + ", 사용자 ID: " + userId));

        return PartyAuthorityResponseDto.from(partyUser);
    }

    @Override
    public PartyTypeResponseDto getType() {
        List<PartyType> partyTypes = partyTypeRepository.findAll();

        return PartyTypeResponseDto.from(partyTypes);
    }

    @Override
    public GetSearchResponseDto getSearch(int page, int limit, String titleSearch) {
    

        Pageable pageable = PageRequest.of(page - 1, limit);

        // Party 검색
        Page<Party> partyPage = partyRepository.findByTitleKeyword(titleSearch, pageable);
        List<PartiesDto> partyDtos = partyPage.getContent().stream()
                .map(PartiesDto::from)
                .toList();

        // PartyRecruitment 검색
        Page<PartyRecruitment> recruitmentPage = partyRecruitmentRepository.findByTitleKeyword(titleSearch, pageable);
        List<PartyRecruitmentSearchDto> recruitmentDtos = recruitmentPage.getContent().stream()
                .map(PartyRecruitmentSearchDto::from)
                .toList();

        return GetSearchResponseDto.builder()
                .party(GetSearchResponseDto.PartySearchDto.builder()
                        .total(partyPage.getTotalElements())
                        .parties(partyDtos)
                        .build())
                .partyRecruitment(GetSearchResponseDto.PartyRecruitmentSearchResultDto.builder()
                        .total(recruitmentPage.getTotalElements())
                        .partyRecruitments(recruitmentDtos)
                        .build())
                .build();
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