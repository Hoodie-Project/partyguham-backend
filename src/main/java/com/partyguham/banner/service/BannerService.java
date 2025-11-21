package com.partyguham.banner.service;

import com.partyguham.banner.dto.request.BannerCreateRequest;
import com.partyguham.banner.dto.request.BannerUpdateRequest;
import com.partyguham.banner.dto.response.BannerListResponse;
import com.partyguham.banner.dto.response.BannerResponse;
import com.partyguham.banner.entity.Banner;
import com.partyguham.banner.entity.BannerPlatform;
import com.partyguham.banner.repository.BannerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    private BannerResponse toResponse(Banner b) {
        return BannerResponse.builder()
                .id(b.getId())
                .platform(b.getPlatform())
                .title(b.getTitle())
                .image(b.getImage())
                .link(b.getLink())
                .build();
    }

    @Transactional
    public BannerResponse create(BannerCreateRequest req) {
        Banner banner = Banner.builder()
                .platform(req.getPlatform())
                .title(req.getTitle())
                .image(req.getImage())
                .link(req.getLink())
                .build();

        Banner saved = bannerRepository.save(banner);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BannerResponse get(Long id) {
        Banner b = bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("banner not found"));
        return toResponse(b);
    }

    @Transactional(readOnly = true)
    public BannerListResponse list(BannerPlatform platform) {

        List<Banner> banners = (platform == null)
                ? bannerRepository.findAll()
                : bannerRepository.findByPlatform(platform);

        List<BannerResponse> responses = banners.stream()
                .map(b -> BannerResponse.builder()
                        .id(b.getId())
                        .status(b.getStatus().toJson())      // BaseEntity
                        .createdAt(b.getCreatedAt().toString())
                        .updatedAt(b.getUpdatedAt().toString())
                        .platform(b.getPlatform())  // enum -> 소문자
                        .title(b.getTitle())
                        .image(b.getImage())
                        .link(b.getLink())
                        .build()
                ).toList();

        return BannerListResponse.builder()
                .total(banners.size())
                .banner(responses)
                .build();
    }

    @Transactional
    public BannerResponse update(Long id, BannerUpdateRequest req) {
        Banner b = bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("banner not found"));

        if (req.getPlatform() != null) b.setPlatform(req.getPlatform());
        if (req.getTitle() != null) b.setTitle(req.getTitle());
        if (req.getImage() != null) b.setImage(req.getImage());
        b.setLink(req.getLink()); // null 허용

        return toResponse(b);
    }

    @Transactional
    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }
}