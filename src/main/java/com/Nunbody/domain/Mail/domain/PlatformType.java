package com.Nunbody.domain.Mail.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum PlatformType {
    NAVER("NAVER"),
    GOOGLE("GOOGLE");

    private final String stringPlatfromType;
    public static PlatformType getEnumPlatfromTypeFromStringPlatfromType(String stringPlatfromType) {
        return Arrays.stream(values())
                .filter(platformType -> platformType.stringPlatfromType.equals(stringPlatfromType))
                .findFirst()
                .orElseThrow(null);
    }
}
