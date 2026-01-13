package com.partyguham.catalog.entity;

import com.partyguham.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static com.partyguham.user.exception.UserErrorCode.PERSONALITY_TOO_MANY_OPTIONS;

@Entity
@Table(name = "personality_questions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "personality_question_seq_gen",
        sequenceName = "personality_question_seq",
        allocationSize = 50
)
public class PersonalityQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personality_question_seq_gen")
    private Long id;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "response_count", nullable = false)
    private short responseCount;

    @OneToMany(
            mappedBy = "personalityQuestion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PersonalityOption> personalityOptions = new ArrayList<>();

    public void validateOptionCount(int selectedCount) {
        if (selectedCount > this.responseCount) {
            throw new BusinessException(PERSONALITY_TOO_MANY_OPTIONS);
        }
    }

}