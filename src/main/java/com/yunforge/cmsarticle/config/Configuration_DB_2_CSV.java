package com.yunforge.cmsarticle.config;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.transaction.PlatformTransactionManager;

import com.yunforge.cmsarticle.model.Article;
import com.yunforge.cmsarticle.util.StringUtil;

@Configuration
@EnableBatchProcessing
public class Configuration_DB_2_CSV {
	@Autowired
	DataSource dataSource;
	
	@Bean
	@StepScope
	public ItemReader<Article> reader(@Value("#{jobParameters['created_time']}") String date) {
		JdbcPagingItemReader<Article> reader = new JdbcPagingItemReader<Article>();
		reader.setDataSource(dataSource);
		reader.setQueryProvider(queryProvider());
		reader.setPageSize(100000);
		System.out.println("**************************");
		System.out.println(StringUtil.getTimeByDateAndFormat(Date.from(Instant.now()), "yyyyMMdd-hhmmss"));
		Map<String, Object> params = new HashMap<String, Object>();
	    params.put("created_time", date);
	    reader.setParameterValues(params);
		reader.setRowMapper((RowMapper<Article>) (rs, rowNum) -> {
			final Article article = new Article();
			LobHandler lobHandler = new DefaultLobHandler();
			article.setId(rs.getLong("id"));
			article.setTitle(rs.getString("title"));
			article.setContent(lobHandler.getClobAsString(rs, "content"));
			article.setCreatedTime(rs.getDate("created_time"));
			article.setCatId(rs.getLong("cat_id"));
			return article;
		});
		return reader;
	}

	public MySqlPagingQueryProvider queryProvider() {
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("SELECT id, title,content,created_time,cat_id");
		queryProvider.setFromClause("FROM article");
		queryProvider.setWhereClause("where (created_time >= :created_time)");
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("id", Order.ASCENDING);
		queryProvider.setSortKeys(sortKeys);
		return queryProvider;
	}

	@Bean
	public ItemProcessor<Article, String> processor() {
		return new CMSItemProcessor_DB_2_JSON();
	}

	@Bean
	public ItemWriter<String> writer() throws IOException {
		FlatFileItemWriter<String> writer = new FlatFileItemWriter<String>();
		PathResource pathResource = new PathResource("cms_article.json");
		writer.setResource(pathResource);
		DelimitedLineAggregator<String> delimitedLineAggregator = new DelimitedLineAggregator<String>();
		writer.setLineAggregator(delimitedLineAggregator);
		return writer;
	}
//	@Bean("publicMap")
//	public Map<String, PathResource> map() {
//		Map<String, PathResource> map = new HashMap<>();
//		PathResource pathResource = new PathResource(StringUtil.getTimeByDateAndFormat(Date.from(Instant.now()), "yyyyMMdd-hhmmss") + "_cms_article.json");
//		map.put("pathResource", pathResource);
//		return map;
//	}
	
	@Bean
	public Job exportUserJob(JobBuilderFactory jobs, Step s1) {
		return jobs.get("exportUserJob")
				.incrementer(new RunIdIncrementer())
				.flow(s1)
				.end()
				.listener(cmsJobListener())
				.build();
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory, @Qualifier("reader") ItemReader<Article> reader,
			@Qualifier("writer") ItemWriter<String> writer,
			@Qualifier("processor") ItemProcessor<Article, String> processor,
			PlatformTransactionManager transactionManager) {

		return stepBuilderFactory.get("step1")
				.<Article, String>chunk(100000)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.faultTolerant()
				.retry(Exception.class) // 重试
				.noRetry(ParseException.class)
				.retryLimit(1) // 每条记录重试一次
				.listener(new RetryFailuireItemListener())
				.skip(Exception.class).skipLimit(1) // 一共允许跳过1次异常
				// .taskExecutor(new SimpleAsyncTaskExecutor()) //设置并发方式执行
				// .throttleLimit(100000) //并发任务数为 100000,默认为4
				.transactionManager(transactionManager)
				.build();
	}

	@Bean
	public CMSJobListener cmsJobListener() {
		return new CMSJobListener();
	}

	@Bean
	public JobParametersBuilder jobParametersBuilder() {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
//		jobParametersBuilder.addString("created_time", "2017-11-06");
		return jobParametersBuilder;
	}

}