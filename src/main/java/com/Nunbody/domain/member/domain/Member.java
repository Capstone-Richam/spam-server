package com.Nunbody.domain.member.domain;


import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.global.common.BaseTimeEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<MailHeader> mail= new ArrayList<>();
    public void updateRefreshToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }

}
