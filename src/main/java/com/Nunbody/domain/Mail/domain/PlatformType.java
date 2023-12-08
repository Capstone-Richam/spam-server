package com.Nunbody.domain.Mail.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum PlatformType {
    NAVER("NAVER"),
    GOOGLE("GOOGLE");


    private final String stringPlatformType;
    public static PlatformType getEnumPlatformTypeFromStringPlatformType(String stringPlatformType) {
        return Arrays.stream(values())
                .filter(platformType -> platformType.stringPlatformType.equals(stringPlatformType))
                .findFirst()
                .orElseThrow(null);
    }
}
