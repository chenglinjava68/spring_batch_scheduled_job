package com.yunforge.cmsarticle.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.yunforge.cmsarticle.model.Article;
import com.yunforge.cmsarticle.model.IndexClass;
import com.yunforge.cmsarticle.util.StringUtil;


public class CMSItemProcessor_DB_2_JSON implements ItemProcessor<Article , String> {

	@Value("${es.index}")
	private String index;
	@Value("${es.type}")
	private String type;
	
	@Override
    public String process(Article  article) throws Exception {
		IndexClass indexClass = new IndexClass();
		Long id = article.getId();
		String title = article.getTitle();
		String content = article.getContent();
		Long catId = article.getCatId();
		String createDate = StringUtil.getTimeByDateAndFormat(article.getCreatedTime(), "yyyy年MM月dd日");
		String pcurl = "/portal/docs/article/" + id;
		String url = "/cms/api/article/view/" + id;
		String type = "";
		String typeName = "知识文档";

		List<Long> ids = new ArrayList<Long>();
		ids.add(7l);
		ids.add(8l);
		ids.add(9l);
		ids.add(10l);
		ids.add(11l);
		ids.add(12l);
		ids.add(13l);
		ids.add(14l);
		ids.add(15l);
		System.out.println("catId : ");
		System.out.println(catId);
		if (null == catId) {
			return null;
		}
		if (ids.contains(catId)) {
			type = "4";
		} else if (17l == catId) {
			type = "3";
		} else if (19l == catId) {
			type = "2";
		} else {
			System.out.println("type 对应不正确");
			System.out.println(article.toString());
			return null;
		}
		
		indexClass.setId(id+"");
		indexClass.setTitle(title);
		indexClass.setContent(content);
		indexClass.setCreateDate(createDate);
		indexClass.setPcurl(pcurl);
		indexClass.setUrl(url);
		indexClass.setType(type);
		indexClass.setTypeName(typeName);
		
		Gson gson = new Gson();
		return gson.toJson(indexClass);
    }
	
}
