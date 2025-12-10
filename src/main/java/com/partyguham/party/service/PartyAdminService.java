package com.partyguham.party.service;

import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.repository.PositionRepository;
import com.partyguham.common.entity.Status;
import com.partyguham.infra.s3.S3FileService;
import com.partyguham.infra.s3.S3Folder;
import com.partyguham.notification.event.PartyFinishedEvent;
import com.partyguham.notification.event.PartyInfoUpdatedEvent;
import com.partyguham.notification.event.PartyReopenedEvent;
import com.partyguham.party.dto.partyAdmin.mapper.PartyUserAdminMapper;
import com.partyguham.party.dto.partyAdmin.request.*;
import com.partyguham.party.dto.partyAdmin.response.*;
import com.partyguham.party.entity.*;
import com.partyguham.party.repository.PartyRepository;
import com.partyguham.party.repository.PartyTypeRepository;
import com.partyguham.party.repository.PartyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyAdminService {

    private final PartyAccessService partyAccessService;
    private final PartyUserAdminMapper partyUserAdminMapper;
    private final PartyUserRepository partyUserRepository;
    private final PartyRepository partyRepository;
    private final PartyTypeRepository partyTypeRepository;
    private final PositionRepository positionRepository;
    private final S3FileService s3FileService;
    private final ApplicationEventPublisher eventPublisher;


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

    @Transactional
    public UpdatePartyResponseDto updateParty(
            Long partyId,
            Long userId,
            UpdatePartyRequestDto request,
            MultipartFile image // 새 이미지, 없으면 null
    ) {
// 1) 권한 체크
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파티입니다. id=" + partyId));

        // 3) 타입 변경
        if (request.getPartyTypeId() != null) {
            PartyType partyType = partyTypeRepository.findById(request.getPartyTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파티 타입입니다. id=" + request.getPartyTypeId()));
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

    @Transactional
    public UpdatePartyStatusResponseDto updatePartyStatus(
            Long partyId,
            Long userId,
            UpdatePartyStatusRequestDto request
    ) {
        // 1) 권한 체크 (파티장/부파티장)
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파티입니다. id=" + partyId));

        // 3) 상태 변경
        party.setPartyStatus(request.partyStatus());

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

    @Transactional
    public void deletePartyImage(Long partyId, Long userId) {
        // 1) 권한 체크 (파티장/부파티장만)
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파티입니다. id=" + partyId));

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
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 조회
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 파티입니다. id=" + partyId));

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
        party.setStatus(Status.DELETED);

        // 5) 파티 관련 모집글 전부 삭제 처리
        if (party.getPartyRecruitments() != null) {
            party.getPartyRecruitments()
                    .forEach(r -> r.setStatus(Status.DELETED));
        }

        // 6) 파티원 이력도 삭제 처리
        if (party.getPartyUsers() != null) {
            party.getPartyUsers()
                    .forEach(pu -> pu.setStatus(Status.DELETED));
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


    public PartyDelegationResponseDto delegateParty(Long partyId,
                                                    Long userId,
                                                    PartyDelegationRequestDto request) {

        // 1) 요청자가 파티의 MASTER 인지 체크 (파티 삭제 때 썼던 메서드 재사용)
        partyAccessService.checkMasterOrThrow(partyId, userId);

        // 2) 파티 존재 확인 (optional이지만 방어적으로 한 번 더)
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 파티입니다. id=" + partyId));

        // 3) 현재 파티장 PartyUser 찾기
        PartyUser currentMaster = partyUserRepository
                .findByParty_IdAndUser_IdAndStatus(partyId, userId, Status.ACTIVE)
                .orElseThrow(() -> new IllegalStateException(
                        "현재 파티장 정보를 찾을 수 없습니다."));

        if (currentMaster.getAuthority() != PartyAuthority.MASTER) {
            throw new IllegalStateException("파티장만 권한을 위임할 수 있습니다.");
        }

        // 4) 위임 대상 파티원 찾기
        Long targetPartyUserId = request.getPartyUserId();

        PartyUser target = partyUserRepository
                .findByIdAndParty_IdAndStatus(targetPartyUserId, partyId, Status.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException(
                        "위임 대상 파티원을 찾을 수 없습니다. id=" + targetPartyUserId));

        if (target.getAuthority() == PartyAuthority.MASTER) {
            throw new IllegalStateException("이미 파티장인 멤버에게는 위임할 수 없습니다.");
        }

        if (target.getId().equals(currentMaster.getId())) {
            throw new IllegalArgumentException("자기 자신에게 파티장 권한을 위임할 수 없습니다.");
        }

        // 5) 권한 변경 로직
        // 지금은 DEPUTY 로직 안 쓰니까: MASTER → MEMBER, 대상 → MASTER
        currentMaster.setAuthority(PartyAuthority.MEMBER);
        target.setAuthority(PartyAuthority.MASTER);

        return PartyDelegationResponseDto.from(party, currentMaster, target);
    }

    @Transactional
    public void updatePartyUser(Long partyId,
                                Long partyUserId,
                                Long userId,
                                UpdatePartyUserRequestDto request) {

        // 1) 관리자 권한 체크 (MASTER or DEPUTY)
        partyAccessService.checkManagerOrThrow(partyId, userId);

        // 2) 파티원 조회
        PartyUser partyUser = partyUserRepository
                .findByIdAndParty_IdAndStatusNot(partyUserId, partyId, Status.DELETED)
                .orElseThrow(() -> new IllegalArgumentException(
                        "파티원을 찾을 수 없습니다. id=" + partyUserId
                ));

        // 3) 포지션 변경 (optional)
        if (request.getPositionId() != null) {
            Position position = positionRepository.findById(request.getPositionId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "포지션이 존재하지 않습니다. id=" + request.getPositionId()
                    ));
            partyUser.setPosition(position);
        }
    }


    public void deletePartyUser(Long partyId,
                                Long partyUserId,
                                Long userId) {

        // 1) 관리자 권한 체크 (파티장/부파티장)
        partyAccessService.checkManagerOrThrow(partyId, userId);

        // 2) 파티원 조회 (DELETED 제외)
        PartyUser target = partyUserRepository
                .findByIdAndParty_IdAndStatusNot(partyUserId, partyId, Status.DELETED)
                .orElseThrow(() -> new IllegalArgumentException(
                        "파티원을 찾을 수 없습니다. id=" + partyUserId
                ));

        // 3) 파티장 강퇴 방지 (규칙: MASTER는 이 API로 못 내보냄)
        if (target.getAuthority() == PartyAuthority.MASTER) {
            throw new IllegalStateException("파티장은 강제 퇴장시킬 수 없습니다.");
        }

        // 4) 소프트 삭제
        target.setStatus(Status.DELETED);
    }


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

        if (partyUsers.isEmpty()) {
            return;
        }

        // 3) 파티장 포함 여부 체크
        boolean hasMaster = partyUsers.stream()
                .anyMatch(pu -> pu.getAuthority() == PartyAuthority.MASTER);

        if (hasMaster) {
            throw new IllegalStateException("파티장은 배치 강제 퇴장시킬 수 없습니다.");
        }

        // 4) 모두 소프트 삭제
        partyUsers.forEach(pu -> pu.setStatus(Status.DELETED));
    }
}
