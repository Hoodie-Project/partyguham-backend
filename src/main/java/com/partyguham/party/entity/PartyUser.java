package com.partyguham.party.entity;

import com.partyguham.catalog.entity.Position; //확인필요
import com.partyguham.common.entity.BaseEntity;
import com.partyguham.common.entity.Status;
import com.partyguham.common.error.exception.BusinessException;
import com.partyguham.party.exception.PartyUserErrorCode;
import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "party_users")
@Getter
@Setter
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

    public void leave() {
        if (this.authority == PartyAuthority.MASTER) {
            throw new BusinessException(PartyUserErrorCode.INVALID_LEAVE_REQUEST_BY_LEADER);
        }

        this.changeStatus(Status.DELETED);
    }

    public void delete() {
        this.changeStatus(Status.DELETED);
    }
}

