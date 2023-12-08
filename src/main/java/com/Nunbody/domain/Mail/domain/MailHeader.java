package com.Nunbody.domain.Mail.domain;

import com.Nunbody.domain.member.domain.Member;
import com.mongodb.lang.Nullable;
import lombok.*;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "mail")
@Entity
public class MailHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String fromPerson;
    private String date;
    @Enumerated(EnumType.STRING)
    @Nullable
    private PlatformType platformType;
    private String topKeyword;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    public void updateTopKeyword(String keyword) {
        this.topKeyword = keyword;
    }
}
