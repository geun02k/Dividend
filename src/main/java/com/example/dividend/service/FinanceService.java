package com.example.dividend.service;

import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividendRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    /** 배당금내역 조회 */
    public ScrapedResult getDividendByCompanyName(String companyName) {
        // 1. 회사명 기준 회사정보 조회
        CompanyEntity companyEntity = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        // 2. 조회된 회사정보의 companyId를 이용한 배당금 목록 조회
        List<DividendEntity> dividendEntities =
                this.dividendRepository.findAllByCompanyId(companyEntity.getId());

        // 3. 결과 조홥 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(dividendEntity ->
                        Dividend.builder()
                                .date(dividendEntity.getDate())
                                .dividend(dividendEntity.getDividend())
                                .build())
                .collect(Collectors.toList());
        Company company = Company.builder()
                .ticker(companyEntity.getTicker())
                .name(companyEntity.getName())
                .build();
        return new ScrapedResult(company, dividends);
    }
}
