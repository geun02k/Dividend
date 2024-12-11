package com.example.dividend.service;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividendRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    // Scraper 인터페이스로 YahooFinanceScraper 구현체를 의존성주입해 사용
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    /** 회사 및 배당금 정보 저장 */
    public Company save(String ticker) {
        // 이미 저장된 회사가 아닌 경우 회사 및 배당금정보 저장.
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if(exists) {
            throw new RuntimeException("Already exists ticker -> " + ticker);
        }

        return this.storeCompanyAndDividend(ticker);
    }

    // 회사와 배당금정보 scraping -> 저장
    private Company storeCompanyAndDividend(String ticker) {
        // 1. ticker를 기준으로 회사 스크래핑
        // yahooFinanceScraper.scrapCompanyByTicker() 메서드는
        // 회샤 정보를 스크래핑 해오면 Company 정보를 반환.
        // 스크래핑 해오지 못하면 null 반환 -> 예외처리 필요
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if(ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("Faild to scrap ticker -> "+ ticker);
        }

        // 2. 회사 정보가 존재할 경우, 회사의 배당금 정보 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 3. 스크래핑 결과 저장 및 반환
        // - 회사정보 저장
        // scrapedResult.getDividendEntities().stream() : List -> stream 타입으로 변환.
        // map() : collection element를 다른 타입으로 매핑 시 사용.
        // collect(Collectors.toList()) : 결과값을 List 타입으로 변환
        CompanyEntity companyEntity = this.companyRepository
                .save(new CompanyEntity(company));

        List<DividendEntity> dividendEntities =
                scrapedResult.getDividendEntities().stream()
                    .map(e -> new DividendEntity(companyEntity.getId(), e))
                    .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }
}
