package com.partyguham.user.profile.entity;
import com.partyguham.catalog.entity.Position;
import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_careers",
        uniqueConstraints = {
                // (user_id, career_type) 1개만 허용 (PRIMARY 1개, SECONDARY 1개)
                @UniqueConstraint(
                        name = "uk_user_career_user_type",
                        columnNames = {"user_id", "career_type"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "user_career_seq_gen",
        sequenceName = "user_career_seq",
        allocationSize = 50
)
public class UserCareer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_career_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @Column(nullable = false)
    private Integer years;

    @Enumerated(EnumType.STRING)
    @Column(name = "career_type", nullable = false)
    private CareerType careerType;
}