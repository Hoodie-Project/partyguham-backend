package com.partyguham.domain.catalog.service;


import com.partyguham.domain.catalog.dto.response.PersonalityQuestionWithOptionsResponse;
import com.partyguham.domain.catalog.entity.PersonalityQuestion;
import com.partyguham.domain.catalog.repository.PersonalityQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalityQuestionQueryService {

    private final PersonalityQuestionRepository questionRepository;

    /**
     * 성향 질문 + 보기 전체 조회
     */
    @Transactional(readOnly = true)
    public List<PersonalityQuestionWithOptionsResponse> getAllQuestionsWithOptions() {
        List<PersonalityQuestion> questions = questionRepository.findAll(); // @EntityGraph로 옵션까지 로딩

        return questions.stream()
                .map(q -> new PersonalityQuestionWithOptionsResponse(
                        q.getId(),
                        q.getContent(),
                        q.getResponseCount(),
                        q.getPersonalityOptions().stream()
                                .map(o -> new PersonalityQuestionWithOptionsResponse.OptionInfo(
                                        o.getId(),
                                        o.getContent()
                                ))
                                .toList()
                ))
                .toList();
    }
}
