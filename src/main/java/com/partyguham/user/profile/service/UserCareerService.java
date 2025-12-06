package com.partyguham.user.profile.service;

import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.repository.PositionRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import com.partyguham.user.profile.dto.request.UserCareerBulkCreateRequest;
import com.partyguham.user.profile.dto.request.UserCareerCreateRequest;
import com.partyguham.user.profile.dto.response.CareerResponse;
import com.partyguham.user.profile.entity.CareerType;
import com.partyguham.user.profile.entity.UserCareer;
import com.partyguham.user.profile.repository.UserCareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCareerService {

    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final UserCareerRepository userCareerRepository;

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    private Position getPositionOrThrow(Long positionId) {
        return positionRepository.findById(positionId)
                .orElseThrow(() -> new IllegalArgumentException("position not found"));
    }

    /**
     * READ: 나의 모든 경력 조회
     */
    @Transactional(readOnly = true)
    public List<CareerResponse> getMyCareers(Long userId) {
        User user = getUserOrThrow(userId);

        return userCareerRepository.findByUser(user).stream()
                .map(CareerResponse::from)  // 공통 변환 로직 사용
                .toList();
    }

    /**
     * CREATE/UPSERT: PRIMARY/SECONDARY를 한 번에 저장/갱신 (최대 2개)
     * - 요청에 들어온 careerType 들을 기준으로 기존 데이터 삭제/생성/수정
     * - 같은 careerType이 요청 안에 중복되면 예외
     * - 응답은 최종적으로 저장된 나의 경력 목록
     */
    @Transactional
    public List<CareerResponse> upsertMyCareers(Long userId, UserCareerBulkCreateRequest req) {
        User user = getUserOrThrow(userId);

        if (req.getCareers() == null || req.getCareers().isEmpty()) {
            throw new IllegalArgumentException("careers is empty");
        }

        // 1) 요청 안에서 careerType 중복 방지
        Set<CareerType> typesInReq = new HashSet<>();
        for (UserCareerCreateRequest c : req.getCareers()) {
            if (!typesInReq.add(c.getCareerType())) {
                throw new IllegalArgumentException("same careerType appears multiple times in request");
            }
        }

        List<UserCareer> result = new ArrayList<>();

        // 2) 각 careerType 별로 upsert 수행
        for (UserCareerCreateRequest c : req.getCareers()) {
            Position pos = getPositionOrThrow(c.getPositionId());
            CareerType type = c.getCareerType();

            // 이미 이 타입의 경력이 있으면 -> 업데이트
            Optional<UserCareer> existingOpt = userCareerRepository.findByUserAndCareerType(user, type);
            if (existingOpt.isPresent()) {
                UserCareer existing = existingOpt.get();
                existing.setPosition(pos);
                existing.setYears(c.getYears());
                result.add(existing);
            } else {
                // 없으면 새로 생성
                UserCareer uc = UserCareer.builder()
                        .user(user)
                        .position(pos)
                        .years(c.getYears())
                        .careerType(type)
                        .build();
                userCareerRepository.save(uc);
                result.add(uc);
            }
        }

        // 3) 엔티티 -> 응답 DTO 변환
        return result.stream()
                .map(CareerResponse::from)
                .toList();
    }

    /**
     * UPDATE: 특정 경력의 years(경력 연차)만 변경
     * - 권장: 단일 필드 업데이트용 API
     */
    @Transactional
    public CareerResponse updateYears(Long userId, Long careerId, Integer years) {
        User user = getUserOrThrow(userId);

        UserCareer uc = userCareerRepository.findById(careerId)
                .orElseThrow(() -> new IllegalArgumentException("career not found"));

        // 본인 소유 검증
        if (!uc.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("not your career");
        }

        uc.setYears(years);

        return CareerResponse.from(uc);
    }

    // DELETE: 특정 경력 삭제
    @Transactional
    public void deleteCareer(Long userId, Long careerId) {
        User user = getUserOrThrow(userId);
        UserCareer uc = userCareerRepository.findById(careerId)
                .orElseThrow(() -> new IllegalArgumentException("career not found"));

        if (!uc.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("not your career");
        }

        userCareerRepository.delete(uc);
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        userCareerRepository.deleteByUserId(userId);
    }

}