package com.Nunbody.domain.member.domain;


import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.global.common.BaseTimeEntity;
import lombok.*;

import jakarta.persistence.*;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "member")
@Entity
public class Member extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String account;
    private String password;
    private String name;
    private String naverId;
    private String naverPassword;
    private String gmailId;
    private String gmailPassword;
    private String accessToken;
    private String refreshToken;
    private Date lastTime;
    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<MailHeader> mail = new ArrayList<>();

    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }
    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public void updateLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
