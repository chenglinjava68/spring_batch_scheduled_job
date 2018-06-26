package com.yunforge.cmsarticle.controller;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	
	@Autowired
	JobLauncher jobLauncher;
	 
	@Autowired
	Job importJob;
	public JobParameters   jobParameters;
	
	@GetMapping("/importArticle/{date}")
	public String importArticle(@PathVariable String date) {
        JobExecution execution;
		try {
			JobParameters param =    new JobParametersBuilder().addString("created_time", date)
					.addLong("time", System.currentTimeMillis()).toJobParameters();  
			execution = jobLauncher.run(importJob, param);
			System.out.println("Exit Status : " + execution.getStatus());   
			return "success";
		} catch (JobExecutionAlreadyRunningException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobRestartException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}             //执行job      
		return "fail";
	}

}
