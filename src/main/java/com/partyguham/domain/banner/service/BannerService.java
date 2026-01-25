package com.partyguham.domain.banner.service;

import com.partyguham.domain.banner.dto.request.BannerUpdateRequest;
import com.partyguham.domain.banner.dto.response.BannerListResponse;
import com.partyguham.domain.banner.dto.response.BannerResponse;
import com.partyguham.domain.banner.entity.Banner;
import com.partyguham.domain.banner.entity.BannerPlatform;
import com.partyguham.domain.banner.repository.BannerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;

    // 내부 공통 변환 메서드
    private BannerResponse toResponse(Banner b) {
        return BannerResponse.from(b);
    }

    /**
     * 배너 생성 (이미지는 S3 key 기준으로 저장)
     * - 컨트롤러에서 S3 업로드 후 imageKey 넘겨주는 구조를 추천
     */
    @Transactional
    public BannerResponse create(BannerPlatform platform, String title, String imageKey, @Nullable String link) {
        Banner banner = Banner.builder()
                .platform(platform)
                .title(title)
                .image(imageKey)   // S3 key 저장
                .link(link)
                .build();

        Banner saved = bannerRepository.save(banner);
        return toResponse(saved);
    }

    /**
     * 단건 조회
     */
    @Transactional(readOnly = true)
    public BannerResponse get(Long id) {
        Banner b = bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("banner not found"));
        return toResponse(b);
    }

    /**
     * 리스트 조회 (platform 옵션 필터)
     */
    @Transactional(readOnly = true)
    public BannerListResponse list(@Nullable BannerPlatform platform) {

        List<Banner> banners = (platform == null)
                ? bannerRepository.findAll()
                : bannerRepository.findByPlatform(platform);

        List<BannerResponse> responses = banners.stream()
                .map(BannerResponse::from)
                .toList();

        return BannerListResponse.builder()
                .total(banners.size())
                .banner(responses)
                .build();
    }

    /**
     * 배너 수정
     */
    @Transactional
    public BannerResponse update(Long id, BannerUpdateRequest req) {
        Banner b = bannerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("banner not found"));

        if (req.getPlatform() != null) {
            b.setPlatform(req.getPlatform());
        }
        if (req.getTitle() != null) {
            b.setTitle(req.getTitle());
        }
        if (req.getImage() != null) {
            b.setImage(req.getImage());   // 필요하면 S3 key 교체
        }
        // link는 null도 허용
        b.setLink(req.getLink());

        return toResponse(b);
    }

    /**
     * 배너 삭제
     * (S3 삭제는 컨트롤러나 별도 Facade에서 banner.getImage() 기반으로 처리하는 걸 추천)
     */
    @Transactional
    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }
}