package com.Nunbody.domain.member.repository;


import com.Nunbody.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByAccount(String account);
    @Query("SELECT m.naverId FROM Member m WHERE m.id = :id")
    Optional<String> findNaverIdById(Long id);
    @Query("SELECT m.naverPassword FROM Member m WHERE m.id = :id")
    Optional<String> findNaverPasswordById(Long id);

    @Query("SELECT m.gmailId FROM Member m WHERE m.id = :id")
    Optional<String> findGmailIdById(Long id);
    @Query("SELECT m.gmailPassword FROM Member m WHERE m.id = :id")
    Optional<String> findGmailPasswordById(Long id);
}
