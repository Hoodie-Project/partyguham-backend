package com.partyguham.catalog.entity;

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
@SequenceGenerator(name="location_seq_gen", sequenceName="location_id_seq", allocationSize=50)
public class Location extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq_gen")
    @Column(name = "location_id")
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