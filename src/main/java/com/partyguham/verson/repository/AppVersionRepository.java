package com.partyguham.verson.repository;

import com.partyguham.verson.entity.AppPlatform;
import com.partyguham.verson.entity.AppVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppVersionRepository extends JpaRepository<com.partyguham.verson.entity.AppVersion, Long> {

    // 특정 플랫폼의 최신 버전(가장 최근에 등록된 것 1개)
    Optional<AppVersion> findTopByPlatformOrderByIdDesc(AppPlatform platform);

    // 필요하면 리스트 조회용
    List<AppVersion> findByPlatformOrderByIdDesc(AppPlatform platform);
}
