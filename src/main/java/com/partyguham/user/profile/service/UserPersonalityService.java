package com.partyguham.user.profile.service;

import com.partyguham.catalog.entity.PersonalityOption;
import com.partyguham.catalog.entity.PersonalityQuestion;
import com.partyguham.catalog.repository.PersonalityOptionRepository;
import com.partyguham.catalog.repository.PersonalityQuestionRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.reader.UserReader;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.profile.dto.response.PersonalityAnswerItem;
import com.partyguham.user.profile.dto.response.PersonalityAnswerResponse;
import com.partyguham.user.profile.dto.request.PersonalityBulkAnswerRequest;
import com.partyguham.user.profile.entity.UserPersonality;
import com.partyguham.user.profile.repository.UserPersonalityRepository;
import jakarta.persistence.EntityNotFoundException;
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

    private final PersonalityQuestionRepository questionRepository;
    private final PersonalityOptionRepository optionRepository;
    private final UserPersonalityRepository userPersonalityRepository;

    /**
     * 성향 응답 생성/수정 (Upsert)
     *
     * @param userId 로그인 유저 ID
     * @param req    질문 ID + 선택한 옵션 ID 리스트
     */
    @Transactional
    public List<PersonalityAnswerResponse> saveAnswers(Long userId, PersonalityBulkAnswerRequest req) {

        User user = userReader.read(userId);

        // 1) 요청 유효성 체크
        List<PersonalityAnswerItem> items = Optional.ofNullable(req.personalities())
                .orElse(List.of());

        if (items.isEmpty()) {
            throw new IllegalArgumentException("personality 리스트가 비어 있습니다.");
        }

        // 2) 질문 ID 모으기 (중복 제거)
        List<Long> questionIds = items.stream()
                .map(PersonalityAnswerItem::questionId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (questionIds.isEmpty()) {
            throw new IllegalArgumentException("questionId가 비어 있습니다.");
        }

        // 3) 질문들 조회
        List<PersonalityQuestion> questions = questionRepository.findAllById(questionIds);
        if (questions.size() != questionIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 questionId가 포함되어 있습니다.");
        }
        Map<Long, PersonalityQuestion> questionMap = questions.stream()
                .collect(Collectors.toMap(PersonalityQuestion::getId, q -> q));

        // 4) 전체 옵션 ID 모으기 (중복 제거)
        List<Long> allOptionIds = items.stream()
                .flatMap(i -> Optional.ofNullable(i.optionIds()).orElse(List.of()).stream())
                .distinct()
                .toList();

        if (allOptionIds.isEmpty()) {
            throw new IllegalArgumentException("optionIds가 비어 있습니다.");
        }

        // 5) 옵션들 조회
        List<PersonalityOption> allOptions = optionRepository.findByIdIn(allOptionIds);
        if (allOptions.size() != allOptionIds.size()) {
            throw new IllegalArgumentException("잘못된 옵션 ID가 포함되어 있습니다.");
        }
        Map<Long, PersonalityOption> optionMap = allOptions.stream()
                .collect(Collectors.toMap(PersonalityOption::getId, o -> o));

        // 6) 질문별 선택 옵션 리스트 구성 + 검증
        Map<Long, List<PersonalityOption>> questionToOptions = new LinkedHashMap<>();

        for (PersonalityAnswerItem item : items) {
            Long qId = item.questionId();
            PersonalityQuestion question = questionMap.get(qId);
            if (question == null) {
                throw new IllegalArgumentException("존재하지 않는 질문 ID입니다. id=" + qId);
            }

            List<Long> optionIds = Optional.ofNullable(item.optionIds())
                    .orElse(List.of())
                    .stream()
                    .distinct()
                    .toList();

            if (optionIds.isEmpty()) {
                throw new IllegalArgumentException("questionId=" + qId + " 에 대한 optionIds가 비어 있습니다.");
            }

            // 6-1) 질문당 최대 선택 개수(responseCount) 검증
            int maxSelectable = question.getResponseCount();
            if (optionIds.size() > maxSelectable) {
                throw new IllegalArgumentException(
                        "questionId=" + qId + " 은(는) 최대 " + maxSelectable + "개까지 선택 가능합니다."
                );
            }

            // 6-2) 옵션 ID -> 엔티티 매핑 + 질문 일치 여부 검증
            List<PersonalityOption> optionsForQuestion = optionIds.stream()
                    .map(optionId -> {
                        PersonalityOption opt = optionMap.get(optionId);
                        if (opt == null) {
                            throw new IllegalArgumentException("존재하지 않는 옵션 ID입니다. id=" + optionId);
                        }
                        if (!Objects.equals(
                                opt.getPersonalityQuestion().getId(),
                                question.getId()
                        )) {
                            throw new IllegalArgumentException(
                                    "optionId=" + optionId + " 는 questionId=" + qId + " 에 속하지 않습니다."
                            );
                        }
                        return opt;
                    })
                    .toList();

            questionToOptions.put(qId, optionsForQuestion);
        }

        // 7) 기존 응답 삭제 (해당 유저 + 이번에 제출한 질문들 기준)
        // user_personalities 테이블에서 user_id + question_id in (...) 삭제
        userPersonalityRepository.deleteByUserIdAndQuestion_IdIn(userId, questionIds);
        // (만약 이 메서드가 없다면, questionIds.forEach(qId -> deleteByUserIdAndQuestionId(userId, qId)); 로 처리)

        // 8) 새 응답 엔티티 생성
        List<UserPersonality> newAnswers = new ArrayList<>();
        for (Map.Entry<Long, List<PersonalityOption>> entry : questionToOptions.entrySet()) {
            Long qId = entry.getKey();
            PersonalityQuestion question = questionMap.get(qId);

            for (PersonalityOption opt : entry.getValue()) {
                UserPersonality up = UserPersonality.builder()
                        .user(user)
                        .question(question)
                        .personalityOption(opt)
                        .build();
                newAnswers.add(up);
            }
        }

        userPersonalityRepository.saveAll(newAnswers);

        // 9) 응답 DTO 리스트로 변환
        return questionToOptions.entrySet().stream()
                .map(entry -> PersonalityAnswerResponse.from(
                        questionMap.get(entry.getKey()),
                        entry.getValue()
                ))
                .toList();
    }
    /**
     * 내 성향 전체 조회
     */
    @Transactional(readOnly = true)
    public List<PersonalityAnswerResponse> getMyAnswers(Long userId) {
        // 해당 유저의 모든 UserPersonality 가져오기
        List<UserPersonality> list = userPersonalityRepository.findByUserId(userId);

        // question 기준으로 그룹핑 (한 질문에 여러 옵션 선택 가능)
        Map<PersonalityQuestion, List<UserPersonality>> grouped =
                list.stream().collect(Collectors.groupingBy(UserPersonality::getQuestion));

        List<PersonalityAnswerResponse> result = new ArrayList<>();

        grouped.forEach((q, answers) -> {
            // 각 질문별로 선택된 옵션들 DTO로 변환
            List<PersonalityAnswerResponse.OptionInfo> ops = answers.stream()
                    .map(a -> new PersonalityAnswerResponse.OptionInfo(
                            // ✅ 여기서도 personalityOption 기준으로 꺼내야 함
                            a.getPersonalityOption().getId(),
                            a.getPersonalityOption().getContent()
                    ))
                    .toList();

            result.add(new PersonalityAnswerResponse(q.getId(), q.getContent(), ops));
        });

        return result;
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
        userPersonalityRepository.deleteByUser_IdAndPersonalityOption_Id(userId, optionId);
    }
}