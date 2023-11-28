package com.Nunbody.domain.Mail.service;


import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.dto.response.MailListResponseDto;
import com.Nunbody.domain.Mail.dto.resquest.ValidateRequestDto;
import com.Nunbody.domain.Mail.repository.MailRepository;

import com.Nunbody.global.error.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.Nunbody.global.error.ErrorCode.IMAP_ERROR;

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
    public String validateConnect(ValidateRequestDto validateRequestDto) throws MessagingException {
        String validate = validateImap(validateRequestDto);
        return validate;
    }
    private String validateImap(ValidateRequestDto validateRequestDto) throws MessagingException {
        try {
            String id = validateRequestDto.getId();
            String password = validateRequestDto.getPassword();
            Properties prop = new Properties();
            prop.put("mail.imap.host", "imap.naver.com");
            prop.put("mail.imap.port", 993);
            prop.put("mail.imap.ssl.enable", "true");
            prop.put("mail.imap.ssl.protocols", "TLSv1.2");
            prop.put("mail.store.protocol", "imap");
            Store store = createStore(prop);
            store.connect("imap.naver.com", id, password);
            return "성공";
        } catch (MessagingException e) {
            String errorMessage = "IMAP 연결 실패: " + e.getMessage();
            // 여기에서 errorMessage를 로깅하거나 필요한 처리를 수행할 수 있습니다.
            throw new InvalidValueException(IMAP_ERROR);

        }
    }
    private Store createStore(Properties prop) throws NoSuchProviderException {
        Session session = Session.getInstance(prop);
        Store store = session.getStore("imap");
        return store;
    }
}
