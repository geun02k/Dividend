package com.example.dividend.schduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestScheduler {

//    @Scheduled(cron="0/5 * * * * *")
//    public void test() {
//        System.out.println("now ->" + System.currentTimeMillis());
//    }

//    @Scheduled(fixedDelay = 1000)
//    public void test1() throws InterruptedException {
//        Thread.sleep(10000);
//        System.out.println(Thread.currentThread().getName() + " -> 테스트1 : " + LocalDateTime.now());
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void test2() {
//        System.out.println(Thread.currentThread().getName() + " -> 테스트2 : " + LocalDateTime.now());
//    }
}
