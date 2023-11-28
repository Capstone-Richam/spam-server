package com.Nunbody.domain.Mail.repository;

import com.Nunbody.domain.Mail.domain.Mail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailRepository extends JpaRepository<Mail,Long> {
    List<Mail> findAllByUserId(Long id);
}
