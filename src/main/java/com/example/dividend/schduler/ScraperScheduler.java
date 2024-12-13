package com.example.dividend.schduler;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.CompanyRepository;
import com.example.dividend.persist.DividendRepository;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    @Scheduled(cron = "${scheduler.scrap.yahoo}") // 매일 정각 수행
    public void yahooFinanceScheduling() {
        log.info("Scraping scheduler is started.");

        // 1. 저장된 회사 목록 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        for (CompanyEntity company : companies) {
            log.info("Scraping -> " + company.getName());

            // 2. 회사마다 배당금 정보 새로 스크래핑
            ScrapedResult scrapResult = this.yahooFinanceScraper.scrap(
                    new Company(company.getTicker(), company.getName()));

            // 3. 스크래핑한 배당금 정보 중 DB에 없는 정보 저장
            scrapResult.getDividends().stream()
                    // dividend 모델 -> dividend 엔티티로 매핑
                    .map(e -> new DividendEntity(company.getId(), e))
                    // dividend 엔티티 -> 저장
                    .forEach(e -> {
                        boolean exists =
                                this.dividendRepository.existsByCompanyIdAndDate(
                                        e.getCompanyId(), e.getDate());
                        if(!exists) {
                            this.dividendRepository.save(e);
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000); //3초 일시정지

            } catch (InterruptedException e) {
                // 현재스레드에 인터럽트 걸기
                Thread.currentThread().interrupt();
            }
        }
        log.info("Scraping scheduler is end.");

    }

}
