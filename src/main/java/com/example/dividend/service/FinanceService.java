package com.example.dividend.service;

import com.example.dividend.exception.impl.NoCompanyException;
import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.CacheKey;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividendRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    /** 배당금내역 조회 */
    // @Cacheable
    // : 해당 key값에 대한 Cache 데이터가 Redis Cache에 존재하지 않을 경우 -> 아래의 서비스를 실행
    //   데이터가 존재하는 경우 아래의 서비스 미실행 -> Cahce에 있는 데이터 반환.
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        // Redis Cache 서버에서 데이터를 가져올 때는 해당 로그 출력되지 않음.
        log.info("search company -> " + companyName);

        // 1. 회사명 기준 회사정보 조회
        CompanyEntity companyEntity = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new NoCompanyException());

        // 2. 조회된 회사정보의 companyId를 이용한 배당금 목록 조회
        List<DividendEntity> dividendEntities =
                this.dividendRepository.findAllByCompanyId(companyEntity.getId());

        // 3. 결과 조홥 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(dividendEntity ->
                        new Dividend(dividendEntity.getDate()
                                    ,dividendEntity.getDividend()))
                .collect(Collectors.toList());
        Company company = new Company(companyEntity.getTicker()
                                    , companyEntity.getName());
        return new ScrapedResult(company, dividends);
    }
}
