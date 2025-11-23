package com.partyguham.user.profile.entity;

import com.partyguham.catalog.entity.PersonalityOption;
import com.partyguham.catalog.entity.PersonalityQuestion;
import com.partyguham.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_personality",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_personality",
                        columnNames = {"user_id", "personality_option_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "user_personality_seq_gen",
        sequenceName = "user_personality_seq",
        allocationSize = 50
)
public class UserPersonality {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_personality_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personality_question_id", nullable = false)
    private PersonalityQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personality_option_id", nullable = false)
    private PersonalityOption personalityOption;

    public void changeOption(PersonalityOption personalityOption) {
        this.personalityOption = personalityOption;
    }
}