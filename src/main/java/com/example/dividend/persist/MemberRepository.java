package com.example.dividend.persist;

import com.example.dividend.persist.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    // 사용자아이디 기준 회원정보 조회
    Optional<MemberEntity> findByUsername(String username);

    // 해당 사용자아이디 존재여부 조회
    boolean existsByUsername(String username);
}
