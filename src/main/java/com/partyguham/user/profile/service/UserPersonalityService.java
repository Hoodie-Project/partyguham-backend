package com.partyguham.user.profile.service;

import com.partyguham.catalog.entity.PersonalityOption;
import com.partyguham.catalog.entity.PersonalityQuestion;
import com.partyguham.catalog.repository.PersonalityOptionRepository;
import com.partyguham.catalog.repository.PersonalityQuestionRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.profile.dto.request.PersonalityAnswerRequest;
import com.partyguham.user.profile.dto.response.PersonalityAnswerResponse;
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

    private final UserRepository userRepository;
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
    public PersonalityAnswerResponse saveAnswer(Long userId, PersonalityAnswerRequest req) {

        // 0) 유저 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        // 1) 질문 존재 여부 확인
        PersonalityQuestion question = questionRepository.findById(req.questionId())
                .orElseThrow(() -> new EntityNotFoundException("question not found"));

        // 2) 옵션 ID 중복 제거 + 비어있는지 체크
        List<Long> distinctOptionIds = req.optionIds().stream().distinct().toList();
        if (distinctOptionIds.isEmpty()) {
            throw new IllegalArgumentException("optionIds must not be empty");
        }

        // (선택) 최대 선택 개수 제한
        // if (distinctOptionIds.size() > 3) throw new IllegalArgumentException("최대 3개까지 선택 가능합니다.");

        // 3) 옵션들 조회
        List<PersonalityOption> options = optionRepository.findByIdIn(distinctOptionIds);
        if (options.size() != distinctOptionIds.size()) {
            throw new IllegalArgumentException("잘못된 옵션 ID가 포함되어 있습니다.");
        }

        // 4) 모든 옵션이 같은 질문에 속하는지 검증
        boolean allMatchQuestion = options.stream()
                .allMatch(o -> Objects.equals(o.getPersonalityQuestion().getId(), question.getId()));
        if (!allMatchQuestion) {
            throw new IllegalArgumentException("옵션이 해당 질문에 속하지 않습니다.");
        }

        // 5) 기존 응답 삭제 (해당 유저 + 질문 기준)
        userPersonalityRepository.deleteByUserIdAndQuestionId(userId, question.getId());

        // 6) 새 응답 저장
        List<UserPersonality> newAnswers = options.stream()
                .map(opt -> UserPersonality.builder()
                        .user(user)
                        .question(question)
                        // ✅ 엔티티 필드명이 personalityOption 이라면 이렇게!
                        .personalityOption(opt)
                        .build())
                .toList();

        userPersonalityRepository.saveAll(newAnswers);

        // 7) 응답 DTO 변환
        List<PersonalityAnswerResponse.OptionInfo> optionInfos = options.stream()
                .map(o -> new PersonalityAnswerResponse.OptionInfo(o.getId(), o.getContent()))
                .toList();

        return new PersonalityAnswerResponse(
                question.getId(),
                question.getContent(),
                optionInfos
        );
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
        userPersonalityRepository.deleteByUserIdAndQuestionId(userId, questionId);
    }

    @Transactional
    public void deleteAnswerByOption(Long userId, Long optionId) {
        userPersonalityRepository.deleteByUserIdAndPersonalityOptionId(userId, optionId);
    }
}