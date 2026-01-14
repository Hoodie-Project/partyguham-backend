package com.partyguham.party.entity;

import com.partyguham.common.entity.BaseEntity;
import com.partyguham.common.entity.Status;
import com.partyguham.common.exception.BusinessException;
import com.partyguham.party.exception.PartyErrorCode;
import com.partyguham.recruitment.entity.PartyRecruitment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "parties")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SequenceGenerator(name="party_id_seq_gen", sequenceName="party_id_seq", allocationSize=50)
public class Party extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_id_seq_gen")
    @Column
    private Long id;

    @Column
    private String title;

    @Column(columnDefinition = "text")
    private String content;

    @Column()
    private String image;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyStatus partyStatus = PartyStatus.IN_PROGRESS;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_type_id", nullable = false)
    private PartyType partyType;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyUser> partyUsers;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyRecruitment> partyRecruitments;

    /**
     * 파티 삭제 (소프트 삭제)
     */
    public void delete() {
        if (this.getStatus() == Status.DELETED) {
            throw new BusinessException(PartyErrorCode.PARTY_ALREADY_DELETED);
        }
        this.changeStatus(Status.DELETED);
    }

    /**
     * 파티 정보 업데이트 (제목, 내용, 타입)
     */
    public void updateInfo(String title, String content, PartyType partyType) {
        validateCanBeModified();
        
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (partyType != null) {
            this.partyType = partyType;
        }
    }

    /**
     * 파티 이미지 업데이트
     */
    public void updateImage(String imageKey) {
        validateCanBeModified();
        this.image = imageKey;
    }

    /**
     * 파티 이미지 제거
     */
    public void removeImage() {
        validateCanBeModified();
        this.image = null;
    }

    /**
     * 파티 수정 가능 여부 검증
     */
    public void validateCanBeModified() {
        if (this.getStatus() == Status.DELETED) {
            throw new BusinessException(PartyErrorCode.PARTY_ALREADY_DELETED);
        }
    }

    /**
     * 파티 상태 업데이트
     */
    public void updatePartyStatus(PartyStatus newStatus) {
        // 상태 전이 검증
        if (newStatus == PartyStatus.CLOSED && this.partyStatus == PartyStatus.CLOSED) {
            throw new BusinessException(PartyErrorCode.PARTY_ALREADY_CLOSED);
        }
        if (newStatus == PartyStatus.IN_PROGRESS && this.partyStatus != PartyStatus.CLOSED) {
            throw new BusinessException(PartyErrorCode.PARTY_NOT_CLOSED);
        }
        this.partyStatus = newStatus;
    }

    // 기존 단순 업데이트 메서드들 (하위 호환성을 위해 유지)
    public void updatePartyType(PartyType partyType) {
        this.partyType = partyType;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
