package com.Nunbody.service;

import com.Nunbody.domain.Member;

import com.Nunbody.domain.MemberRepository;
import com.Nunbody.dto.MemberRegisterResponseDto;
import com.Nunbody.dto.SignInResponseDto;
import com.Nunbody.exception.auth.InvalidEmailException;
import com.Nunbody.exception.auth.InvalidPasswordException;
import com.Nunbody.jwt.JwtTokenProvider;
import com.Nunbody.token.OAuthToken;
import com.Nunbody.token.TokenInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private Logger logger = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
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

    public void register(MemberRegisterResponseDto resource) {

        Member member;
        if (resource.isHasNaver() && resource.isHasGmail()) {
            member = Member.builder()
                    .account(resource.getAccount())
                    .password(passwordEncoder.encode(resource.getPassword()))
                    .name(resource.getName())
                    .naverId(resource.getNaverId())
                    .naverPassword(resource.getNaverPassword())
                    .gmailId(resource.getGmailId())
                    .gmailPassword(resource.getGmailPassword())
                    .refreshToken(null).build();
        }
        else if (resource.isHasNaver()) {
                member = Member.builder()
                        .account(resource.getAccount())
                        .password(passwordEncoder.encode(resource.getPassword()))
                        .name(resource.getName())
                        .naverId(resource.getNaverId())
                        .naverPassword(resource.getNaverPassword())
                        .refreshToken(null).build();
            } else if (resource.isHasGmail()) {
                member = Member.builder()
                        .account(resource.getAccount())
                        .password(passwordEncoder.encode(resource.getPassword()))
                        .name(resource.getName())
                        .gmailId(resource.getGmailId())
                        .gmailPassword(resource.getGmailPassword())
                        .refreshToken(null).build();
            } else {
                member = Member.builder()
                        .account(resource.getAccount())
                        .password(passwordEncoder.encode(resource.getPassword()))
                        .name(resource.getName())
                        .refreshToken(null).build();
            }
            memberRepository.save(member);

        }

        @Transactional
        public SignInResponseDto signIn (String account, String password){
            Member member = memberRepository.findByAccount(account).orElseThrow(() -> new InvalidEmailException("회원정보가 존재하지 않습니다."));
            if (!passwordEncoder.matches(password, member.getPassword())) {

                throw new InvalidPasswordException("잘못된 비밀번호입니다.");
            }

            TokenInfo accessToken = jwtTokenProvider.createAccessToken(member.getMemberId());
            TokenInfo refreshToken = jwtTokenProvider.createRefreshToken(member.getMemberId());
            member.updateRefreshToken(refreshToken.getToken());
            return new SignInResponseDto(
                    member.getMemberId(), member.getAccount(), member.getName(), member.getNaverId(), member.getNaverPassword(), member.isHasNaver(), member.getGmailId(), member.getGmailPassword(), member.isHasGmail(), accessToken.getToken(), refreshToken.getToken(), accessToken.getExpireTime(), refreshToken.getExpireTime()
            );
        }

    }
