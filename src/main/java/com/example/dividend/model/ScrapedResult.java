package com.example.dividend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ScrapedResult {
    // 스크래핑한 회사에 대한 company 인스턴스
    private Company company;

    // 스크래핑한 배당금 정보목록을 담은 dividendEntities 인스턴스
    // (한 회사는 여러 배당금 정보를 가짐)
    private List<Dividend> dividendEntities;

    // 기본생성자
    public ScrapedResult() {
        this.dividendEntities = new ArrayList<>();
    }
}
