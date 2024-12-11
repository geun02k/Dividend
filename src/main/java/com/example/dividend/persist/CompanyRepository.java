package com.example.dividend.persist;

import com.example.dividend.persist.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    // 회사코드(ticker)에 대한 회사정보 존재여부 조회
    boolean existsByTicker(String ticker);

    // 회사명과 일치하는 회사정보 조회
    Optional<CompanyEntity> findByName(String name);

}
