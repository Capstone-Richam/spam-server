package com.Nunbody.domain.Mail.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.*;


import javax.persistence.*;
import java.util.ArrayList;


@Builder
public class MailList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String host;
    @Builder.Default
    public ArrayList<MailHeader> data = new ArrayList<>();

    public void addData(MailHeader mailHeader) {
        data.add(mailHeader);
    }
}
