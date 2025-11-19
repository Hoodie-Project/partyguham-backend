package com.partyguham.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personality_question")
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
    private int responseCount;

    @OneToMany(
            mappedBy = "personalityQuestion",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<PersonalityOption> personalityOptions = new ArrayList<>();

    public void addOption(PersonalityOption option) {
        personalityOptions.add(option);
        option.setPersonalityQuestion(this);
    }
}