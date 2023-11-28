package com.Nunbody.domain.Mail.repository;

import com.Nunbody.domain.Mail.domain.MailHeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailRepository extends JpaRepository<MailHeader,Long> {
    List<MailHeader> findAllByUserId(Long id);
}
