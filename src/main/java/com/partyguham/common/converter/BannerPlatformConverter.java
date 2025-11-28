package com.partyguham.common.converter;

import com.partyguham.banner.entity.BannerPlatform;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BannerPlatformConverter implements Converter<String, BannerPlatform> {

    @Override
    public BannerPlatform convert(String source) {
        return BannerPlatform.valueOf(source.toUpperCase());
    }
}