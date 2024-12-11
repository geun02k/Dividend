package com.example.dividend;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.scraper.YahooFinanceScraper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

//@SpringBootApplication
public class DividendApplication {

    public static void main(String[] args) {
//        SpringApplication.run(DividendApplication.class, args);

        YahooFinanceScraper scraper = new YahooFinanceScraper();

//        // YahooFinanceScraper.scrap() 실행 테스트
//        ScrapedResult result = scraper.scrap(Company.builder().ticker("COKE").build());
//        System.out.println(result);
//
        // YahooFinanceScraper.scrapCompanyByTicker() 실행 테스트
        Company company = scraper.scrapCompanyByTicker("MMM");
        System.out.println(company.toString());
//
//         //스크래핑 test
//         dividendInfoScrapingTest();
    }

    // 스크래핑 테스트용 코드
    private static void dividendInfoScrapingTest() {
        // 스크래핑
        // 스크래핑이 잘 수행되는지를 확인하기 위해 main에서 테스트용으로 구현
        // 실제 개발에서는 이렇게 진행하면 안됨.
        try {
            // 1. HTML 문서 받아오기
            // connect(String url)
            // Creates a new Connection (session), with the defined request URL.
            // 정의된 요청 url을 이용해 새로운 연결(세션) 생성
            Connection connect = Jsoup.connect(
                    "https://finance.yahoo.com/quote/COKE/history/?frequency=1mo&period1=99153000&period2=1733789598");
            // connect.get()
            // Execute the request as a GET, and parse the result.
            // 요청을 GET으로 실행하고 파싱한 데이터 Document 타입으로 리턴
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
            //    : key-value값은 html 문서 확인 시
            // getElementsByAttributeValue(String key, String value)
            // Find elements that have attributes whose value contains the match string.
            // html에서 key,value에 해당하는 속성을 가진 element들 찾아서 반환.
            // 강의와 달리 배당금 데이터를 가지는 테이블의 특정 속성값 존재x
            // 방법1. 데이터 해당 테이블의 상위에 특정 속성값을 가지는 div 태그 가져오기
            //       (n번째 자식태그로 존재하는 <table> 추가추출 필요)
            // Elements elements = document.getElementsByAttributeValue("data-testid", "history-table");
            // 방법2. 배당금 데이터를 가지는 테이블의 class 속성 이용해 테이블 정보 가져오기
            //       (html 검색했을 때 동일한 css class를 사용하는 곳은 없어서 일단 사용.)
            Elements elements = document.getElementsByAttributeValue("class", "table yf-j5d1ld noDl");
            // tbody에도 많은 하위 테그들이 존재한다.
            // 하지만 tbody의 하위 태그들은 배당금 데이터이기에 스크래핑할 때 마다 개수가 달라진다.
            // 이런 경우에는 위와 같이 index로 데이터를 가져올 수 없다.
            // tbody의 경우는 어떤 회사에서 데이터를 가져오더라도
            // table의 하위 2번째에는 tbody 태그가 위치함을 알기에 index로 접근해 데이터를 가져올 수 있다.
            Element table = elements.get(0); // <table> 태그 가져오기
            Element tbody = table.children().get(1); // <tbody> 태그 가져오기

            // 3. 필요한 데이터 추출
            // 배당금 정보만 가져오기 = text의 내용이 Dividend로 끝나는 데이터 가져오기
            for (Element child : tbody.children()) { // 모든 데이터 순회
                String txt = child.text();
                // endsWith("문자열") : 대상 문자열이 특정 문자 또는 문자열로 끝나는지 체크
                if(!txt.endsWith("Dividend")) {
                    continue;
                }
                // txt = "Oct 25, 2024 2.50 Dividend"
                String[] splits = txt.split(" "); // 공백으로 구분
                String month = splits[0];
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                System.out.println(year + "/" + month + "/" + day + "->" + dividend);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
