package com.partyguham.domain.banner.repository;

import com.partyguham.domain.banner.entity.Banner;
import com.partyguham.domain.banner.entity.BannerPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findByPlatform(BannerPlatform platform);
}