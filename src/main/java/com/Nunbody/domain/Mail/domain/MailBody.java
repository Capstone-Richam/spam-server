package com.Nunbody.domain.Mail.domain;

import javax.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;


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

