package com.yunforge.cmsarticle.config;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableBatchProcessing
@EnableScheduling
@Service
public class ScheduledJob {
	
	@Autowired  
    private JobLauncher jobLauncher;  
	@Resource(name="exportUserJob")
	private Job job; 
	/**
	 * 
	 * 作者:覃飞剑
	 * 日期:2018年6月26日
	 * 返回:void
	 * 说明:每天抓一次
	 */
	@Scheduled(cron = "0 0 0 /1 * ? *")
	public void run() {       
        try {          
             String dateParam = new Date().toString();       
             JobParameters param =    new JobParametersBuilder().addString("created_time", dateParam).toJobParameters();  
             System.out.println(dateParam);  
             JobExecution execution = jobLauncher.run(job, param);             //执行job      
             System.out.println("Exit Status : " + execution.getStatus());    
         } catch (Exception e) {   
                 e.printStackTrace();   
         } 
    }


}
