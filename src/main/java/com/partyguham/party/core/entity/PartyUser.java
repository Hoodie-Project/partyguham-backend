package com.partyguham.party.core.entity;

import com.partyguham.catalog.entity.Position; //확인필요
import com.partyguham.common.entity.BaseEntity;
import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@SequenceGenerator(name="party_user_seq_gen", sequenceName="party_user_seq_gen", allocationSize=50)
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
}
