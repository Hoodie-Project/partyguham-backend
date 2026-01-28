package com.partyguham.domain.version.service;

import com.partyguham.domain.version.dto.request.AppVersionCreateRequest;
import com.partyguham.domain.version.dto.request.AppVersionUpdateRequest;
import com.partyguham.domain.version.dto.response.AppVersionResponse;
import com.partyguham.domain.version.entity.AppPlatform;
import com.partyguham.domain.version.entity.AppVersion;
import com.partyguham.domain.version.repository.AppVersionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppVersionService {

    private final AppVersionRepository repo;

    private AppVersionResponse toDto(AppVersion v) {
        return AppVersionResponse.builder()
                .id(v.getId())
                .platform(v.getPlatform())
                .latestVersion(v.getLatestVersion())
                .minRequiredVersion(v.getMinRequiredVersion())
                .releaseNotes(v.getReleaseNotes())
                .forceUpdate(v.getForceUpdate())
                .downloadUrl(v.getDownloadUrl())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }

    @Transactional
    public AppVersionResponse create(AppVersionCreateRequest req) {
        AppVersion v = AppVersion.builder()
                .platform(req.getPlatform())
                .latestVersion(req.getLatestVersion())
                .minRequiredVersion(req.getMinRequiredVersion())
                .releaseNotes(req.getReleaseNotes())
                .forceUpdate(req.isForceUpdate())
                .downloadUrl(req.getDownloadUrl())
                .build();

        repo.save(v);
        return toDto(v);
    }

    @Transactional(readOnly = true)
    public List<AppVersionResponse> getAll() {
        return repo.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AppVersionResponse get(Long id) {
        AppVersion v = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("version not found"));
        return toDto(v);
    }

    @Transactional
    public AppVersionResponse update(Long id, AppVersionUpdateRequest req) {
        AppVersion v = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("version not found"));

        v.setLatestVersion(req.getLatestVersion());
        v.setMinRequiredVersion(req.getMinRequiredVersion());
        v.setReleaseNotes(req.getReleaseNotes());
        v.setForceUpdate(req.isForceUpdate());
        v.setDownloadUrl(req.getDownloadUrl());

        return toDto(v);
    }

    @Transactional
    public void delete(Long id) {
        repo.deleteById(id);
    }

    /** 플랫폼별 최신 버전 조회 */
    @Transactional(readOnly = true)
    public AppVersionResponse getLatest(AppPlatform platform) {
        AppVersion v = repo.findTopByPlatformOrderByIdDesc(platform)
                .orElseThrow(() -> new EntityNotFoundException("version not found for " + platform));
        return toDto(v);
    }
}