package com.Nunbody.domain.Mail.repository;

import com.Nunbody.domain.Mail.domain.MailHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailRepository extends JpaRepository<MailHeader,Long> {
    Page<MailHeader> findAllByMemberId(Long id, Pageable pageable);
    List<MailHeader> findAllByMemberId(Long id);
}
