package com.Nunbody.domain;

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
/*static class MailList {
    private String host;
    public ArrayList<MailBody> data;

    public MailList() {
        this.data = new ArrayList<MailBody>();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public ArrayList<MailBody> getData() {
        return data;
    }

    public void addData(MailBody mail) {
        this.data.add(mail);
    }
}*/
