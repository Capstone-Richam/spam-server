package com.Nunbody.domain.Mail.domain;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import jakarta.persistence.*;


@Data
@Builder
@ToString
@Document(collection = "mail_body")
public class MailBody {
    @Id
    private String id;
    @Field(name = "mailId")
    private Long mailId;
    @Field(name = "content", targetType = FieldType.STRING)
    private String content;


}

