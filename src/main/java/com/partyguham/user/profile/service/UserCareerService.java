package com.partyguham.user.profile.service;

import com.partyguham.catalog.entity.Position;
import com.partyguham.catalog.reader.PositionReader;
import com.partyguham.common.exception.BusinessException;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.profile.dto.request.*;
import com.partyguham.user.profile.dto.response.CareerResponse;
import com.partyguham.user.profile.entity.UserCareer;
import com.partyguham.user.profile.reader.UserProfileReader;
import com.partyguham.user.profile.repository.UserCareerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.partyguham.user.exception.UserErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserCareerService {

    private final UserProfileReader userProfileReader;
    private final PositionReader positionReader;
    private final UserCareerRepository userCareerRepository;

    @Transactional(readOnly = true)
    public List<CareerResponse> getMyCareers(Long userId) {
        return userProfileReader.readCareersByUserId(userId).stream()
                .map(CareerResponse::from)
                .toList();
    }

    @Transactional
    public List<CareerResponse> upsertMyCareers(Long userId, UserCareerBulkCreateRequest req) {
        // 1) 유저 존재 확인
        User user = userProfileReader.read(userId);

        // 2) 리스트 널/빈 값 체크 및 중복 체크 (인라인 처리)
        List<UserCareerCreateRequest> careerRequests = req.getCareers();

        // 스트림을 이용해 중복된 CareerType이 있는지 확인
        long distinctCount = careerRequests.stream()
                .map(UserCareerCreateRequest::getCareerType)
                .distinct()
                .count();

        if (distinctCount != careerRequests.size()) {
            throw new BusinessException(USER_CAREER_DUPLICATE);
        }

        List<UserCareer> result = new ArrayList<>();

        // 3) 각 careerType 별로 upsert 수행
        for (UserCareerCreateRequest c : careerRequests) {
            Position pos = positionReader.read(c.getPositionId());

            // 기존에 해당 타입의 경력이 있는지 조회 (UserProfileReader 활용)
            UserCareer uc = userProfileReader.readCareerByType(userId, c.getCareerType())
                    .map(existing -> {
                        // 있다면 필드 수정 (더티 체킹)
                        existing.update(pos, c.getYears());
                        return existing;
                    })
                    .orElseGet(() -> {
                        // 없다면 신규 생성 및 저장
                        UserCareer newCareer = UserCareer.create(user, pos, c.getYears(), c.getCareerType());
                        return userCareerRepository.save(newCareer);
                    });

            result.add(uc);
        }

        // 4) 최종 저장/수정된 리스트 반환
        return result.stream()
                .map(CareerResponse::from)
                .toList();
    }

    @Transactional
    public CareerResponse updateCareer(Long userId, Long careerId, UpdateCareerRequest dto) {
        UserCareer uc = userProfileReader.readCareerAndValidateOwner(careerId, userId);

        Position position = null;
        if (dto.getPositionId() != null) {
            position = positionReader.read(dto.getPositionId());
        }
        uc.update(position, dto.getYears());

        return CareerResponse.from(uc);
    }

    @Transactional
    public void updateCareers(Long userId, BulkCareerUpdateRequest dto) {
        // 1. 요청받은 ID들만 리스트로 추출
        List<Long> ids = dto.careers().stream()
                .map(CareerUpdateItem::id)
                .toList();

        // 2. 해당 유저의 경력 사항들을 한꺼번에 조회 (N+1 방지)
        List<UserCareer> userCareers = userCareerRepository.findAllByIdInAndUserId(ids, userId);

        // 3. 매핑 및 업데이트
        for (CareerUpdateItem item : dto.careers()) {
            userCareers.stream()
                    .filter(uc -> uc.getId().equals(item.id()))
                    .findFirst()
                    .ifPresent(uc -> {
                        // 수정할 값이 있을 때만 Reader를 호출하도록 개선
                        Position pos = null;
                        if (item.positionId() != null) {
                            pos = positionReader.read(item.positionId());
                        }

                        uc.update(pos, item.years());
                    });
        }
    }

    @Transactional
    public void deleteCareer(Long userId, Long careerId) {
        UserCareer uc = userProfileReader.readCareerAndValidateOwner(careerId, userId);
        userCareerRepository.delete(uc);
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        userCareerRepository.deleteByUserId(userId);
    }
}