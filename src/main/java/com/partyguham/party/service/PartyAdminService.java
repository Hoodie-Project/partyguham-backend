package com.partyguham.party.service;

import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.reader.PositionReader;
import com.partyguham.common.entity.Status;
import com.partyguham.common.exception.BusinessException;
import com.partyguham.infra.s3.S3FileService;
import com.partyguham.infra.s3.S3Folder;
import com.partyguham.notification.event.*;
import com.partyguham.party.dto.partyAdmin.mapper.PartyUserAdminMapper;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import com.partyguham.party.entity.*;
import com.partyguham.party.reader.PartyReader;
import com.partyguham.party.reader.PartyUserReader;
import com.partyguham.party.repository.PartyUserRepository;
import com.partyguham.recruitment.entity.PartyRecruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.partyguham.party.exception.PartyUserErrorCode.PARTY_USER_KICK_MASTER_NOT_ALLOWED;

@Service
@RequiredArgsConstructor
public class PartyAdminService {

    private final PartyReader partyReader;
    private final PartyUserReader partyUserReader;
    private final PositionReader positionReader;

    private final ApplicationEventPublisher eventPublisher;

    private final S3FileService s3FileService;

    private final PartyUserAdminMapper partyUserAdminMapper;

    private final PartyUserRepository partyUserRepository;



    /**
     * 관리자용 파티원 목록 조회
     * - 파티장/부파티장 권한 필요
     * - 필터(authority, nickname, main) + 페이징 적용
     * - totalPartyUserCount(전체 인원) + total(필터 후 인원) 반환
     */
    @Transactional(readOnly = true)
    public GetAdminPartyUsersResponseDto getPartyUsers(
            Long partyId,
            GetAdminPartyUsersRequestDto request,
            Long userId
    ) {
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        // 2) 페이징 기본값 (0-based page)
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        // 3) 전체 파티원 수 (삭제 제외, 필터 X)
        long totalCount = partyUserRepository.countAllByPartyIdNotDeleted(partyId);

        // 4) 필터 + 페이징 적용된 결과 조회
        Page<PartyUser> pageResult =
                partyUserRepository.searchAdminPartyUsers(partyId, request, pageable);

        long total = pageResult.getTotalElements();

        // 5) 엔티티 → DTO 리스트 변환
        List<GetAdminPartyUsersResponseDto.AdminPartyUserDto> items =
                pageResult.getContent().stream()
                        .map(partyUserAdminMapper::toAdminDto)
                        .toList();

        // 6) 최종 응답 조립
        return GetAdminPartyUsersResponseDto.builder()
                .totalPartyUserCount(totalCount) // 파티 전체 인원 수
                .total(total)           // 필터 + 페이징 후 인원 수
                .partyUsers(items)
                .build();
    }

    @Transactional
    public UpdatePartyResponseDto updateParty(
            Long partyId,
            Long userId,
            UpdatePartyRequestDto request,
            MultipartFile image // 새 이미지, 없으면 null
    ) {
        // 1) 권한 체크
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        // 2) 파티 조회
        Party party = partyReader.readParty(partyId);
        // 3) 타입 변경
        if (request.getPartyTypeId() != null) {
            PartyType partyType = partyReader.readType(request.getPartyTypeId());
            party.setPartyType(partyType);
        }

        // 4) 제목/내용 수정
        if (request.getTitle() != null) {
            party.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            party.setContent(request.getContent());
        }

        // 5) 이미지 교체 (파일이 왔을 때만)
        if (image != null && !image.isEmpty()) {
            String oldKey = party.getImage();
            String newKey = s3FileService.upload(image, S3Folder.PARTY);

            party.setImage(newKey);

            if (oldKey != null && !oldKey.equals(newKey)) {
                s3FileService.delete(oldKey);
            }
        }

        // 이벤트 발행
        List<PartyUser> members = partyUserRepository
                .findByPartyIdAndStatus(partyId, Status.ACTIVE);

        for (PartyUser member : members) {
            PartyFinishedEvent event = PartyFinishedEvent.builder()
                    .partyId(party.getId())
                    .partyTitle(party.getTitle())
                    .partyUserId(member.getUser().getId())
                    .fcmToken(member.getUser().getFcmToken())
                    .build();

            eventPublisher.publishEvent(event);
        }


        return UpdatePartyResponseDto.from(party);
    }

    @Transactional
    public UpdatePartyStatusResponseDto updatePartyStatus(
            Long partyId,
            Long userId,
            UpdatePartyStatusRequestDto request
    ) {
        // 1) 권한 체크 (파티장/부파티장)
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        // 2) 파티 조회
        Party party = partyReader.readParty(partyId);

        // 3) 상태 변경
        party.setPartyStatus(request.partyStatus());

        // 이벤트 발행
        List<PartyUser> members = partyUserRepository
                .findByPartyIdAndStatus(partyId, Status.ACTIVE);

        if (request.partyStatus() == PartyStatus.CLOSED) {
            for (PartyUser member : members) {
                PartyInfoUpdatedEvent event = PartyInfoUpdatedEvent.builder()
                        .partyId(party.getId())
                        .partyTitle(party.getTitle())
                        .partyImage(party.getImage())
                        .partyUserId(member.getUser().getId())
                        .fcmToken(member.getUser().getFcmToken())
                        .build();

                eventPublisher.publishEvent(event);
            }
        }

        if (request.partyStatus() == PartyStatus.IN_PROGRESS) {
            for (PartyUser member : members) {
                PartyReopenedEvent event = PartyReopenedEvent.builder()
                        .partyId(party.getId())
                        .partyTitle(party.getTitle())
                        .partyImage(party.getImage())
                        .partyUserId(member.getUser().getId())
                        .fcmToken(member.getUser().getFcmToken())
                        .build();

                eventPublisher.publishEvent(event);
            }
        }

        // 4) 응답 DTO로 변환
        return UpdatePartyStatusResponseDto.from(party);
    }

    @Transactional
    public void deletePartyImage(Long partyId, Long userId) {
        // 1) 권한 체크 (파티장/부파티장만)
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        // 2) 파티 조회
        Party party = partyReader.readParty(partyId);
        // 3) 기존 이미지 키 가져오기
        String oldImageKey = party.getImage();
        if (oldImageKey == null || oldImageKey.isBlank()) {
            // 이미지가 원래 없으면 그냥 리턴
            return;
        }

        // 4) DB에서 먼저 끊어주기 (null 세팅)
        party.setImage(null);

        // 5) S3에서 실제 파일 삭제 - 삭제 실패시 어떻게 할지는 정책에 따라 (지금은 예외 그대로 던지도록)
        s3FileService.delete(oldImageKey);
    }


    /**
     * 파티 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteParty(Long partyId, Long userId) {
        // 1) 권한 체크 (파티장만)
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkMaster();

        // 2) 파티 조회
        Party party = partyReader.readParty(partyId);

        // 이미 삭제된 파티면 그냥 리턴
        if (party.getStatus() == Status.DELETED) {
            return;
        }

        // 3) 파티 대표 이미지 S3 삭제 (실패해도 롤백 안 되게)
        String oldImageKey = party.getImage();
        if (oldImageKey != null && !oldImageKey.isBlank()) {
            s3FileService.deleteSafely(oldImageKey);
        }

        // 4) 파티 자체 삭제 처리
        party.delete();

        // 5) 파티 관련 모집글 전부 삭제 처리
        if (party.getPartyRecruitments() != null) {
            party.getPartyRecruitments()
                    .forEach(PartyRecruitment::delete);
        }

        // 6) 파티원 이력도 삭제 처리
        if (party.getPartyUsers() != null) {
            party.getPartyUsers()
                    .forEach(PartyUser::delete);
        }

        // 7) 🆕 연관 지원내역(PartyApplication) 전체 삭제
        //    ※ Party → Recruitment → Applications 구조라면 아래처럼 처리
//        if (party.getPartyRecruitments() != null) {
//            party.getPartyRecruitments().forEach(rec -> {
//                if (rec.getApplications() != null) {
//                    rec.getApplications()
//                            .forEach(app -> app.setStatus(Status.DELETED));
//                }
//            });
//        }
    }

    @Transactional
    public PartyDelegationResponseDto delegateParty(Long partyId,
                                                    Long userId,
                                                    PartyDelegationRequestDto request) {

        // 요청자가 파티의 MASTER 인지 체크 (파티 삭제 때 썼던 메서드 재사용)
        // 권한 체크 (파티장만)
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkMaster();

        // 파티 조회
        Party party = partyReader.readParty(partyId);

        // 위임 대상 파티원 찾기
        Long targetPartyUserId = request.getPartyUserId();

        PartyUser target = partyUserReader.readByPartyAndUser(targetPartyUserId, userId);

        // 권한 변경
        partyUser.delegateTo(target);

        // 이벤트 발행
        List<PartyUser> members = partyUserRepository
                .findByPartyIdAndStatus(partyId, Status.ACTIVE);

        for (PartyUser member : members) {
            PartyLeaderChangedEvent event = PartyLeaderChangedEvent.builder()
                    .partyUserId(member.getUser().getId())
                    .userNickname(target.getUser().getNickname())
                    .partyId(party.getId())
                    .partyTitle(party.getTitle())
                    .partyImage(party.getImage())
                    .fcmToken(member.getUser().getFcmToken())
                    .build();

            eventPublisher.publishEvent(event);
        }

        return PartyDelegationResponseDto.from(party, partyUser, target);
    }

    @Transactional
    public void updatePartyUser(Long partyId,
                                Long partyUserId,
                                Long userId,
                                UpdatePartyUserRequestDto request) {
        // 1) 관리자 권한 체크 (MASTER or DEPUTY)
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        // 2) 파티원 조회
        PartyUser updatePartyUser = partyUserReader.readByPartyAndUser(partyId, userId);

        // 3) 포지션 변경 (optional)
        Position position = positionReader.read(request.getPositionId());
        updatePartyUser.updatePosition(position);

        // 이벤트 발행
        Party party = partyReader.readParty(partyId);

        List<PartyUser> members = partyUserRepository
                .findByPartyIdAndStatus(partyId, Status.ACTIVE);

        for (PartyUser member : members) {
            PartyMemberPositionChangedEvent event = PartyMemberPositionChangedEvent.builder()
                    .partyUserId(member.getUser().getId())
                    .userNickname(partyUser.getUser().getNickname())
                    .position(position.getMain() + " " + position.getSub())
                    .partyId(party.getId())
                    .partyTitle(party.getTitle())
                    .partyImage(party.getImage())
                    .fcmToken(member.getUser().getFcmToken())
                    .build();

            eventPublisher.publishEvent(event);
        }
    }

    @Transactional
    public void deletePartyUser(Long partyId,
                                Long partyUserId,
                                Long userId) {
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        // 2) 파티원 조회
        PartyUser target = partyUserReader.readByPartyAndUser(partyUserId, partyId);
        target.checkMaster();
        target.delete();

        // 이벤트 발행
        Party party = partyReader.readParty(partyId);

        List<PartyUser> members = partyUserRepository
                .findByPartyIdAndStatus(partyId, Status.ACTIVE);

        for (PartyUser member : members) {
            PartyMemberKickedEvent event = PartyMemberKickedEvent.builder()
                    .partyUserId(member.getUser().getId())
                    .userNickname(target.getUser().getNickname())
                    .partyId(party.getId())
                    .partyTitle(party.getTitle())
                    .partyImage(party.getImage())
                    .fcmToken(member.getUser().getFcmToken())
                    .build();

            eventPublisher.publishEvent(event);
        }
    }


    @Transactional
    public void deletePartyUserBatch(Long partyId,
                                     Long userId,
                                     DeletePartyUsersBodyRequestDto request) {
        PartyUser partyUser = partyUserReader.readByPartyAndUser(partyId, userId);
        partyUser.checkManager();

        List<Long> ids = request.getPartyUserIds();

        // 2) 해당 파티 + 아직 삭제 안 된 파티원들 조회
        List<PartyUser> targets = partyUserRepository
                .findByPartyIdAndIdInAndStatus(partyId, ids, Status.ACTIVE);

        if (targets.isEmpty()) {
            return;
        }

        // 3) 파티장 포함 여부 체크
        if (targets.stream().anyMatch(PartyUser::isMaster)) {
            throw new BusinessException(PARTY_USER_KICK_MASTER_NOT_ALLOWED);
        }

        // 4) 모두 소프트 삭제
        targets.forEach(PartyUser::delete);
    }
}
