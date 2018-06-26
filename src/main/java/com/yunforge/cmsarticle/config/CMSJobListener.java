package com.yunforge.cmsarticle.config;



import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.PathResource;

import com.yunforge.cmsarticle.util.ElasticsearchUtil;
import com.yunforge.cmsarticle.util.StringUtil;


public class CMSJobListener implements JobExecutionListener{

	long startTime;
	long endTime;
	
//	@Resource(name="publicMap")
//	private Map<String, PathResource> publicMap;
	/**
     * 注入工具类
     */
    @Autowired
    private ElasticsearchUtil elasticsearchUtil;
    
	
	@Value("${es.index}")
	private String index;
	@Value("${es.type}")
	private String type;
	
	
	@Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		startTime = System.currentTimeMillis();
		System.out.println("任务处理开始");
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		endTime = System.currentTimeMillis();
		System.out.println("任务处理结束");
		System.out.println("耗时:" + (endTime - startTime)/1000 + "s");
		
		try {
			String absolutePath = new PathResource("cms_article.json").getFile().getAbsolutePath();
			System.out.println(absolutePath);
			elasticsearchUtil.bulkImportData(index, type, absolutePath);
			StringUtil.deleteFiles(absolutePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
