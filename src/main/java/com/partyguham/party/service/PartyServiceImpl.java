package com.partyguham.party.service;

import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.repository.PositionRepository;
import com.partyguham.common.util.ImageUploader;
import com.partyguham.party.dto.party.request.GetPartiesRequestDto;
import com.partyguham.party.dto.party.request.GetPartyUsersRequestDto;
import com.partyguham.party.dto.party.request.PartyCreateRequestDto;
import com.partyguham.party.dto.party.response.*;
import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyAuthority;
import com.partyguham.party.entity.PartyType;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.exception.PartyAccessDeniedException;
import com.partyguham.party.exception.PartyNotFoundException;
import com.partyguham.party.exception.PartyTypeNotFoundException;
import com.partyguham.party.exception.PartyUserNotFoundException;
import com.partyguham.party.exception.PositionNotFoundException;
import com.partyguham.party.exception.UserNotFoundException;
import com.partyguham.party.repository.PartyRepository;
import com.partyguham.party.repository.PartyTypeRepository;
import com.partyguham.party.repository.PartyUserRepository;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
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
public class PartyServiceImpl /*extends S3FileService*/ implements PartyService  { //TODO: S3 이미지 업로드

    private final PartyRepository partyRepository;
    private final PartyTypeRepository partyTypeRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final PartyUserRepository partyUserRepository;
    private final UserCareerRepository userCareerRepository;
    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final ImageUploader imageUploader;
    //private final S3FileService s3FileService;

    @Override
    @Transactional
    public PartyResponseDto createParty(PartyCreateRequestDto request, Long userId, String imageKey) { // 파티 생성
        PartyType partyType = partyTypeRepository.findById(request.getPartyTypeId())
                .orElseThrow(() -> new PartyTypeNotFoundException(request.getPartyTypeId()));

        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new PositionNotFoundException(request.getPositionId()));


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Party party = Party.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .partyType(partyType)
                .image(imageKey)
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
    public GetPartiesResponseDto getParties(GetPartiesRequestDto request) { // 파티 목록 조회
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
    public GetPartyResponseDto getParty(Long partyId) { // 파티 단일 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        return GetPartyResponseDto.from(party);
    }

    @Override
    public GetPartyUserResponseDto getPartyUsers(GetPartyUsersRequestDto request, Long partyId) { // 파티원 목록 조회
        
        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

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
                .filter(partyUser -> partyUser.getAuthority() == PartyAuthority.MEMBER)
                .map(partyUser -> {
                    var userCareers = userCareerRepository.findByUser(partyUser.getUser());
                    return PartyUserDto.from(partyUser, userCareers);
                })
                .toList();

        return GetPartyUserResponseDto.builder()
                .partyAdmin(partyAdmin)
                .partyUser(partyUserList)
                .build();
    }

    @Override
    public PartyAuthorityResponseDto getPartyAuthority(Long partyId, Long userId) { // 나의 파티 권한 조회
        
        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        // PartyUser 조회
        PartyUser partyUser = partyUserRepository.findByPartyIdAndUserId(partyId, userId)
                .orElseThrow(() -> new PartyUserNotFoundException(partyId, userId));

        return PartyAuthorityResponseDto.from(partyUser);
    }

    @Override
    public PartyTypeResponseDto getPartyTypes() { // 파티 타입 목록 조회
        List<PartyType> partyTypes = partyTypeRepository.findAll();

        return PartyTypeResponseDto.from(partyTypes);
    }

    @Override
    @Transactional
    public void leaveParty(Long partyId, Long userId) { // 파티 나가기
        // 파티 존재 확인
        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));

        // PartyUser 조회 및 삭제
        PartyUser partyUser = partyUserRepository.findByPartyIdAndUserId(partyId, userId)
                .orElseThrow(() -> new PartyUserNotFoundException(partyId, userId));

        // 파티장은 파티를 나갈 수 없음
        if (partyUser.getAuthority() == PartyAuthority.MASTER) {
            throw new PartyAccessDeniedException("파티장은 파티를 나갈 수 없습니다.");
        }

        partyUserRepository.delete(partyUser);
    }

    @Override
    public GetSearchResponseDto searchParties(int page, int limit, String titleSearch) { // 파티/모집공고 통합검색
        Pageable pageable = PageRequest.of(page - 1, limit);

        // Party 검색
        Page<Party> partyPage = partyRepository.findByTitleKeyword(titleSearch, pageable);
        List<PartiesDto> partyListDto = partyPage.getContent().stream()
                .map(PartiesDto::from)
                .toList();

        // PartyRecruitment 검색
        Page<PartyRecruitment> recruitmentPage = partyRecruitmentRepository.findByTitleKeyword(titleSearch, pageable);
        List<PartyRecruitmentSearchDto> recruitmentListDto = recruitmentPage.getContent().stream()
                .map(PartyRecruitmentSearchDto::from)
                .toList();

        return GetSearchResponseDto.builder()
                .party(GetSearchResponseDto.PartySearchDto.builder()
                        .total(partyPage.getTotalElements())
                        .parties(partyListDto)
                        .build())
                .partyRecruitment(GetSearchResponseDto.PartyRecruitmentSearchResultDto.builder()
                        .total(recruitmentPage.getTotalElements())
                        .partyRecruitments(recruitmentListDto)
                        .build())
                .build();
    }
}
