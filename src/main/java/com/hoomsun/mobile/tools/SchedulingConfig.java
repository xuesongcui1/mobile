package com.hoomsun.mobile.tools;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


/**
 * 定时任务启动
 *@Title:  
 *@Description:  
 *@Author:Administrator  
 *@Since:2018年3月29日  
 *@Version:1.1.0
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
	private static Logger logger = LoggerFactory.getLogger(SchedulingConfig.class);

	@Autowired 
	HttpServletRequest request; 
	
    /**
     * 移动
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨一点执行
    public void mobile() {
		 logger.warn("定时任务开始");
    	try {
    		Runtime.getRuntime().exec("taskkill /F /IM chromedriver.exe");
    		Runtime.getRuntime().exec("taskkill /F /IM chrome.exe");
    	} catch (Exception e) {
    		logger.error("-----定时任务失败----",e);
    	}
		 logger.warn("定时任务结束");
    }
    
}