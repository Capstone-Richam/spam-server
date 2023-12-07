package com.Nunbody.domain.Mail.repository;

import com.Nunbody.domain.Mail.domain.MailBody;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.Async;
@EnableMongoRepositories
public interface MailBodyRepository extends MongoRepository<MailBody, String> {
//    @Async
//    MailBody findByMailId(Long mail_id);
    MailBody findByMailId(Long id);
}
