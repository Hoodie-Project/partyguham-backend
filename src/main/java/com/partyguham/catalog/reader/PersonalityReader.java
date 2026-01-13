package com.partyguham.catalog.reader;


import com.partyguham.catalog.entity.PersonalityOption;
import com.partyguham.catalog.entity.PersonalityQuestion;
import com.partyguham.catalog.repository.PersonalityOptionRepository;
import com.partyguham.catalog.repository.PersonalityQuestionRepository;
import com.partyguham.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.partyguham.catalog.exception.CatalogErrorCode.*;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalityReader {

    private final PersonalityQuestionRepository questionRepository;
    private final PersonalityOptionRepository optionRepository;

    /**
     * 질문 목록 조회 및 존재 검증
     */
    public List<PersonalityQuestion> readQuestions(List<Long> questionIds) {
        List<PersonalityQuestion> questions = questionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new BusinessException(PERSONALITY_QUESTION_NOT_FOUND);
        }
        return questions;
    }

    /**
     * 옵션 목록 조회 및 존재 검증
     */
    public List<PersonalityOption> readOptions(List<Long> optionIds) {
        List<PersonalityOption> options = optionRepository.findByIdIn(optionIds);
        if (options.size() != optionIds.size()) {
            throw new BusinessException(PERSONALITY_OPTION_NOT_FOUND);
        }
        return options;
    }
}