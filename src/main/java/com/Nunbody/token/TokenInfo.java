package com.Nunbody.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TokenInfo {
    private String accessToken;
    private String refreshToken;

    public static TokenInfo of(String accessToken, String refreshToken) {
        return new TokenInfo(accessToken, refreshToken);
    }
}
