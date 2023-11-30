package com.Nunbody.domain.Mail.repository;

import com.Nunbody.domain.Mail.domain.MailBody;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
public interface MailBodyRepository extends MongoRepository<MailBody,String> {

}
