package com.Nunbody.domain.Mail.service;


import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.domain.Mail;
import com.Nunbody.domain.Mail.domain.MailList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.mail.*;
import java.util.Properties;

@Service
public class MailService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public MailBody Test(){
        MailBody mailBody;
        mailBody = MailBody.builder()
                .mailId("1")
                .content("안녕하세요 최호연이라고 합니다. 이렇게 연락드리게 되어서 대단히 죄송합니다")
                .build();

        return mongoTemplate.insert(mailBody);
    }
    public MailList getMail(String host){
        MailList naverMail = MailList.builder()
                .host(host)
                .build();
        naverMail.setHost(host);

        /** naver mail */
        final String naverHost = "imap.naver.com";
        final String naverId = "qkrwlstjr0131";
        final String naverPassword = "beakgugong1!";

        try {
            Properties prop = new Properties();
            prop.put("mail.imap.host", naverHost);
            prop.put("mail.imap.port", 993);
            prop.put("mail.imap.ssl.enable", "true");
            prop.put("mail.imap.ssl.protocols", "TLSv1.2");
            prop.put("mail.store.protocol", "imap");


            // Session 클래스 인스턴스 생성
            Session session = Session.getInstance(prop);

            // Store 클래스
            Store store = session.getStore("imap");
            store.connect(naverHost, naverId, naverPassword);

            // 받은 편지함
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();
            Mail mailData;

//            for(Message message: messages) {
//                mailData = new Mail();
//                mailData.setTitle(message.getSubject());
//                mailData.setFrom(message.getFrom()[0].toString());
//                mailData.setContent(message.getContent().toString());
//                naverMail.addData(mailData);
//            }

            for(int i=0;i<100;i++){
                mailData = Mail.builder()
                                .title(messages[i].getSubject())
                                .from(messages[i].getFrom()[0].toString())
                                .content(messages[i].getContent().toString())
                                .build();

                naverMail.addData(mailData);
            }


            // 폴더와 스토어 닫기
            folder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return naverMail;
    }
}
