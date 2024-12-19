package com.example.dividend.security;

import com.example.dividend.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/** 토큰 생성을 위한 클래스 */
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String KEY_ROLES = "roles";
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour

    private final MemberService memberService;

    @Value("{spring.jwt.secret}")
    private String secretKey;

    /**
     * 토큰 생성(발급)
     * @param username 사용자명(사용자아이디)
     * @param roles 사용자권한 목록
     * @return JWT 토큰
     */
    public String generateToken(String username, List<String> roles) {
        // Claims : 사용자 권한정보를 저장하기위한 Claims 생성
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles); // 사용자권한

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now) // 토큰이 생성된 시간
                    .setExpiration(expiredDate) // // 토큰 만료 시간
                    .signWith(SignatureAlgorithm.HS512, this.secretKey) // 서명방식 지정 -> signWith(사용할 시그니처 암호화 알고리즘 지정, 암호화에 사용할 비밀키)
                    .compact();
    }

    // generateToken 생성 시 Jwts.claims().setSubject(username)한 값 return
    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    /**
     * 토큰 유효성 검증 (만료여부확인)
     * @param token 토큰값
     * @return true/false
     */
    public boolean validateToken(String token) {
        // 토큰값이 비어있으면 유효하지 않은 토큰 -> false 반환
        if(!StringUtils.hasText(token)) return false;

        Claims claims = this.parseClaims(token);
        // 만료시간까지 남은시간을 확인해 만료여부 반환
        // claims.getExpiration().before(new Date())
        // : 토큰 만료시간이 현재시간보다 이전인지 아닌지 여부 판단
        return !claims.getExpiration().before(new Date());
    }

    // 생성된 토큰 유효성 검증 (토큰으로부터 클레임정보 가져오기)
    private Claims parseClaims(String token) {
        try {
            // 클레임정보 가져오기
            return Jwts.parser().
                    setSigningKey(this.secretKey). // 서명 시 사용한 비밀키 지정
                            parseClaimsJws(token).
                    getBody();

            // 토큰 만료시간 이후 토큰정보를 가져오려는 경우 ExpiredException 발생
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    /**
     * JWT토큰정보 -> 스프링시큐리티 인증정보로 변환
     * @param jwt JWT 토큰
     * @return Authentication 스프링에서 지원해주는 형태의 토큰 반환
     */
    @Transactional
    public Authentication getAuthentication(String jwt) {
        UserDetails userDetails =
                this.memberService.loadUserByUsername(this.getUsername(jwt));
        // 스프링에서 지원해주는 형태의 토큰으로 변경.
        // 유저정보, 빈값, 권한정보 전달.
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
    }

}
