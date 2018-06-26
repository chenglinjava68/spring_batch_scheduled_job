package com.yunforge.cmsarticle.config;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
	@Resource(name = "exportUserJob")
	private Job job;

	/**
	 * 
	 * 作者:覃飞剑 日期:2018年6月26日 返回:void 说明:每周日0点0分0秒触发
	 */
	@Scheduled(cron = "0 0 0 ? * 7")
	public void run() {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Calendar rightNow = Calendar.getInstance();
			rightNow.add(Calendar.DAY_OF_YEAR, -7);// 日期加-7天
			Date dt1 = rightNow.getTime();
			String dateParam = sdf.format(dt1);

			JobParameters param = new JobParametersBuilder().addString("created_time", dateParam).toJobParameters();
			System.out.println(dateParam);
			JobExecution execution = jobLauncher.run(job, param); // 执行job
			System.out.println("Exit Status : " + execution.getStatus());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
