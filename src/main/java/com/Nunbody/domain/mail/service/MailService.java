package com.Nunbody.domain.Mail.service;


import com.Nunbody.domain.Mail.domain.MailBody;
import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.domain.MailList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.Nunbody.domain.Mail.repository.MailBodyRepository;
import com.Nunbody.domain.Mail.repository.MailRepository;
import com.Nunbody.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MailService {

    private final MongoTemplate mongoTemplate;
    private final MailBodyRepository mailBodyRepository;
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    private final Pattern pattern = Pattern.compile("<(.*?)>");
    private Matcher matcher;
    public MailBody Test(){
        MailBody mailBody;
        mailBody = MailBody.builder()
                .mailId("1")
                .content("안녕하세요 최호연이라고 합니다. 이렇게 연락드리게 되어서 대단히 죄송합니다")
                .build();

        return mongoTemplate.insert(mailBody);
    }
    public MailList getMail(Long userId){
        MailList naverMail = MailList.builder()
                .userId(userId)
                .build();

        /** naver mail */
        final String naverHost = "imap.naver.com";
        final String naverId = "haulqogustj";
        final String naverPassword = "qogustj50@";
//        naverMail.setHost(host);



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
//            MailHeader mailData;

            for(int i=0;i<10;i++){
//                mailHeaderData = new MailHeader();
               MailHeader mailHeaderData = MailHeader.builder()
                       .member(memberRepository.findById(userId).orElseThrow())
                            .title(messages[i].getSubject())
                            .fromPerson(messages[i].getFrom()[0].toString())
                            .build();
                mailRepository.save(mailHeaderData);
//                naverMail.addData(mailHeaderData);
                MailBody mailBody = MailBody.builder()
                        .content(messages[i].getContent().toString())
                        .mailId(mailHeaderData.getId().toString())
                        .build();
                mailBodyRepository.save(mailBody);

            }


//            for(int i=0;i<100;i++){
//                matcher = pattern.matcher(messages[i].getFrom()[0].toString());
//                if(matcher.find()) {
//                    String fromPerson = matcher.group(1);
//                    mailHeaderData = MailHeader.builder()
//                            .title(messages[i].getSubject())
//                            .fromPerson(fromPerson)
//                            .build();
//                }
//                else {
//                    mailHeaderData = MailHeader.builder()
//                            .title(messages[i].getSubject())
//                            .fromPerson(messages[i].getFrom()[0].toString())
//                            .build();
//                }
//                naverMail.addData(mailHeaderData);
//            }


            // 폴더와 스토어 닫기
            folder.close(false);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return naverMail;
    }

}
