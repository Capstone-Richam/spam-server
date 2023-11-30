package com.Nunbody.domain.Mail.repository;

import com.Nunbody.domain.Mail.domain.MailBody;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.Async;

public interface MailBodyRepository extends MongoRepository<MailBody, Long> {
    @Async
    MailBody findByMailId(Long mail_id);
}
