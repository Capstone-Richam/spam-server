package com.Nunbody.domain.Mail.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Table(name = "mailList")
@Entity
@Setter
public class MailList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String host;
    @Builder.Default
    public ArrayList<Mail> data = new ArrayList<>();


    public void addData(Mail mail) {
        this.data.add(mail);
    }
}
