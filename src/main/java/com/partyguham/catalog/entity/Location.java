package com.partyguham.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "location")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "location_seq_gen",
        sequenceName = "location_seq",
        allocationSize = 50
)
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq_gen")
    private Long id;

    @Column(nullable = false)
    private String province;

    @Column(nullable = false)
    private String city;

//    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
//    private List<UserLocation> userLocations;
}