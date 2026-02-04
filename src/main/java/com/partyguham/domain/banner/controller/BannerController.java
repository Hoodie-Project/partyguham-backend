package com.partyguham.domain.banner.controller;

import com.partyguham.domain.banner.dto.request.BannerCreateRequest;
import com.partyguham.domain.banner.dto.request.BannerUpdateRequest;
import com.partyguham.domain.banner.dto.response.BannerResponse;
import com.partyguham.domain.banner.dto.response.BannerListResponse;
import com.partyguham.domain.banner.entity.BannerPlatform;
import com.partyguham.domain.banner.service.BannerService;
import com.partyguham.global.annotation.ApiV2Controller;
import com.partyguham.infra.s3.S3FileService;
import com.partyguham.infra.s3.S3Folder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@ApiV2Controller
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;
    private final S3FileService s3FileService;

    // 단건 조회
    @GetMapping("/{id}")
    public BannerResponse get(@PathVariable Long id) {
        return bannerService.get(id);
    }

    // 목록 조회 (platform 필터 optional)
    @GetMapping
    public BannerListResponse list(
            @RequestParam() BannerPlatform platform
    ) {
        return bannerService.list(platform);
    }


    // 생성
    @PostMapping("/banner")
    public BannerResponse createBanner(
            @RequestPart MultipartFile image,
            @RequestPart BannerCreateRequest req
    ) {
        String key = s3FileService.upload(image, S3Folder.BANNER);

        return bannerService.create(
                req.getPlatform(),
                req.getTitle(),
                key,
                req.getLink()
        );
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