package com.example.dividend.persist.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "MEMBER")
public class MemberEntity implements UserDetails { // UserDetails : 스프링시큐리티에서 지원해주는 기능 사용을 위해 해당 인터페이스 구현.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    // 참고 블로그 : https://velog.io/@thsruddl77/Basic-attribute-type-should-not-be-a-container
    @ElementCollection // 1:N의 매핑으로 테이블에 데이터가 저장.
    private List<String> roles; // 사용자가 여러 권한을 가질 수 있음.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // roles 정보를 SimpleGrantedAuthority로 매핑
        // -> 스프링 시큐리티에서 지원하는 role 관련기능을 쓰기 위함.
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
