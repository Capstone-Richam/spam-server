package com.Nunbody.domain.member.repository;

import com.Nunbody.domain.member.domain.Keyword;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories
public interface KeywordRepository extends MongoRepository<Keyword,String> {
    Optional<Keyword> findByMemberId(Long memberId);
}
