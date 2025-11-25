package com.partyguham.common.converter;

import com.partyguham.auth.oauth.entity.Provider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class StringToProviderConverter implements Converter<String, Provider> {

    @Override
    public Provider convert(String source) {
        try {
            return Provider.from(source);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "unsupported_provider: " + source
            );
        }
    }
}