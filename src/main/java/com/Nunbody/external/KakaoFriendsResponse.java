package com.Nunbody.external;

import com.Nunbody.domain.Kakao.service.dto.Friend;
import lombok.Data;

import java.util.List;

@Data
public class KakaoFriendsResponse {
    private String after_url;
    private List<Friend> elements;
    private int total_count;
    private int favorite_count;
}
