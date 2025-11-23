package com.partyguham.user.profile.service;

import com.partyguham.catalog.entity.Location;
import com.partyguham.catalog.repository.LocationRepository;
import com.partyguham.user.profile.dto.request.UserLocationBulkRequest;
import com.partyguham.user.profile.dto.response.UserLocationResponse;
import com.partyguham.user.profile.entity.UserLocation;
import com.partyguham.user.profile.repository.UserLocationRepository;
import com.partyguham.user.account.entity.User;
import com.partyguham.user.account.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserLocationService {

    private static final int MAX_LOCATIONS = 3;

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final UserLocationRepository userLocationRepository;

    /** 내 관심지역 전체 조회 */
    @Transactional
    public List<UserLocationResponse> getMyLocations(Long userId) {
        List<UserLocation> list = userLocationRepository.findByUserId(userId);

        return list.stream()
                .map(ul -> new UserLocationResponse(
                        ul.getId(),
                        ul.getLocation().getId(),
                        ul.getLocation().getProvince(),
                        ul.getLocation().getCity()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 내 관심지역 일괄 설정 (기존 것 전부 삭제 후 새로 저장)
     * - 최대 3개
     * - 중복 locationId 허용 안 함
     */
    @Transactional
    public void setMyLocations(Long userId, UserLocationBulkRequest req) {
        List<Long> locationIds = Optional.ofNullable(req.getLocationIds())
                .orElse(List.of());

        // 1) 개수 제한 체크
        if (locationIds.size() > MAX_LOCATIONS) {
            throw new IllegalArgumentException("관심 지역은 최대 " + MAX_LOCATIONS + "개까지 설정할 수 있습니다.");
        }

        // 2) 중복 제거 후 사이즈 비교 (중복 있으면 에러)
        Set<Long> distinct = new HashSet<>(locationIds);
        if (distinct.size() != locationIds.size()) {
            throw new IllegalArgumentException("중복된 지역이 포함되어 있습니다.");
        }

        // 3) 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        // 4) 기존 관심지역 삭제
        List<UserLocation> oldList = userLocationRepository.findByUserId(userId);
        userLocationRepository.deleteAllInBatch(oldList);

        // 5) 비어있으면 여기서 끝
        if (locationIds.isEmpty()) return;

        // 6) Location 엔티티 조회
        List<Location> locations = locationRepository.findAllById(locationIds);
        if (locations.size() != locationIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 locationId가 포함되어 있습니다.");
        }

        // 7) 새 UserLocation 생성
        List<UserLocation> newList = locations.stream()
                .map(loc -> UserLocation.builder()
                        .user(user)
                        .location(loc)
                        .build())
                .collect(Collectors.toList());

        userLocationRepository.saveAll(newList);
    }

    /** 특정 관심지역 한 개 삭제 */
    @Transactional
    public void deleteMyLocation(Long userId, Long userLocationId) {
        UserLocation ul = userLocationRepository.findById(userLocationId)
                .orElseThrow(() -> new IllegalArgumentException("userLocation not found"));

        // 자기 것만 삭제 가능
        if (!ul.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인의 관심지역만 삭제할 수 있습니다.");
        }

        userLocationRepository.delete(ul);
    }
}