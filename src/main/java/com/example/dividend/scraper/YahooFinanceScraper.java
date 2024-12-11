package com.example.dividend.scraper;

import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper {
    private static final long START_TIME = 86400; // 하루24시간을 sec로 표현 (60초 * 60분 * 24시간)
    // 기존 url -> https://finance.yahoo.com/quote/COKE/history/
    //              ?frequency=1mo
    //              &period1=99153000
    //              &period2=1733789598
    // 불필요정보 제거후 string format을 사용하기위해 변경한 url
    private static final String STATISTICS_URL =
            "https://finance.yahoo.com/quote/%s/history"
            + "?frequency=1mo"
            + "&period1=%d"
            + "&period2=%d";
    // 기존 url -> https://finance.yahoo.com/quote/COKE/
    private static final String SUMMARY_URL =
            "https://finance.yahoo.com/quote/%s";

    /** 회사코드에 대한 배당금내역 스크래핑 */
    public ScrapedResult scrap(Company company) {
        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {
            // System.currentTimeMillis()
            // : 1970/01/01 부터 현재시간까지 경과한 시간을 ms로 가져온 값 출력.
            // System.currentTimeMillis() / 1000
            // : 현재시간을 ms로 가져와 1000으로 나눠 sec로 단위변경
            long now = System.currentTimeMillis() / 1000;
            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);

            // 1. HTML 문서 받아오기
            Connection connect = Jsoup.connect(url);
            Document document = connect
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
                    .header("scheme", "https")
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6")
                    .header("cache-control", "no-cache")
                    .header("pragma", "no-cache")
                    .header("upgrade-insecure-requests", "1")
                    .get();

            // 2. HTML 문서를 파싱 -> Document instance 생성
            // 방법2. 배당금 데이터를 가지는 테이블의 class 속성 이용해 테이블 정보 가져오기
            //       (html 검색했을 때 동일한 css class를 사용하는 곳은 없어서 일단 사용.)
            Elements parsingDivs = document.getElementsByAttributeValue("class", "table yf-j5d1ld noDl");
            if(parsingDivs.isEmpty()) {
               throw new RuntimeException("html 데이터를 가져오지 못했습니다.");
            }
            Element table = parsingDivs.get(0); // <table> 태그 가져오기
            Element tbody = table.children().get(1); // <tbody> 태그 가져오기

            // 3. 필요한 데이터 추출
            // 배당금 정보만 가져오기 = text의 내용이 Dividend로 끝나는 데이터 가져오기
            List<Dividend> dividends = new ArrayList<>();
            for (Element child : tbody.children()) { // 모든 데이터 순회
                String txt = child.text();
                // endsWith("문자열") : 대상 문자열이 특정 문자 또는 문자열로 끝나는지 체크
                if(!txt.endsWith("Dividend")) {
                    continue;
                }
                // txt = "Oct 25, 2024 2.50 Dividend"
                String[] splits = txt.split(" "); // 공백으로 구분
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if(month <= 0) {
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0] + "\n" +
                            year + "/" + month + "/" + day + "->" + dividend);
                }

                // LocalDateTime date = LocalDateTime.of(2020,5,13,6,30)
                // 숫자 -> LocalDateTime 시간으로 변경
                dividends.add(Dividend.builder()
                                    .date(LocalDateTime.of(year, month, day, 0, 0))
                                    .dividend(dividend)
                                    .build());
                //System.out.println(year + "/" + month + "/" + day + "->" + dividend);
            }
            scrapedResult.setDividendEntities(dividends);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return scrapedResult;
    }

    /** 회사코드(ticker)를 받아 해당 회사의 메타정보를 scraping으로 가져와 결과로 반환 */
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker);

        try {
            // 1. HTML 문서 받아오기
            Connection connect = Jsoup.connect(url);
            Document document = connect
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36")
                    .header("scheme", "https")
                    .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("accept-encoding", "gzip, deflate, br")
                    .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6")
                    .header("cache-control", "no-cache")
                    .header("pragma", "no-cache")
                    .header("upgrade-insecure-requests", "1")
                    .get();

            // 2. HTML 문서를 파싱 -> Document instance 생성
            Element h1Tag = document.getElementsByTag("h1").get(1);
            String title = h1Tag.text()
                    .substring(0, h1Tag.text().lastIndexOf(" ("))
                    .replace("\"", "");

            return Company.builder()
                    .ticker(ticker)
                    .name(title)
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
