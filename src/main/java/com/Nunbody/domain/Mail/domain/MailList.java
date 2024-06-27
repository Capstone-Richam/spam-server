package com.Nunbody.domain.Mail.domain;

import lombok.Builder;

import jakarta.persistence.*;


@Builder
public class MailList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;

}
