package com.Nunbody.domain.member.service;

import com.Nunbody.domain.member.domain.Keyword;
import com.Nunbody.domain.member.domain.Member;

import com.Nunbody.domain.member.dto.SignInResponseDto;
import com.Nunbody.domain.member.repository.KeywordRepository;
import com.Nunbody.domain.member.repository.MemberRepository;
import com.Nunbody.domain.member.dto.MemberRegisterRequestDto;
import com.Nunbody.exception.auth.InvalidEmailException;
import com.Nunbody.exception.auth.InvalidPasswordException;
import com.Nunbody.global.error.exception.BusinessException;
import com.Nunbody.global.error.exception.InvalidValueException;
import com.Nunbody.jwt.JwtTokenProvider;
import com.Nunbody.token.OAuthToken;
import com.Nunbody.token.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.Nunbody.global.common.EncoderDecoder;

import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.Nunbody.global.error.ErrorCode.ACCOUNT_EXISTS_ERROR;
import static com.Nunbody.global.error.ErrorCode.NAME_EXISTS_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private Logger logger = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final OAuthService oAuthService;

    public OAuthToken getOauthTokenWithCode(String socialLoginType, String code) throws JsonProcessingException {
        ResponseEntity<String> accessTokenResponse = null;
        if ("kakao".equals(socialLoginType)) {
            accessTokenResponse = oAuthService.createPostKakaoRequest(code);
        } else if ("google".equals(socialLoginType)) {
            accessTokenResponse = oAuthService.createPostGoogleRequest(code);
        } else {
            // socialLoginType이 지원되지 않는 경우 예외 처리
            throw new IllegalArgumentException("오류: " + socialLoginType);
        }
        OAuthToken oAuthToken = oAuthService.getAccessToken(accessTokenResponse);
        logger.info("Access Token: {}", oAuthToken.getAccessToken());
        logger.info("refresh Token: {}", oAuthToken.getRefreshToken());
        logger.info("id Token: {}", oAuthToken.getIdToken());
        logger.info(" TokenType: {}", oAuthToken.getTokenType());
        logger.info("expriesin: {}", oAuthToken.getExpiresIn());
        logger.info("scope: {}", oAuthToken.getScope());
        return oAuthToken;
    }

    public void register(MemberRegisterRequestDto resource) {

        Member member;

        member = Member.builder()
                .account(resource.getAccount())
                .password(passwordEncoder.encode(resource.getPassword()))
                .name(resource.getName())
                .naverId(resource.getNaverId())
                .naverPassword(EncoderDecoder.encodeToBase64(resource.getNaverPassword()))
                .gmailId(resource.getGmailId())
                .gmailPassword(EncoderDecoder.encodeToBase64(resource.getGmailPassword()))
                .refreshToken(null).build();

        Member keyMember = memberRepository.save(member);
        Keyword keyword = Keyword.builder()
                .memberId(keyMember.getId())
                .words(new ArrayList<>())
                .build();
        keywordRepository.save(keyword);

    }

        @Transactional
        public SignInResponseDto signIn (String account, String password){
            Member member = memberRepository.findByAccount(account).orElseThrow(() -> new InvalidEmailException("회원정보가 존재하지 않습니다."));
            if (!passwordEncoder.matches(password, member.getPassword())) {

                throw new InvalidPasswordException("잘못된 비밀번호입니다.");
            }
            TokenInfo tokenInfo = oAuthService.issueAccessTokenAndRefreshToken(member);
            member.updateRefreshToken(tokenInfo.getRefreshToken());
            return SignInResponseDto.of(member,tokenInfo);
        }

        public void validateAccount(String account){

            boolean isDuplicate = memberRepository.existsByAccount(account);

            if (isDuplicate) {
                throw new BusinessException(ACCOUNT_EXISTS_ERROR);
                // DuplicateAccountException은 중복 계정이 발견되었을 때 던질 예외 클래스입니다.
                // 이 예외 클래스는 RuntimeException 또는 다른 적절한 예외 클래스를 상속하여 정의해야 합니다.
            }
        }
    public void validateName(String name){

        boolean isDuplicate = memberRepository.existsByName(name);

        if (isDuplicate) {
            throw new InvalidValueException(NAME_EXISTS_ERROR);
            // DuplicateAccountException은 중복 계정이 발견되었을 때 던질 예외 클래스입니다.
            // 이 예외 클래스는 RuntimeException 또는 다른 적절한 예외 클래스를 상속하여 정의해야 합니다.
        }
    }
    }
