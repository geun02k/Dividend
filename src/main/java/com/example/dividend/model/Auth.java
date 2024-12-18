package com.example.dividend.model;

import com.example.dividend.persist.entity.MemberEntity;
import lombok.Data;

import java.util.List;

public class Auth {

    // 회원가입 시 사용을 위한 innerClass 생성
    @Data
    public static class SignUp {
        private String username;
        private String password;
        private List<String> roles; // 사용자가 정하기보다는 내부 로직에서 처리필요.

        // SignUp 클래스 데이터 -> MemberEntity 클래스 데이터로 변환
        public MemberEntity toEntity() {
            return MemberEntity.builder()
                    .username(this.username)
                    .password(this.password)
                    .roles(this.roles)
                    .build();
        }
    }
}
