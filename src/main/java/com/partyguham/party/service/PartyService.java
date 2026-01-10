package com.partyguham.party.service;

import com.partyguham.catalog.reader.PositionReader;
import com.partyguham.common.entity.Status;
import com.partyguham.catalog.entity.Position;
import com.partyguham.infra.s3.S3FileService;
import com.partyguham.infra.s3.S3Folder;
import com.partyguham.notification.event.PartyMemberLeftEvent;
import com.partyguham.party.dto.party.PartiesDto;
import com.partyguham.party.dto.party.PartyUserDto;
import com.partyguham.party.dto.party.request.GetPartiesRequest;
import com.partyguham.party.dto.party.request.GetPartyUsersRequest;
import com.partyguham.party.dto.party.request.PartyCreateRequest;
import com.partyguham.party.dto.party.response.*;
import com.partyguham.party.entity.Party;
import com.partyguham.party.entity.PartyAuthority;
import com.partyguham.party.entity.PartyType;
import com.partyguham.party.entity.PartyUser;
import com.partyguham.party.reader.PartyReader;
import com.partyguham.party.reader.PartyUserReader;
import com.partyguham.party.repository.PartyRepository;
import com.partyguham.party.repository.PartyTypeRepository;
import com.partyguham.party.repository.PartyUserRepository;
import com.partyguham.recruitment.dto.response.PartyRecruitmentSearchDto;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.recruitment.repository.PartyRecruitmentRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.reader.UserReader;
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
public class PartyService  {

    private final UserReader userReader;
    private final PartyReader partyReader;
    private final PartyUserReader partyUserReader;
    private final PositionReader positionReader;

    private final PartyRepository partyRepository;
    private final PartyTypeRepository partyTypeRepository;
    private final PartyUserRepository partyUserRepository;
    private final UserCareerRepository userCareerRepository;
    private final PartyRecruitmentRepository partyRecruitmentRepository;
    private final S3FileService s3FileService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public PartyResponse createParty(PartyCreateRequest request, Long userId, MultipartFile image) {

        // 1) 파티 타입 조회 (존재 여부 확인)
        PartyType partyType = partyReader.readType(request.getPartyTypeId());


        // 2) 파티장 역할을 맡을 유저 조회
        User user = userReader.read(userId);

        // 3) 파티장 기본 포지션 조회
        Position position = positionReader.read(request.getPositionId());

        // 4) 이미지가 있으면 업로드 후 키 저장
        String imageKey = null;
        if (image != null && !image.isEmpty()) {
            imageKey = s3FileService.upload(image, S3Folder.PARTY);
        }

        // 5) 파티 엔티티 생성
        Party party = Party.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .partyType(partyType)
                .image(imageKey)
                .build();

        partyRepository.save(party);

        // 6) 파티장 PartyUser 생성
        PartyUser masterUser = PartyUser.builder()
                .party(party)
                .user(user)
                .position(position)
                .authority(PartyAuthority.MASTER)
                .build();

        partyUserRepository.save(masterUser);

        // 7) 결과 반환
        return PartyResponse.from(party);
    }

    public GetPartiesResponse getParties(GetPartiesRequest request) { // 파티 목록 조회
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(request.getOrder(), request.getSort())
        );

        Page<Party> page = partyRepository.searchParties(request, pageable);

        List<PartiesDto> parties = page.getContent().stream()
                .map(PartiesDto::from)
                .toList();

        return GetPartiesResponse.fromPage(page.getTotalElements(), parties);
    }


    public GetPartyResponse getParty(Long partyId) { // 파티 단일 조회
        Party party = partyReader.readParty(partyId);

        return GetPartyResponse.from(party);
    }

    public GetPartyUserResponse getPartyUsers(GetPartyUsersRequest request, Long partyId) { // 파티원 목록 조회
        Party party = partyReader.readParty(partyId);

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

        return GetPartyUserResponse.builder()
                .partyAdmin(partyAdmin)
                .partyUser(partyUserList)
                .build();
    }

    public PartyAuthorityResponse getPartyAuthority(Long partyId, Long userId) { // 나의 파티 권한 조회

        Party party = partyReader.readParty(partyId);

        PartyUser partyUser = partyUserReader.getMember(partyId, userId);

        return PartyAuthorityResponse.from(partyUser);
    }

    public PartyTypeResponse getPartyTypes() { // 파티 타입 목록 조회
        List<PartyType> partyTypes = partyTypeRepository.findAll();

        return PartyTypeResponse.from(partyTypes);
    }

    @Transactional
    public void leaveParty(Long partyId, Long userId) { // 파티 나가기
        // 파티 존재 확인
        Party party = partyReader.readParty(partyId);

        // PartyUser 조회 및 삭제
        PartyUser leftUser = partyUserReader.getMember(partyId, userId);

        leftUser.leave();

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

    public GetSearchResponse searchParties(int page, int limit, String titleSearch) { // 파티/모집공고 통합검색
        Pageable pageable = PageRequest.of(page - 1, limit);

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

        return GetSearchResponse.builder()
                .party(GetSearchResponse.PartySearchDto.builder()
                        .total(partyPage.getTotalElements())
                        .parties(partyListDto)
                        .build())
                .partyRecruitment(GetSearchResponse.PartyRecruitmentSearchResultDto.builder()
                        .total(recruitmentPage.getTotalElements())
                        .partyRecruitments(recruitmentListDto)
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public UserJoinedPartyResponse getByNickname(String nickname) {

        List<PartyUser> list =
                partyUserRepository.findByUserNickname(nickname);

        List<UserJoinedPartyResponse.PartyUserItem> items =
                list.stream().map(pu ->
                        UserJoinedPartyResponse.PartyUserItem.builder()
                                .id(pu.getId())
                                .createdAt(pu.getCreatedAt().toString())
                                .position(UserJoinedPartyResponse.PositionDto.builder()
                                        .main(pu.getPosition().getMain())
                                        .sub(pu.getPosition().getSub())
                                        .build())
                                .party(UserJoinedPartyResponse.PartyDto.builder()
                                        .id(pu.getParty().getId())
                                        .title(pu.getParty().getTitle())
                                        .image(pu.getParty().getImage())
                                        .partyStatus(pu.getParty().getPartyStatus())
                                        .partyType(UserJoinedPartyResponse.PartyTypeDto.builder()
                                                .type(pu.getParty().getPartyType().getType())
                                                .build())
                                        .build())
                                .build()
                ).toList();

        return UserJoinedPartyResponse.builder()
                .total(items.size())
                .partyUsers(items)
                .build();
    }
}
