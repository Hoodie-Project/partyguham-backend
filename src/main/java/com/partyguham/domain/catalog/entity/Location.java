package com.partyguham.domain.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "locations_seq_gen",
        sequenceName = "locations_id_seq",
        allocationSize = 1
)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "locations_seq_gen")
    private Long id;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

//    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
//    private List<UserLocation> userLocations;
}