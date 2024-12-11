package com.example.dividend.scraper;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker); // input
    ScrapedResult scrap(Company company); // output
}
