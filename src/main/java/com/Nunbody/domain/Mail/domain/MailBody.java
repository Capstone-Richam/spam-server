package com.Nunbody.domain.Mail.domain;

import javax.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mail_body")
@Data
@Builder
public class MailBody {
    @Id
    private String id;
    private String mailId;
    private String content;


}

