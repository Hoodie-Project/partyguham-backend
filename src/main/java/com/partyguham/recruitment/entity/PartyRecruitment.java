package com.partyguham.recruitment.entity;

import com.partyguham.catalog.entity.Position;
import com.partyguham.common.entity.BaseEntity;
import com.partyguham.application.entity.PartyApplication;
import com.partyguham.common.entity.Status;
import com.partyguham.common.exception.BusinessException;
import com.partyguham.party.entity.Party;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static com.partyguham.recruitment.exception.RecruitmentErrorCode.*;


@Entity
@Table(name = "party_recruitments")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SequenceGenerator(
        name = "party_recruitment_id_seq_gen",
        sequenceName = "party_recruitment_id_seq",
        allocationSize = 50
)
public class PartyRecruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_recruitment_id_seq_gen")
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @Column(columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Column(nullable = false)
    private Integer currentParticipants;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @OneToMany(mappedBy = "partyRecruitment", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyApplication> partyApplications;

    /**
     * 모집 수정
     */
    public void update(String content, int maxParticipants) {
        if (maxParticipants <= this.currentParticipants) {
            throw new BusinessException(PR_INVALID_MAX_PARTICIPANTS);
        }

        this.content = content;
        this.maxParticipants = maxParticipants;

    }

    /**
     * 소프트 삭제
     */
    public void delete() {
        this.changeStatus(Status.DELETED);
    }

    /**
     * 모집을 수동으로 마감 처리합니다.
     */
    public void complete() {
        if (Boolean.TRUE.equals(this.completed)) {
            throw new BusinessException(PR_COMPLETED_TRUE); // 이미 마감된 경우 예외 처리 (선택)
        }
        this.completed = true;
    }


    /** 모집 마감 여부 확인 */
    public void validateNotCompleted() {
        if (Boolean.TRUE.equals(this.completed)) {
            throw new BusinessException(PR_COMPLETED_TRUE);
        }
    }

    /** 참여 인원 증가 및 자동 마감 처리 */
    public void increaseParticipant() {
        validateNotCompleted();
        this.currentParticipants++;

        if (this.currentParticipants.equals(this.maxParticipants)) {
            this.completed = true;
        }
    }
}
