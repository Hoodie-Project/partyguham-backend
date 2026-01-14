package com.partyguham.application.entity;

import com.partyguham.common.entity.BaseEntity;
import com.partyguham.common.exception.BusinessException;
import com.partyguham.recruitment.entity.PartyRecruitment;
import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static com.partyguham.application.exception.ApplicationErrorCode.*;

@Entity
@Table(name = "party_applications")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SequenceGenerator(
        name = "party_application_id_seq_gen",
        sequenceName = "party_application_id_seq",
        allocationSize = 50
)
public class PartyApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_application_id_seq_gen")
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_recruitment_id", nullable = false)
    private PartyRecruitment partyRecruitment;

    @Column(columnDefinition = "text")
    private String message;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "application_status", nullable = false)
    private PartyApplicationStatus applicationStatus = PartyApplicationStatus.PENDING;

    /**
     * 검토 중(PENDING) 상태인지 확인
     */
    public void validatePendingStatus() {
        if (this.applicationStatus != PartyApplicationStatus.PENDING) {
            throw new BusinessException(INVALID_APPLICATION_STATUS);
        }
    }

    private void validateStatus(PartyApplicationStatus required) {
        if (this.applicationStatus != required) {
            throw new BusinessException(INVALID_APPLICATION_STATUS);
        }
    }

    /** 파티장이 승인(PENDING -> PROCESSING) */
    public void approveByManager() {
        validateStatus(PartyApplicationStatus.PENDING);
        this.applicationStatus = PartyApplicationStatus.PROCESSING;
    }

    /** 파티장이 거절(PENDING -> REJECTED) */
    public void rejectByManager() {
        validateStatus(PartyApplicationStatus.PENDING);
        this.applicationStatus = PartyApplicationStatus.REJECTED;
    }

    /** 지원자가 최종 수락(PROCESSING -> APPROVED) */
    public void acceptByApplicant() {
        validateStatus(PartyApplicationStatus.PROCESSING);
        this.applicationStatus = PartyApplicationStatus.APPROVED;
    }

    /** 지원자가 최종 거절(PROCESSING -> DECLINED) */
    public void declineByApplicant() {
        validateStatus(PartyApplicationStatus.PROCESSING);
        this.applicationStatus = PartyApplicationStatus.DECLINED;
    }

    /** 모집 인원 마감으로 인한 자동 종료 */
    public void closeByRecruitment() {
        this.applicationStatus = PartyApplicationStatus.CLOSED;
    }

    /** 본인 확인 검증 */
    public void validateOwner(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new BusinessException(NOT_APPLICATION_OWNER);
        }
    }

}
