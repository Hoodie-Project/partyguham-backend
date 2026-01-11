package com.partyguham.verson.controller;

import com.partyguham.common.annotation.ApiV2Controller;

import com.partyguham.verson.dto.request.AppVersionCreateRequest;
import com.partyguham.verson.dto.request.AppVersionUpdateRequest;
import com.partyguham.verson.dto.response.AppVersionResponse;
import com.partyguham.verson.entity.AppPlatform;
import com.partyguham.verson.service.AppVersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ApiV2Controller
@RequiredArgsConstructor
@RequestMapping("/app/versions")
public class AppVersionController {

    private final AppVersionService service;

    // ---------- Admin용 CRUD ----------

    @PostMapping
    public AppVersionResponse create(@RequestBody AppVersionCreateRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<AppVersionResponse> list() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AppVersionResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public AppVersionResponse update(@PathVariable Long id,
                                     @RequestBody AppVersionUpdateRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ---------- 앱에서 쓰는 “플랫폼별 최신 버전 조회” ----------

    /**
     * 예: GET /api/v2/app/versions/latest?platform=android
     */
    @GetMapping("/latest")
    public AppVersionResponse latest(@RequestParam AppPlatform platform) {
        return service.getLatest(platform);
    }
}