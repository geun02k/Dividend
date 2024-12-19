package com.example.dividend.persist;

import com.example.dividend.persist.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    // 회사코드(ticker)에 대한 회사정보 존재여부 조회
    boolean existsByTicker(String ticker);

    // 회사명과 일치하는 회사정보 조회
    Optional<CompanyEntity> findByName(String name);

    // 회사코드와 일치하는 회사정보 조회
    Optional<CompanyEntity> findByTicker(String ticker);

    // keyword로 시작하는 회사명 목록 조회 (대소문자구분 무시, Like연산자사용)
    Page<CompanyEntity> findByNameStartingWithIgnoreCase(String keyword, Pageable limit);

}
