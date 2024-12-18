package com.example.dividend.service;

import com.example.dividend.model.Auth;
import com.example.dividend.persist.MemberRepository;
import com.example.dividend.persist.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService { // UserDetailsService 상속받아 서비스 구현
    // PasswordEncoder
    // : 인코딩된 패스워드 정보를 DB에 저장하기 위해 사용.
    //   PasswordEncoder 클래스를 통해 인코딩을 수행하기 위해서는
    //   어떤 구현체를 사용할 것인지 직접 정의해 빈 등록이 필요.
    //   이는 AppConfig.java 파일에서 정의.
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    // loadUserByUsername() : 스프링 시큐리티 기능을 사용하기위한 필수구현 메서드
    // UserDetails 반환타입
    // : memberRepository.findByUsername() 메서드의 반환타입은 Optional<MemberEntity>이다.
    //   반환값이 null이 아니면 MemberEntity 타입으로 값을 반환한다.
    //   MemberEntity는 UserDetails 인터페이스의 구현체이므로
    //   loadUserByUsername() 메서드는 다형성에 의해 MemberEntity 타입으로도 반환가능.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    /** 회원가입 */
    public MemberEntity register(Auth.SignUp member) {
        // 사용자아이디 중복체크
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if(exists) {
            throw new RuntimeException("이미 사용중인 아이디 입니다.");
        }

        // 비밀번호 인코딩
        member.setPassword(this.passwordEncoder.encode(member.getPassword()));

        // 회원정보 저장
        MemberEntity result = this.memberRepository.save(member.toEntity());

        return result;
    }

}
