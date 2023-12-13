package com.Nunbody.domain.Mail.domain;

import com.Nunbody.domain.member.domain.Member;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "mail_member")
@Entity
@Setter
public class MailMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_member_id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne
    @JoinColumn(name = "mail_id")
    private MailHeader mailHeader;

    public static MailMember createMailMember(Member member, MailHeader mailHeader) {
        return MailMember.builder()
                .member(member)
                .mailHeader(mailHeader)
                .build();
    }
}
