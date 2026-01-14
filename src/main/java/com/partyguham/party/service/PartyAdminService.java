package com.partyguham.party.service;

import com.partyguham.catalog.reader.PositionReader;
import com.partyguham.catalog.entity.Position;
import com.partyguham.common.entity.Status;
import com.partyguham.common.exception.BusinessException;
import com.partyguham.infra.s3.S3FileService;
import com.partyguham.infra.s3.S3Folder;
import com.partyguham.notification.event.*;
import com.partyguham.party.dto.partyAdmin.mapper.PartyUserAdminMapper;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import com.partyguham.party.entity.*;
import com.partyguham.party.exception.PartyUserErrorCode;
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

/**
 * ===========================
 *  파티 관리(Party Admin) 서비스
 * ===========================
 *
 * 파티장(MASTER) 또는 부파티장(DEPUTY) 권한이 필요한 파티 관리 기능을 제공합니다.
 * 주요 기능: 파티 정보 관리, 파티 상태 관리, 파티원 관리, 파티장 권한 위임, 파티 삭제
 */
@Service
@RequiredArgsConstructor
public class PartyAdminService {

    private final PartyReader partyReader;
    private final PartyUserReader partyUserReader;
    private final PositionReader positionReader;

    private final ApplicationEventPublisher eventPublisher;

    private final S3FileService s3FileService;
    private final PartyAccessService partyAccessService;

    private final PartyUserAdminMapper partyUserAdminMapper;

    private final PartyUserRepository partyUserRepository;


    /**
     * 관리자용 파티원 목록 조회
     * - 파티장/부파티장 권한 필요
     * - 필터(authority, nickname, main) + 페이징 적용
     * - totalPartyUserCount(전체 인원) + total(필터 후 인원) 반환
     *  
     * @param partyId 파티 ID
     * @param request 조회 요청 (필터, 페이징 정보)
     * @param userId 요청한 사용자 ID
     * @return 파티원 목록 및 통계 정보 (전체 인원 수, 필터 후 인원 수 포함)
     */
    @Transactional(readOnly = true)
    public GetAdminPartyUsersResponseDto getPartyUsers(
            Long partyId,
            GetAdminPartyUsersRequestDto request,
            Long userId
    ) {
        // 1) 권한 체크
        partyAccessService.checkManagerOrThrow(partyId, userId);

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

    /**
     * 파티 정보 수정
     *
     * @param partyId 파티 ID
     * @param userId 요청한 사용자 ID
     * @param request 수정 요청 정보 (제목, 내용, 타입)
     * @param image 새 이미지 파일 (선택사항, null 가능)
     * @return 수정된 파티 정보
     */
    @Transactional
    public UpdatePartyResponseDto updateParty(
            Long partyId,
            Long userId,
            UpdatePartyRequestDto request,
            MultipartFile image
    ) {
// 1) 권한 체크
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyReader.readParty(partyId);

        // 3) 타입 변경
        if (request.getPartyTypeId() != null) {
            PartyType partyType = partyReader.readType(request.getPartyTypeId());
            party.updatePartyType(partyType);
        }

        // 4) 제목/내용 수정
        if (request.getTitle() != null) {
            party.updateTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            party.updateContent(request.getContent());
        }

        // 5) 이미지 교체 (파일이 왔을 때만)
        if (image != null && !image.isEmpty()) {
            String oldKey = party.getImage();
            String newKey = s3FileService.upload(image, S3Folder.PARTY);

            party.updateImage(newKey);

            if (oldKey != null && !oldKey.equals(newKey)) {
                s3FileService.delete(oldKey);
            }
        }

        // 이벤트 발행
        List<PartyUser> members = partyUserRepository
                .findByParty_IdAndStatus(partyId, Status.ACTIVE);

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

    /**
     * 파티 상태 변경
     *
     * 파티 상태를 진행중(IN_PROGRESS) 또는 종료(CLOSED)로 변경합니다.
     * 상태 변경 시 파티원에게 알림 이벤트를 발행합니다.
     *
     * @param partyId 파티 ID
     * @param userId 요청한 사용자 ID
     * @param request 상태 변경 요청 (PartyStatus 포함)
     * @return 변경된 파티 상태 정보
     */
    @Transactional
    public UpdatePartyStatusResponseDto updatePartyStatus(
            Long partyId,
            Long userId,
            UpdatePartyStatusRequestDto request
    ) {
        // 1) 권한 체크 (파티장/부파티장)
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyReader.readParty(partyId);

        // 3) 상태 변경
        party.updatePartyStatus(request.partyStatus());

        // 이벤트 발행
        List<PartyUser> members = partyUserRepository
                .findByParty_IdAndStatus(partyId, Status.ACTIVE);

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

    /**
     * 파티 대표 이미지 삭제
     *
     * @param partyId 파티 ID
     * @param userId 요청한 사용자 ID
     */
    @Transactional
    public void deletePartyImage(Long partyId, Long userId) {
        // 1) 권한 체크 (파티장/부파티장만)
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyReader.readParty(partyId);

        // 3) 기존 이미지 키 가져오기
        String oldImageKey = party.getImage();
        if (oldImageKey == null || oldImageKey.isBlank()) {
            return;
        }

        // 4) DB에서 먼저 끊어주기 (null 세팅)
        party.removeImage();

        // 5) S3에서 실제 파일 삭제 - 삭제 실패시 어떻게 할지는 정책에 따라 (지금은 예외 그대로 던지도록)
        s3FileService.delete(oldImageKey);
    }


    /**
     * 파티 삭제 (소프트 삭제)
     *
     * 파티와 연관된 모집글, 파티원 이력을 함께 삭제 처리합니다.
     *
     * @param partyId 파티 ID
     * @param userId 요청한 사용자 ID
     */
    @Transactional
    public void deleteParty(Long partyId, Long userId) {
        // 1) 권한 체크 (파티장만)
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyReader.readParty(partyId);

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

    /**
     * 파티장 권한 위임
     *
     * 파티장 권한을 다른 파티원에게 위임합니다. 위임 시 파티원에게 알림 이벤트를 발행합니다.
     *
     * @param partyId 파티 ID
     * @param userId 요청한 사용자 ID (파티장)
     * @param request 위임 요청 (위임 대상 파티원 ID 포함)
     * @return 위임 결과 정보
     */
    @Transactional
    public PartyDelegationResponseDto delegateParty(Long partyId,
                                                    Long userId,
                                                    PartyDelegationRequestDto request) {

        // 1) 요청자가 파티의 MASTER 인지 체크 (파티 삭제 때 썼던 메서드 재사용)
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 존재 확인 (optional이지만 방어적으로 한 번 더)
        Party party = partyReader.readParty(partyId);

        // 3) 현재 파티장 PartyUser 찾기
        PartyUser currentMaster = partyUserReader.readByPartyIdAndUserIdAndStatus(partyId, userId, Status.ACTIVE);

        if (currentMaster.getAuthority() != PartyAuthority.MASTER) {
            throw new BusinessException(PartyUserErrorCode.PARTY_DELEGATION_NOT_ALLOWED);
        }

        // 4) 위임 대상 파티원 찾기
        Long targetPartyUserId = request.getPartyUserId();

        PartyUser target = partyUserReader.readByIdAndPartyIdAndStatus(targetPartyUserId, partyId, Status.ACTIVE);

        if (target.getAuthority() == PartyAuthority.MASTER) {
            throw new BusinessException(PartyUserErrorCode.PARTY_USER_ALREADY_MASTER);
        }

        if (target.getId().equals(currentMaster.getId())) {
            throw new BusinessException(PartyUserErrorCode.PARTY_USER_SELF_DELEGATION);
        }

        // 5) 권한 변경 로직
        // 지금은 DEPUTY 로직 안 쓰니까: MASTER → MEMBER, 대상 → MASTER
        currentMaster.updateAuthority(PartyAuthority.MEMBER);
        target.updateAuthority(PartyAuthority.MASTER);

        // 이벤트 발행
        List<PartyUser> members = partyUserRepository
                .findByParty_IdAndStatus(partyId, Status.ACTIVE);

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

        return PartyDelegationResponseDto.from(party, currentMaster, target);
    }

    /**
     * 파티원 정보 수정
     *
     * 파티원의 포지션을 변경합니다. 변경 시 파티원에게 알림 이벤트를 발행합니다.
     *
     * @param partyId 파티 ID
     * @param partyUserId 수정 대상 파티원 ID
     * @param userId 요청한 사용자 ID
     * @param request 수정 요청 정보 (포지션 ID 포함)
     */
    @Transactional
    public void updatePartyUser(Long partyId,
                                Long partyUserId,
                                Long userId,
                                UpdatePartyUserRequestDto request) {

        // 1) 관리자 권한 체크 (MASTER or DEPUTY)
        partyAccessService.checkManagerOrThrow(partyId, userId);

        // 2) 파티원 조회
        PartyUser partyUser = partyUserReader.readByIdAndPartyIdAndStatusNot(partyUserId, partyId, Status.DELETED);

        // 3) 포지션 변경
        Position position = positionReader.read(request.getPositionId());
        partyUser.updatePosition(position);


        // 이벤트 발행
        Party party = partyReader.readParty(partyId);

        List<PartyUser> members = partyUserRepository
                .findByParty_IdAndStatus(partyId, Status.ACTIVE);

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

    /**
     * 개별 파티원 강제 퇴장
     *
     * 파티원을 강제로 퇴장시킵니다. 파티장은 강제 퇴장시킬 수 없습니다.
     * 퇴장 시 파티원에게 알림 이벤트를 발행합니다.
     *
     * @param partyId 파티 ID
     * @param partyUserId 퇴장 대상 파티원 ID
     * @param userId 요청한 사용자 ID
     */
    @Transactional
    public void deletePartyUser(Long partyId,
                                Long partyUserId,
                                Long userId) {

        // 1) 관리자 권한 체크 (파티장/부파티장)
        partyAccessService.checkManagerOrThrow(partyId, userId);

        // 2) 파티원 조회 (DELETED 제외)
        PartyUser target = partyUserReader.readByIdAndPartyIdAndStatusNot(partyUserId, partyId, Status.DELETED);

        // 3) 파티장 강퇴 방지 (규칙: MASTER는 이 API로 못 내보냄)
        if (target.getAuthority() == PartyAuthority.MASTER) {
            throw new BusinessException(PartyUserErrorCode.PARTY_USER_KICK_MASTER_NOT_ALLOWED);
        }

        // 4) 소프트 삭제
        target.delete();

        // 이벤트 발행
        Party party = partyReader.readParty(partyId);

        List<PartyUser> members = partyUserRepository
                .findByParty_IdAndStatus(partyId, Status.ACTIVE);

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


    /**
     * 파티원 다수 강제 퇴장 (Batch)
     *
     * 여러 파티원을 한 번에 강제 퇴장시킵니다. 파티장은 포함할 수 없습니다.
     *
     * @param partyId 파티 ID
     * @param userId 요청한 사용자 ID
     * @param request 배치 삭제 요청 (파티원 ID 목록 포함)
     */
    @Transactional
    public void deletePartyUserBatch(Long partyId,
                                     Long userId,
                                     DeletePartyUsersBodyRequestDto request) {

        // 1) 관리자 권한 체크
        partyAccessService.checkManagerOrThrow(partyId, userId);

        List<Long> ids = request.getPartyUserIds();
        if (ids == null || ids.isEmpty()) {
            return; // 혹은 IllegalArgumentException 던져도 됨
        }

        // 2) 해당 파티 + 아직 삭제 안 된 파티원들 조회
        List<PartyUser> partyUsers = partyUserRepository
                .findByParty_IdAndIdInAndStatusNot(partyId, ids, Status.DELETED);

        // 3) 파티장 포함 여부 체크
        boolean hasMaster = partyUsers.stream()
                .anyMatch(pu -> pu.getAuthority() == PartyAuthority.MASTER);

        if (hasMaster) {
            throw new BusinessException(PartyUserErrorCode.PARTY_USER_BATCH_KICK_MASTER_NOT_ALLOWED);
        }

        // 4) 모두 소프트 삭제
        partyUsers.forEach(PartyUser::delete);
    }
}
