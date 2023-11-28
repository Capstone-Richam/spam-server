package com.Nunbody.domain.Mail.domain;

<<<<<<< HEAD:src/main/java/com/Nunbody/domain/Mail/domain/Mail.java
import com.Nunbody.domain.member.domain.Member;
import lombok.*;
=======
>>>>>>> aa7adcf7aec8796f91a96c6ffcb3740f6cecd154:src/main/java/com/Nunbody/domain/Mail/domain/MailHeader.java

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.*;
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
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;


}
