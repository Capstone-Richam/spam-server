package com.Nunbody.domain.Mail.service;


import com.Nunbody.domain.Mail.domain.MailHeader;
import com.Nunbody.domain.Mail.domain.PlatformType;
import com.Nunbody.domain.Mail.dto.response.MailListResponseDto;
import com.Nunbody.domain.Mail.dto.resquest.ValidateRequestDto;
import com.Nunbody.domain.Mail.repository.MailRepository;

import com.Nunbody.domain.member.domain.Member;
import com.Nunbody.domain.member.repository.MemberRepository;
import com.Nunbody.global.error.exception.InvalidValueException;
import com.Nunbody.global.error.exception.LoginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.Nunbody.global.error.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MailManageService {
    private final MailRepository mailRepository;
    private final MemberRepository memberRepository;
    public Page<MailListResponseDto> getMailList(Long memberId, String type, Pageable pageable){
        Member member =getMember(memberId);
        Page<MailListResponseDto> mailListResponseDtoList = createMailListResponseDtoList(memberId,type,pageable);
        return mailListResponseDtoList;
    }
    private Page<MailListResponseDto> createMailListResponseDtoList(Long memberId, String type,Pageable pageable){
        if(type.isEmpty()){
            Page<MailHeader> mailHeaderPage = mailRepository.findAllByMemberId(memberId, pageable);
            return mailHeaderPage.map(mailHeader -> MailListResponseDto.of(mailHeader));

        }
        Page<MailHeader> mailHeaderPage = mailRepository.findAllByMemberIdAndPlatformType(memberId, PlatformType.getEnumPlatfromTypeFromStringPlatfromType(type),pageable);
        return mailHeaderPage.map(mailHeader -> MailListResponseDto.of(mailHeader));
    }
    public String validateConnect(ValidateRequestDto validateRequestDto) throws MessagingException {
        String validate = validateImap(validateRequestDto);
        return validate;
    }
    private Member getMember(Long memberId){
        return memberRepository.findById(memberId).orElse(null);
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
        } catch (AuthenticationFailedException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Please check your username, password")) {
                // Handle username or password error
                // You can log the error message or perform any other necessary actions
                throw new InvalidValueException(INVALID_EMAIL_ERROR);
            } if (errorMessage.contains("Please check IMAP/SMTP settings in the webmail")) {
                // Handle IMAP/SMTP settings error
                // You can log the error message or perform any other necessary actions
                throw new InvalidValueException(IMAP_ERROR);
            }
        }

        return null;
    }
    private Store createStore(Properties prop) throws NoSuchProviderException {
        Session session = Session.getInstance(prop);
        Store store = session.getStore("imap");
        return store;
    }
//    public MailListResponseDto filtering(Long memberId){
//        List<MailHeader> mailList = mailRepository.findAllByMemberId(memberId);
//
//    }

}
