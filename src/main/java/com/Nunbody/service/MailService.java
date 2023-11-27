package com.Nunbody.service;

import com.Nunbody.domain.MailBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public MailBody Test(){
        MailBody mailBody;
        mailBody = MailBody.builder()
                .hostId("jinseok")
                .title("안녕하세요. 가짜 메일입니다.")
                .from("hoyoen@naver.com")
                .content("안녕하세요 최호연이라고 합니다. 이렇게 연락드리게 되어서 대단히 죄송합니다")
                .build();

        return mongoTemplate.insert(mailBody);
    }
}
