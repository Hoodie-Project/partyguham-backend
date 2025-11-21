package com.partyguham.banner.controller;

import com.partyguham.banner.dto.request.BannerCreateRequest;
import com.partyguham.banner.dto.request.BannerUpdateRequest;
import com.partyguham.banner.dto.response.BannerResponse;
import com.partyguham.banner.dto.response.BannerListResponse;
import com.partyguham.banner.entity.BannerPlatform;
import com.partyguham.banner.service.BannerService;
import com.partyguham.common.annotation.ApiV2Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiV2Controller
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    // 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BannerResponse create(@RequestBody BannerCreateRequest req) {
        return bannerService.create(req);
    }

    // 단건 조회
    @GetMapping("/{id}")
    public BannerResponse get(@PathVariable Long id) {
        return bannerService.get(id);
    }

    // 목록 조회 (platform 필터 optional)
    @GetMapping
    public BannerListResponse list(
            @RequestParam(required = false) BannerPlatform platform
    ) {
        return bannerService.list(platform);
    }

    // 수정
    @PutMapping("/{id}")
    public BannerResponse update(@PathVariable Long id,
                                 @RequestBody BannerUpdateRequest req) {
        return bannerService.update(id, req);
    }

    // 삭제
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bannerService.delete(id);
    }
}