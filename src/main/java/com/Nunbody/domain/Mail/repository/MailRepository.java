package com.Nunbody.domain.Mail.repository;

import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.domain.PlatformType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailRepository extends JpaRepository<MailHeader,Long> {
    Page<MailHeader> findAllByMemberIdOrderByDate(Long id, Pageable pageable);
    List<MailHeader> findAllByMemberIdOrderByDate(Long id);
    Page<MailHeader> findAllByMemberIdAndPlatformTypeOrderByDate(Long memberId, PlatformType platformType, Pageable pageable);
}
