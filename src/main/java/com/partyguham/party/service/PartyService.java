package com.partyguham.party.service;

import com.partyguham.common.entity.Status;
import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.repository.PositionRepository;
import com.partyguham.infra.s3.S3FileService;
import com.partyguham.infra.s3.S3Folder;
import com.partyguham.notification.event.PartyMemberLeftEvent;
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
import com.partyguham.recruitment.dto.response.PartyRecruitmentSearchDto;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.profile.repository.UserCareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyService {

    private final PartyRepository partyRepository;
    private final PartyTypeRepository partyTypeRepository;
    private final PositionRepository positionRepository;
    private final UserRepository userRepository;
    private final PartyUserRepository partyUserRepository;
    private final UserCareerRepository userCareerRepository;
    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final S3FileService s3FileService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PartyResponseDto createParty(PartyCreateRequestDto request, Long userId, MultipartFile image) {

        PartyType partyType = partyTypeRepository.findById(request.getPartyTypeId())
                .orElseThrow(() -> new PartyTypeNotFoundException(request.getPartyTypeId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Position position = positionRepository.findById(request.getPositionId())
                .orElseThrow(() -> new PositionNotFoundException(request.getPositionId()));

        String imageKey = null;
        if (image != null && !image.isEmpty()) {
            imageKey = s3FileService.upload(image, S3Folder.PARTY);
        }

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

        return PartyResponseDto.from(party);
    }

    public GetPartiesResponseDto getParties(GetPartiesRequestDto request) { // 파티 목록 조회
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(request.getOrder(), request.getSort())
        );

        Page<Party> page = partyRepository.searchParties(request, pageable);

        List<PartiesDto> parties = page.getContent().stream()
                .map(PartiesDto::from)
                .toList();

        return GetPartiesResponseDto.fromPage(page.getTotalElements(), parties);
    }

    public GetPartyResponseDto getParty(Long partyId) { // 파티 단일 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException());

        return GetPartyResponseDto.from(party);
    }

    public GetPartyUserResponseDto getPartyUsers(GetPartyUsersRequestDto request, Long partyId) { // 파티원 목록 조회

        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException());

        // 기본값 적용
        request.applyDefaultValues();

        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize()
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

        return GetPartyUserResponseDto.from(partyAdmin, partyUserList);
    }

    public PartyAuthorityResponseDto getPartyAuthority(Long partyId, Long userId) { // 나의 파티 권한 조회

        partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException());

        // PartyUser 조회
        PartyUser partyUser = partyUserRepository.findByPartyIdAndUserId(partyId, userId)
                .orElseThrow(() -> new PartyUserNotFoundException(partyId, userId));

        return PartyAuthorityResponseDto.from(partyUser);
    }

    public PartyTypeResponseDto getPartyTypes() { // 파티 타입 목록 조회
        List<PartyType> partyTypes = partyTypeRepository.findAll();

        return PartyTypeResponseDto.from(partyTypes);
    }

    @Transactional
    public void leaveParty(Long partyId, Long userId) { // 파티 나가기

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException());

        PartyUser leftUser = partyUserRepository.findByPartyIdAndUserId(partyId, userId)
                .orElseThrow(() -> new PartyUserNotFoundException(partyId, userId));

        if (leftUser.getAuthority() == PartyAuthority.MASTER) {
            throw new PartyAccessDeniedException("파티장은 파티를 나갈 수 없습니다.");
        }

        // 소프트 삭제: status를 DELETED로 변경
        leftUser.setStatus(Status.DELETED);

        // 이벤트 발행
        List<PartyUser> members = partyUserRepository
                .findByParty_IdAndStatus(partyId, Status.ACTIVE);

        for (PartyUser member : members) {
            PartyMemberLeftEvent event = PartyMemberLeftEvent.builder()
                    .partyUserId(member.getUser().getId())
                    .userNickname(leftUser.getUser().getNickname())
                    .partyId(party.getId())
                    .partyTitle(party.getTitle())
                    .partyImage(party.getImage())
                    .fcmToken(member.getUser().getFcmToken())
                    .build();

            eventPublisher.publishEvent(event);
        }
    }

    public GetSearchResponseDto searchParties(int page, int size, String titleSearch) { // 파티/모집공고 통합검색
        Pageable pageable = PageRequest.of(page - 1, size);

        // Party 검색
        Page<Party> partyPage = partyRepository.findByTitleKeyword(titleSearch, pageable);
        List<PartiesDto> partyListDto = partyPage.getContent().stream()
                .map(PartiesDto::from)
                .toList();

        // 조회된 파티들의 ID로 모집공고 조회
        List<Long> partyIds = partyPage.getContent().stream()
                .map(Party::getId)
                .toList();

        Page<PartyRecruitment> recruitmentPage = partyIds.isEmpty()
                ? Page.empty(pageable)
                : partyRecruitmentRepository.findByPartyIdIn(partyIds, pageable);
        List<PartyRecruitmentSearchDto> recruitmentListDto = recruitmentPage.getContent().stream()
                .map(PartyRecruitmentSearchDto::from)
                .toList();

        return GetSearchResponseDto.from(
                partyPage.getTotalElements(),
                partyListDto,
                recruitmentPage.getTotalElements(),
                recruitmentListDto
        );
    }

    @Transactional(readOnly = true)
    public UserJoinedPartyResponseDto getByNickname(String nickname) {

        List<PartyUser> list =
                partyUserRepository.findByUserNickname(nickname);

        List<UserJoinedPartyResponseDto.PartyUserItem> items =
                list.stream().map(pu ->
                        UserJoinedPartyResponseDto.PartyUserItem.builder()
                                .id(pu.getId())
                                .createdAt(pu.getCreatedAt().toString())
                                .position(UserJoinedPartyResponseDto.PositionDto.builder()
                                        .main(pu.getPosition().getMain())
                                        .sub(pu.getPosition().getSub())
                                        .build())
                                .party(UserJoinedPartyResponseDto.PartyDto.builder()
                                        .id(pu.getParty().getId())
                                        .title(pu.getParty().getTitle())
                                        .image(pu.getParty().getImage())
                                        .partyStatus(pu.getParty().getPartyStatus())
                                        .partyType(UserJoinedPartyResponseDto.PartyTypeDto.builder()
                                                .type(pu.getParty().getPartyType().getType())
                                                .build())
                                        .build())
                                .build()
                ).toList();

        return UserJoinedPartyResponseDto.builder()
                .total(items.size())
                .partyUsers(items)
                .build();
    }
}
