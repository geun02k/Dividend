package com.example.dividend.web;

import com.example.dividend.model.Company;
import com.example.dividend.model.constants.CacheKey;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    private final CacheManager redisCacheManager;

    // 회사검색 자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        // DB에서 keyword로 시작하는 회사 목록 검색
        List<String> result = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(result);
    }

    // 회사 배당금정보 조회
    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> allCompany = companyService.getAllCompany(pageable);
        return ResponseEntity.ok(allCompany);
    }

    // 회사 및 배당금 정보 저장
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("Ticker is empty.");
        }

        Company company = this.companyService.save(ticker);

        // 회사정보(company) 반환
        return ResponseEntity.ok(company);
    }

    // 회사 및 배당금 정보 삭제
    @DeleteMapping("/{ticker}")
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        // 회사 및 배당금 정보 삭제
        String companyName = this.companyService.deleteCompany(ticker);
        // 캐시서버에서 해당 회사의 캐시데이터 삭제
        this.clearFinanceCache(companyName);

        return ResponseEntity.ok(companyName);
    }

    /** 캐시정보 삭제 */
    public void clearFinanceCache(String companyName) {
        // getCache(key)
        // : 해당 key에 해당하는 Cache 데이터 가져오기
        this.redisCacheManager.getCache(CacheKey.KEY_FINANCE).evict(companyName);
    }

}
