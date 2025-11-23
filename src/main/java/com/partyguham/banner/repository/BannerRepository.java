package com.partyguham.banner.repository;

import com.partyguham.banner.entity.Banner;
import com.partyguham.banner.entity.BannerPlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    List<Banner> findByPlatform(BannerPlatform platform);
}