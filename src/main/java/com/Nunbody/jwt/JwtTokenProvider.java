package com.Nunbody.jwt;


import com.Nunbody.token.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.naming.AuthenticationException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private String secretKey;
    private long validityInMilliseconds;
    private final long ACCESS_TOKEN_VALID_TIME = (1000*60*60*24); //day
    private final long REFRESH_TOKEN_VALID_TIME = (1000*60*60*24*7); //week
    @Value("aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789AbCdEfGhIjKlMnOpQrStUvWxYz")
    private String baseSecretKey;
    public JwtTokenProvider(@Value("&{security.jwt.token.secret-key}") String secretKey, @Value("${security.jwt.token.expire-length}") long validityInMilliseconds) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.validityInMilliseconds = validityInMilliseconds;
    }
    public TokenInfo createAccessToken(Long id) {
        return createToken(id , ACCESS_TOKEN_VALID_TIME);
    }

    public TokenInfo createRefreshToken(Long id) {
        return createToken(id, REFRESH_TOKEN_VALID_TIME);
    }

    // 토큰 생성
    public TokenInfo createToken(Long id, long validTime) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(id));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validTime); // 유효기간 계산 (지금으로부터 + 유효시간)
        logger.info("now: {}", now);
        logger.info("validity: {}", validity);

        String token = Jwts.builder()
                .setClaims(claims) // sub 설정
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화방식?
                .compact();

        token = "Bearer " + token;
        return new TokenInfo(token, validity.getTime()- now.getTime());
    }
    public String resolveToken(String token) {
        if(token.startsWith("Bearer ")) {
            return token.replace("Bearer ", "");
        }
        return null;
    }
    public Claims parseClaims(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(baseSecretKey));
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        try {
            claims.get("email");
        } catch(Exception e) {
            try {
                throw new AuthenticationException("Jwt 토큰에 이메일이 존재하지 않습니다.");
            } catch (AuthenticationException ex) {
                ex.printStackTrace();
            }
        }
        Collection<GrantedAuthority> authorities =
                Arrays.stream(claims.get("ROLE_").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        UserDetails userDetails = new User(claims.get("email").toString(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    // 토큰에서 값 추출
    public Long getSubject(String token) {
        return Long.valueOf(Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    // 유효한 토큰인지 확인
    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return !claims.getBody().getExpiration().before(new Date());
    }
}
