package com.example.dividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 토큰 헤더정보
    // ex) Authorization : Bearer aaaa-bbbb-cccc

    // 1. 토큰정보를 가지는 사용자정의 헤더키
    //    토큰은 HTTP 프로토콜에서 헤더에 포함되는 정보.
    //    어떤 기준으로 토큰을 주고받을지 결정 = 헤더의 키를 결정.
    public static final String TOKEN_HEADER = "Authorization";
    // 2. 인증타입
    //    JWT 토큰을 사용하는 경우에는 토큰 앞에 Bearer를 붙여준다.
    public static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    /**
     * 토큰 유효성 검증 필터
     * : token 정보가 유효히면 SecurityContext에 인증정보 추가.
     *   아니면 다음 필터 진행.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 토큰값 반환
        String token = this.resolveTokenFromRequest(request);
        // 2. 토큰 유효성 검증
        if(StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
            // 3. JWT토큰정보 -> 스프링시큐리티 인증정보로 변환
            Authentication auth = this.tokenProvider.getAuthentication(token);
            // 4. 시큐리티 컨텐스트에 인증정보 추가
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // filterChain.doFilter() : 필터가 연속적으로 실행될 수 있도록함.
         filterChain.doFilter(request, response);
    }

    // 토큰값 반환 : request header에서 토큰정보 get
    private String resolveTokenFromRequest(HttpServletRequest request) {
        // getHeader(key) : 헤더 정보에서 key에 해당하는 value값 반환.
        String token = request.getHeader(TOKEN_HEADER);

        // 토큰 유효성여부 확인
        if(!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            // prefix를 제외한 실제 토큰값 반환
            return token.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
