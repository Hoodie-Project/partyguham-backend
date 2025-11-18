package com.partyguham.party.core.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name="party_type")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SequenceGenerator(
        name = "party_type_id_seq_gen",
        sequenceName = "party_type_id_seq",
        allocationSize = 10
)
public class PartyType {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "party_type_id_seq_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @OneToMany(mappedBy = "partyType", fetch = FetchType.LAZY)
    private List<Party> parties;

}
