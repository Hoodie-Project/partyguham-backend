package com.partyguham.user.profile.entity;

import com.partyguham.catalog.entity.Location;
import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_location",
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
        name = "user_location_seq_gen",
        sequenceName = "user_location_seq",
        allocationSize = 50
)
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_location_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
}