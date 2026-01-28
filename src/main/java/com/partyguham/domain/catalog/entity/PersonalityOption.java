package com.partyguham.domain.catalog.entity;

import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.user.profile.entity.UserPersonality;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.partyguham.domain.user.exception.UserErrorCode.INVALID_OPTION_FOR_QUESTION;

@Entity
@Table(name = "personality_options")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "personality_options_seq_gen",
        sequenceName = "personality_options_id_seq",
        allocationSize = 1
)
public class PersonalityOption {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personality_options_seq_gen")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personality_question_id", nullable = false)
    private PersonalityQuestion personalityQuestion;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @OneToMany(
            mappedBy = "personalityOption",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<UserPersonality> userPersonalities = new ArrayList<>();

    public void validateBelongsTo(Long questionId) {
        if (!this.personalityQuestion.getId().equals(questionId)) {
            throw new BusinessException(INVALID_OPTION_FOR_QUESTION);
        }
    }
}