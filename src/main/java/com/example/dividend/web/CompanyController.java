package com.example.dividend.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class CompanyController {

    // 회사검색 자동완성
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        return null;
    }

    // 회사 배당금정보 조회
    @GetMapping
    public ResponseEntity<?> searchCompany() {
        return null;
    }

    @PostMapping
    public ResponseEntity<?> addCompany() {
        return null;
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
