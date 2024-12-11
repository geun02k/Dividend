package com.example.dividend.web;

import com.example.dividend.model.Company;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    // 회사검색 자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        // trie에서 keyword로 시작하는 회사 목록 검색
        List<String> result = this.companyService.autocomplete(keyword);
        return ResponseEntity.ok(result);
    }

    // 회사 배당금정보 조회
    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> allCompany = companyService.getAllCompany(pageable);
        return ResponseEntity.ok(allCompany);
    }

    // 회사 및 배당금 정보 저장
    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)) {
            throw new RuntimeException("Ticker is empty.");
        }

        Company company = this.companyService.save(ticker);

        // 자동검색을 위한 trie에 회사명 저장
        this.companyService.addAutocompleteKeyword(company.getName());

        // 회사정보(company) 반환
        return ResponseEntity.ok(company);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
