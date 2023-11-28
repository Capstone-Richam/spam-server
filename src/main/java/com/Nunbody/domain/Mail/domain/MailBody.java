package com.Nunbody.domain.Mail.domain;

import javax.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@Builder
@ToString
@Document(collection = "mail_body")
public class MailBody {
    @Id
    private String id;
    @Field(name = "mail_id")
    private String mailId;
    @Field(name = "content")
    private String content;
}

