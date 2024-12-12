package com.example.dividend.persist;

import com.example.dividend.persist.entity.DividendEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DividendRepository extends JpaRepository<DividendEntity, Long> {
    // companyId에 대한 회사정보 목록 조회
    List<DividendEntity> findAllByCompanyId(Long companyId);

    // 복합유니크키(companyId, date)에 대한 회사정보 존재여부 조회
    boolean existsByCompanyIdAndDate(Long companyId, LocalDateTime dateTime);
}
