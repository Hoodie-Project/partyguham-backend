package com.partyguham.party.entity;

import com.partyguham.catalog.entity.Position; //확인필요
import com.partyguham.common.entity.BaseEntity;
import com.partyguham.common.entity.Status;
import com.partyguham.common.exception.BusinessException;
import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static com.partyguham.party.exception.PartyUserErrorCode.*;

@Entity
@Table(name = "party_users")
@Getter
@SuperBuilder
@NoArgsConstructor
@SequenceGenerator(name = "party_user_seq_gen", sequenceName = "party_user_seq_gen", allocationSize = 50)
public class PartyUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_user_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartyAuthority authority;

    /** 마스터(방장) 여부 확인 */
    public boolean isMaster() {
        return this.authority == PartyAuthority.MASTER;
    }

    /** 매니저 권한 확인 */
    public void checkManager() {
        if (this.authority != PartyAuthority.MASTER && this.authority != PartyAuthority.DEPUTY) {
            throw new BusinessException(NEED_MANAGER_AUTHORITY);
        }
    }

    /** 마스터(방장) 권한 확인 */
    public void checkMaster() {
        if (this.authority != PartyAuthority.MASTER) {
            throw new BusinessException(NEED_MASTER_AUTHORITY);
        }
    }

    /** 권한 위임 (내가 방장이 되고 상대는 방장이 아니게 됨) */
    public void delegateTo(PartyUser target) {
        this.checkMaster();

        if (this.equals(target)) {
            throw new BusinessException(PARTY_USER_SELF_DELEGATION);
        }

        this.authority = PartyAuthority.MEMBER; // 나는 일반 멤버로 (또는 DEPUTY)
        target.authority = PartyAuthority.MASTER; // 상대는 마스터로
    }

    /** 강제 퇴장 처리 */
    public void kick(PartyUser target) {
        this.checkManager();
        target.checkMaster();

        target.changeStatus(Status.DELETED);
    }

    /** 떠나기 */
    public void leave() {
        if (this.authority == PartyAuthority.MASTER) {
            throw new BusinessException(PARTY_LEAVE_REQUEST_BY_LEADER);
        }

        this.changeStatus(Status.DELETED);
    }

    /**
     * 멤버의 포지션(직무) 변경
     * @param newPosition 수정할 새로운 포지션 엔티티
     */
    public void updatePosition(Position newPosition) {
        this.position = newPosition;
    }

    public void delete() {
        this.changeStatus(Status.DELETED);
    }
}

