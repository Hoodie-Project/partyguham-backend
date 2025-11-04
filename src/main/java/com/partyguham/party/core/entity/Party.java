package com.partyguham.party.core.entity;

import com.partyguham.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SequenceGenerator(name="party_seq_gen", sequenceName="party_seq", allocationSize=50)
public class Party extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_seq_gen")
    @Column(name = "party_id")
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @Column()
    private String image;

//    @OneToMany(mappedBy = "party")
//    private List<PartyUser> partyUsers = new ArrayList<>();
//
//    public void addPartyUser(PartyUser partyUser) {
//        partyUsers.add(partyUser);
//        partyUser.setParty(this);
//
//    }
}
