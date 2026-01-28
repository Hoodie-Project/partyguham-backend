package com.partyguham.domain.user.profile.service;

import com.partyguham.domain.catalog.entity.PersonalityOption;
import com.partyguham.domain.catalog.entity.PersonalityQuestion;
import com.partyguham.domain.catalog.reader.PersonalityReader;
import com.partyguham.domain.user.account.entity.User;
import com.partyguham.domain.user.account.reader.UserReader;
import com.partyguham.domain.user.profile.dto.PersonalityAnswerDto;
import com.partyguham.domain.user.profile.dto.response.PersonalityAnswerResponse;
import com.partyguham.domain.user.profile.dto.request.PersonalityBulkAnswerRequest;
import com.partyguham.domain.user.profile.entity.UserPersonality;
import com.partyguham.domain.user.profile.reader.UserProfileReader;
import com.partyguham.domain.user.profile.repository.UserPersonalityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 유저 성향(성격) 답변 CRUD 서비스
 *
 * - saveAnswer : 특정 질문에 대해 여러 옵션 선택(upsert)
 * - getMyAnswers : 내 모든 성향 답변 조회
 * - deleteAnswer : 특정 질문에 대한 내 답변 삭제
 */
@Service
@RequiredArgsConstructor
public class UserPersonalityService {

    private final UserReader userReader;
    private final UserProfileReader userProfileReader;
    private final PersonalityReader personalityReader;
    private final UserPersonalityRepository userPersonalityRepository;

    /**
     * 성향 응답 생성/수정 (Upsert)
     * 기존 응답을 삭제하고 새로운 응답을 저장하는 Delete & Insert 방식
     */
    @Transactional
    public List<PersonalityAnswerResponse> saveAnswers(Long userId, PersonalityBulkAnswerRequest req) {
        User user = userReader.read(userId);
        List<PersonalityAnswerDto> items = req.personalities();

        // 1) 질문 및 옵션 마스터 데이터 일괄 조회 (N+1 방지)
        List<Long> questionIds = items.stream().map(PersonalityAnswerDto::questionId).toList();
        Map<Long, PersonalityQuestion> questionMap = personalityReader.readQuestions(questionIds).stream()
                .collect(Collectors.toMap(PersonalityQuestion::getId, q -> q));

        List<Long> allOptionIds = items.stream().flatMap(i -> i.optionIds().stream()).distinct().toList();
        Map<Long, PersonalityOption> optionMap = personalityReader.readOptions(allOptionIds).stream()
                .collect(Collectors.toMap(PersonalityOption::getId, o -> o));

        // 2) 검증 및 엔티티 생성
        List<UserPersonality> newAnswers = new ArrayList<>();
        Map<Long, List<PersonalityOption>> questionToOptions = new LinkedHashMap<>();

        for (PersonalityAnswerDto item : items) {
            PersonalityQuestion question = questionMap.get(item.questionId());
            question.validateOptionCount(item.optionIds().size()); // 질문당 선택 가능 개수 검증

            List<PersonalityOption> optionsForQuestion = item.optionIds().stream()
                    .map(optionId -> {
                        PersonalityOption opt = optionMap.get(optionId);
                        opt.validateBelongsTo(question.getId()); // 옵션이 해당 질문 소속인지 검증

                        newAnswers.add(UserPersonality.create(user, question, opt));
                        return opt;
                    }).toList();

            questionToOptions.put(item.questionId(), optionsForQuestion);
        }

        // 3) 벌크 삭제 후 일괄 저장
        userPersonalityRepository.deleteByUserIdAndQuestion_IdIn(userId, questionIds);
        userPersonalityRepository.saveAll(newAnswers);

        return questionToOptions.entrySet().stream()
                .map(entry -> PersonalityAnswerResponse.from(questionMap.get(entry.getKey()), entry.getValue()))
                .toList();
    }

    /** 나의 모든 성향 응답 조회 및 그룹화 */
    @Transactional(readOnly = true)
    public List<PersonalityAnswerResponse> getMyAnswers(Long userId) {
        List<UserPersonality> list = userProfileReader.readPersonalitiesByUserId(userId);

        return list.stream()
                .collect(Collectors.groupingBy(UserPersonality::getQuestion))
                .entrySet().stream()
                .map(entry -> PersonalityAnswerResponse.from(entry.getKey(),
                        entry.getValue().stream().map(UserPersonality::getPersonalityOption).toList()))
                .toList();
    }

    @Transactional
    public void deleteAllAnswers(Long userId) {
        userPersonalityRepository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteAnswersByQuestion(Long userId, Long questionId) {
        userPersonalityRepository.deleteByUserIdAndQuestion_Id(userId, questionId);
    }

    @Transactional
    public void deleteAnswerByOption(Long userId, Long optionId) {
        userPersonalityRepository.deleteByUserIdAndOptionId(userId, optionId);
    }
}