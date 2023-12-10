package com.Nunbody.domain.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;

@AllArgsConstructor
@Builder
@Getter
public class RefreshToken {
    @Id
    private Long id;
    private String refreshToken;

    public static RefreshToken createRefreshToken(Long userId, String refreshToken) {
        return RefreshToken.builder()
                .id(userId)
                .refreshToken(refreshToken)
                .build();
    }
}

