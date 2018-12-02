package com.allcure.spider;
//
import com.allcure.spider.service.SpiderService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class AllcureSpiderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AllcureSpiderApplication.class, args);
    }


    @Scheduled(cron = "0/10 * * * * ?")
    public void doExecute() {
        System.out.println("==定时任务===");
        SpiderService spiderService = new SpiderService();
        spiderService.spide();
    }
}
