package com.Nunbody.domain.Mail.service;

import com.Nunbody.domain.Mail.domain.Mail;
import com.Nunbody.domain.Mail.dto.response.MailListResponseDto;
import com.Nunbody.domain.Mail.repository.MailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MailManageService {
    private final MailRepository mailRepository;
    public List<MailListResponseDto> getMailList(Long id){

        List<MailListResponseDto> mailListResponseDtoList = createMailListResponseDtoList(id);
        return mailListResponseDtoList;
    }
    private List<MailListResponseDto> createMailListResponseDtoList(Long id){
        List<Mail> mailList = mailRepository.findAllByUserId(id);
        return mailList.stream()
                .map(mail ->
                        MailListResponseDto.of(mail))
                .collect(Collectors.toList());
    }
}
