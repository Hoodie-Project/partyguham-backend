package com.partyguham.domain.user.profile.entity;

import com.partyguham.domain.catalog.entity.PersonalityOption;
import com.partyguham.domain.catalog.entity.PersonalityQuestion;
import com.partyguham.domain.user.account.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_personalities",
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
        name = "user_personalities_seq_gen",
        sequenceName = "user_personalities_id_seq",
        allocationSize = 1
)
public class UserPersonality {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_personalities_seq_gen")
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


    public static UserPersonality create(User user, PersonalityQuestion question, PersonalityOption option) {
        return UserPersonality.builder()
                .user(user)
                .question(question)
                .personalityOption(option)
                .build();
    }
}