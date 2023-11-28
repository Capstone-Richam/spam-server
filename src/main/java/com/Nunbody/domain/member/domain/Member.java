package com.Nunbody.domain.member.domain;


import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.global.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

import java.util.ArrayList;
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

    private String refreshToken;
    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<MailHeader> mail= new ArrayList<>();
    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

}
