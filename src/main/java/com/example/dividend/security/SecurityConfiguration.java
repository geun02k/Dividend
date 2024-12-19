package com.example.dividend.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    // 생성한 인증필터
    private final JwtAuthenticationFilter authenticationFilter;

    /** 인증관련 설정 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 1. 인증 제외경로 설정.
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                    .antMatchers("/**/signup", "/**/signin").permitAll()
                .and()
                .addFilterBefore(this.authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 2. 필요권한 설정. (해당 설정파일에서 적용하지 않고 실제 적용할 컨트롤러에서 설정)
    }

    /** 개발관련 인증제외경로 설정 (For 개발의편의성) */
    @Override
    public void configure(final WebSecurity web) throws Exception {
        // 인증을 제외할 경로에 대해 제외처리.
        // : /h2-console 하위 경로로 api를 호출하게 되면 해당 인증정보는 무시.
        //   = 인증정보 없이 해당 경로에 자유롭게 접근허용.
        web.ignoring()
                .antMatchers("/h2-console/**");
    }

    // spring boot 2.x 버전부터 필수구현
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
