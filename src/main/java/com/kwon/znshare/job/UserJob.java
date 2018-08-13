package com.kwon.znshare.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Configurable
@EnableScheduling
@EnableAsync
public class UserJob {

    @Autowired

    //每天凌晨1点执行一次
    @Scheduled(cron = "0 0 1 * * ?")
    public void loginJob() {

    }



}