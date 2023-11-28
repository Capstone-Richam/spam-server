package com.Nunbody.domain.Mail.service;


import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.dto.response.MailListResponseDto;
import com.Nunbody.domain.Mail.repository.MailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MailManageService {
    private final MailRepository mailRepository;
    public Page<MailListResponseDto> getMailList(Long id, Pageable pageable){

        Page<MailListResponseDto> mailListResponseDtoList = createMailListResponseDtoList(id,pageable);
        return mailListResponseDtoList;
    }
    private Page<MailListResponseDto> createMailListResponseDtoList(Long id, Pageable pageable){
        Page<MailHeader> mailHeaderPage = mailRepository.findAllByMemberId(id, pageable);
        return mailHeaderPage.map(mailHeader -> MailListResponseDto.of(mailHeader));
    }
}
