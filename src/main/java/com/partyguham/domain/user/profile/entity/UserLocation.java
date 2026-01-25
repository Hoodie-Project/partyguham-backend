package com.partyguham.domain.user.profile.entity;

import com.partyguham.domain.catalog.entity.Location;
import com.partyguham.domain.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_locations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_location",
                        columnNames = {"user_id", "location_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "user_locations_seq_gen",
        sequenceName = "user_locations_id_seq",
        allocationSize = 1
)
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_locations_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    public static UserLocation create(User user, Location location) {
        return UserLocation.builder()
                .user(user)
                .location(location)
                .build();
    }
}