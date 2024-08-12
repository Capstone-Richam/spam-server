package com.Nunbody.domain.Kakao.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Friend {
    private Long id;
    private String uuid;
    private boolean favorite;
    private String profile_nickname;
    private String profile_thumbnail_image;
}