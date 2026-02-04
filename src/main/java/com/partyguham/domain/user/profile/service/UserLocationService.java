package com.partyguham.domain.user.profile.service;

import com.partyguham.domain.catalog.entity.Location;
import com.partyguham.domain.catalog.reader.LocationReader;
import com.partyguham.global.exception.BusinessException;
import com.partyguham.domain.user.account.reader.UserReader;
import com.partyguham.domain.user.profile.dto.request.UserLocationBulkRequest;
import com.partyguham.domain.user.profile.dto.response.UserLocationResponse;
import com.partyguham.domain.user.profile.entity.UserLocation;
import com.partyguham.domain.user.profile.reader.UserProfileReader;
import com.partyguham.domain.user.profile.repository.UserLocationRepository;
import com.partyguham.domain.user.account.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.partyguham.domain.user.exception.UserErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserLocationService {

    private final UserReader userReader;
    private final UserProfileReader userProfileReader;
    private final LocationReader locationReader;

    private final UserLocationRepository userLocationRepository;

    /** 내 관심지역 전체 조회 */
    @Transactional()
    public List<UserLocationResponse> getMyLocations(Long userId) {
        List<UserLocation> list = userProfileReader.readLocationByUserId(userId);

        return list.stream()
                .map(UserLocationResponse::from)
                .toList();
    }

    /**
     * 내 관심지역 저장 (기존 것 전부 삭제 후 새로 저장)
     * - 최대 3개
     * - 중복 locationId 허용 안 함
     */
    @Transactional
    public List<UserLocationResponse> setMyLocations(Long userId, UserLocationBulkRequest req) {
        List<Long> locationIds = req.getLocationIds();

        // 1) 중복 체크 (비즈니스 규칙이므로 서비스에서 유지)
        if (new HashSet<>(locationIds).size() != locationIds.size()) {
            throw new BusinessException(USER_LOCATION_DUPLICATE);
        }
        // 2) 필요한 데이터 조회
        User user = userReader.read(userId);
        List<Location> locations = locationReader.readByIds(locationIds);

        // 3) 기존 관심지역 일괄 삭제 (Delete)
        userLocationRepository.deleteByUserId(userId);

        // 4) 새 UserLocation 생성 및 저장 (Insert)
        List<UserLocation> newList = locations.stream()
                .map(location -> UserLocation.create(user, location)) // 정적 팩토리 메서드 권장
                .toList();

        return userLocationRepository.saveAll(newList).stream()
                .map(UserLocationResponse::from)
                .toList();
    }

    /** 특정 관심지역 전체 삭제 */
    @Transactional
    public void deleteAllMyLocations(Long userId) {
        userLocationRepository.deleteByUserId(userId);
    }

    /** 특정 관심지역 한 개 삭제 */
    @Transactional
    public void deleteMyLocation(Long userId, Long userLocationId) {
        UserLocation ul = userProfileReader.readLocationAndValidateOwner(userId, userLocationId);

        userLocationRepository.delete(ul);
    }
}