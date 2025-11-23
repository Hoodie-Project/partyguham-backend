package com.partyguham.admin.version.controller;

import com.partyguham.admin.version.VersionEntity;
import com.partyguham.admin.version.VersionService;
import com.partyguham.common.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@ApiV2Controller
@RequestMapping("/api/versions")
@RequiredArgsConstructor
public class VersionController {

    private final VersionService versionService;

    @PostMapping
    public VersionEntity create(@RequestBody VersionEntity version) {
        return versionService.create(version);
    }

    @GetMapping
    public Page<VersionEntity> getAll(Pageable pageable) {
        return versionService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public VersionEntity getById(@PathVariable Long id) {
        return versionService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 버전이 존재하지 않습니다: " + id));
    }

    @PutMapping("/{id}")
    public VersionEntity update(@PathVariable Long id, @RequestBody VersionEntity version) {
        return versionService.update(id, version);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        versionService.delete(id);
    }
}