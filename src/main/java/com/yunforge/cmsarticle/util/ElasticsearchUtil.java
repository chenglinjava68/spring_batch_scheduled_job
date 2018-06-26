package com.yunforge.cmsarticle.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.yunforge.cmsarticle.model.IndexClass;





@Service("ElasticsearchUtil")
public class ElasticsearchUtil {

//	public  Map<String, JsonValueProcessor> processors = new HashMap<String, JsonValueProcessor>();
//	public  String SHORTFORMAT = "yyyy-MM-dd";
//	public  String LONGFORMAT = "yyyy-MM-dd hh:mm:ss";
	
	@Resource(name = "esClient")  
	private  TransportClient client;
	
	

    /**
	 * 创建索引
	 * 
	 * @param indexName
	 */
	public  void createIndex(String indexName) {
		try {

			// ����������

			if (isIndexExists(indexName)) {
				System.out.println("Index  " + indexName + " already exits!");
			} else {
				CreateIndexRequest cIndexRequest = new CreateIndexRequest(
						indexName);
				CreateIndexResponse cIndexResponse = client.admin().indices()
						.create(cIndexRequest).actionGet();
				if (cIndexResponse.isAcknowledged()) {
					System.out.println("create index successfully");
				} else {
					System.out.println("Fail to create index!");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 查看索引是否存在
	 * 
	 * @param indexName
	 * @param client
	 * @return
	 */
	public  boolean isIndexExists(String indexName) {
		boolean flag = false;
		IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(
				indexName);

		IndicesExistsResponse inExistsResponse = client.admin().indices()
				.exists(inExistsRequest).actionGet();

		if (inExistsResponse.isExists()) {
			flag = true;
		} else {
			flag = false;
		}

		return flag;
	}

	/**
	 * 查看索引的类型是否存在
	 * 
	 * @param indexName
	 * @param type
	 * @param client
	 * @return
	 */
	public  boolean isIndexTypeExists(String indexName, String type
			) {
		boolean flag = false;
		flag = isIndexExists(indexName);
		if (!flag) {
			System.out.println("index " + indexName + " not exists ");
			return flag;
		} else {
			TypesExistsResponse reponse = client
					.admin()
					.indices()
					.typesExists(
							new TypesExistsRequest(new String[] { indexName },
									type)).actionGet();
			return reponse.isExists();
		}
	}

	
	
	/**
	 * 删除索引
	 * 
	 * @param indexName
	 * @param client
	 */
	public  void deleteIndex(String indexName) {

		try {
			if (!isIndexExists(indexName)) {
				System.out.println(indexName + " not exists");
			} else {
				DeleteIndexResponse dResponse = client.admin().indices()
						.prepareDelete(indexName).execute().actionGet();
				if (dResponse.isAcknowledged()) {
					System.out.println("delete index " + indexName
							+ "  successfully!");
				} else {
					System.out.println("Fail to delete index " + indexName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据id删除一个文档
	 * 
	 * @param index
	 *            索引
	 * @param type
	 *            类型
	 * @param id
	 * @param client
	 */
	public  void deleteDocment(String index, String type, String id) {

		try {
			if (!isIndexExists(index)) {
				System.out.println(index + " not exists");
			} else {
				DeleteIndexResponse dResponse = client.admin().indices()
						.prepareDelete(index, type, id).execute().actionGet();
				if (dResponse.isAcknowledged()) {
					System.out.println("delete index " + index
							+ "  successfully!");
				} else {
					System.out.println("Fail to delete index " + index);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 插入一个list集合数据
	 * 
	 * @param index
	 * @param type
	 * @param list
	 * @param client
	 */
	public  void insertDataByList(String index, String type, List list) {
		//
		if (isIndexExists(index)) {
			createIndex(index);
		}
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.get(i);
			
				// 构造json
				XContentBuilder builder;
				try {
					builder = XContentFactory.jsonBuilder()
							.startObject();
				String id = null;
				// 通过反射获取obj属性信息
				for (Field f : obj.getClass().getDeclaredFields()) {
					f.setAccessible(true);
					if (f.getName().equals("id")) {
						id = f.get(obj).toString();
					}
					builder.field(f.getName(), f.get(obj));

				}
				builder.endObject();
				if (id != null) {
					client.prepareIndex(index, type, id)
							.setSource(builder.string()).get();
				} else {
					client.prepareIndex(index, type)
							.setSource(builder.string()).get();
				}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			

		}
	}

	/**
	 * 根据index，type，id获取文档
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param client
	 * @return
	 */
	public  String getDocumentByIndexAndTypeAndId(String index,
			String type, String id ) {
		if (!checkExists(index, type)) {
			return null;
		} else {
			GetResponse gReponse = client.prepareGet(index, type, id).execute()
					.actionGet();
			return gReponse.getSourceAsString();
		}
	}

	/**
	 * 更新文档
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param jsonStr
	 * @param client
	 * @return
	 */
	public  String updateDocument(String index, String type, String id,
			String jsonStr ) {
		if (checkExists(index, type)) {
			client.prepareUpdate(index, type, id).setDoc(jsonStr).get();
			return "update success!";
		} else {
			return "can not update !";
		}
	}

	/**
	 * 检查index，type是否存在
	 * 
	 * @param index
	 * @param type
	 * @param client
	 * @return
	 */
	public  boolean checkExists(String index, String type
			) {
		if (!isIndexExists(index)) {
			System.out.println(index + " is not exists !");
			return false;
		} else if (!isIndexTypeExists(index, type)) {
			System.out.println("index of type :" + type + " is not exists !");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 短语查询 短语不分词
	 * 
	 * @param index
	 * @param type
	 * @param terms
	 *            条件 要查询的列名 + 短语
	 * @param form
	 *            结果从第几个开始显示
	 * @param size
	 *            显示多少个结果
	 * @param client
	 * @return
	 */
	public  List<Object> matchPhraseQuery(String index, String type,
			String[] terms, Integer form, Integer size ) {
		List<Object> result = new ArrayList<Object>();
		if (checkExists(index, type)) {
			SearchRequestBuilder setQuery = client
					.prepareSearch(index)
					.setTypes(type)
					.setQuery(
							QueryBuilders.matchPhraseQuery(terms[0], terms[1]));
			if (form != null) {
				setQuery.setFrom(form);
			}
			if (size != null) {
				setQuery.setSize(size);
			}
			// 设置是否按查询匹配度排序
			setQuery.setExplain(true);
			SearchResponse reponse = setQuery.execute().actionGet();
			SearchHits hits = reponse.getHits();
			if (null == hits || hits.totalHits() == 0) {
				System.out.println("查询无结果");
			} else {
				for (int i = 0; i < hits.getHits().length; i++) {
					result.add(hits.getHits()[i].getSourceAsString());
				}
			}
			return result;
		} else {
			System.out.println("index或者type不存在");
			return result;
		}
	}

	/**
	 * 短语查询 短语可分词
	 * 
	 * @param index
	 * @param type
	 * @param terms
	 *            条件 要查询的列名 + 短语
	 * @param form
	 *            结果从第几个开始显示
	 * @param size
	 *            显示多少个结果
	 * @param client
	 * @return
	 */
	public  List<Object> matchQuery(String index, String type,
			String[] terms, Integer form, Integer size) {
		List<Object> result = new ArrayList<Object>();
		if (checkExists(index, type)) {
			SearchRequestBuilder setQuery = client.prepareSearch(index)
					.setTypes(type)
					.setQuery(QueryBuilders.matchQuery(terms[0], terms[1]));
			if (form != null) {
				setQuery.setFrom(form);
			}
			if (size != null) {
				setQuery.setSize(size);
			}
			// 设置是否按查询匹配度排序
			setQuery.setExplain(true);
			SearchResponse reponse = setQuery.execute().actionGet();
			SearchHits hits = reponse.getHits();
			if (null == hits || hits.totalHits() == 0) {
				System.out.println("查询无结果");
			} else {
				for (int i = 0; i < hits.getHits().length; i++) {
					result.add(hits.getHits()[i].getSourceAsString());
				}
			}
			return result;
		} else {
			System.out.println("index或者type不存在");
			return result;
		}
	}

	/**
	 * 建立type映射
	 * 
	 * @param client
	 * @param index
	 * @param type
	 * @param map
	 *            key为列名，list存放 属性或者列方法和对应的值 如 {"type","text"}
	 * @return
	 */
	public  boolean putIndexMapping( String index,
			String type, Map<String, List<String[]>> map) {
		// mapping
		XContentBuilder mappingBuilder;
		try {
			mappingBuilder = XContentFactory.jsonBuilder();
			mappingBuilder.startObject();
			mappingBuilder.startObject(type);
			mappingBuilder.startObject("properties");
			for (Entry<String, List<String[]>> entry : map.entrySet()) {
				List<String[]> list = entry.getValue();
				mappingBuilder.startObject(entry.getKey());
				for (String[] strs : list) {
					mappingBuilder.field(strs[0], strs[1]);
				}
				mappingBuilder.endObject();
			}
			mappingBuilder.endObject();
			mappingBuilder.endObject();
			mappingBuilder.endObject();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		IndicesAdminClient indicesAdminClient = client.admin().indices();
		PutMappingRequestBuilder putMappingRequestBuilder = indicesAdminClient
				.preparePutMapping(index);
		putMappingRequestBuilder.setType(type);
		putMappingRequestBuilder.setSource(mappingBuilder);
		PutMappingResponse response = putMappingRequestBuilder.get();
		return response.isAcknowledged();
	}

	/**
	 * 批量导入数据
	 * 
	 * @param index
	 * @param type
	 * @param fileURL
	 * @param client
	 * @return
	 */
	public  String bulkImportData(String index, String type,
			String fileURL ) {
		try {
			File article = new File(fileURL);
			InputStreamReader isr = new InputStreamReader(new FileInputStream(article), "UTF-8");
			BufferedReader bfr = new BufferedReader(isr);
			String line = null;
			BulkRequestBuilder bulkRequest = client.prepareBulk();
			int count=0;
            while((line=bfr.readLine())!=null){
            	Gson gson = new Gson();
            	Map fromJson = gson.fromJson(line, Map.class);
            	String id = fromJson.get("id").toString();
            	if (StringUtil.isEmpty(id) ) {
            		bulkRequest.add(client.prepareIndex(index, type).setSource(line));
            	} else {
            		bulkRequest.add(client.prepareIndex(index, type, id).setSource(line));
            	}
                if (count%10000==0) {
                    bulkRequest.execute().actionGet();
                }
                count++;
            }
			System.out.println("提交");
			bulkRequest.execute().actionGet();

			bfr.close();
			isr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "import Data fail!";
		}
		return "success";
	}

	/**
	 * 
	 * 作者:覃飞剑
	 * 日期:2017年7月12日
	 * @param index
	 * @param type
	 * @param List<Map> list
	 * @return
	 * 返回:String
	 * 说明:批量导入数据
	 */
	public  String bulkImportData(String index, String type,
			List<Map> list) {
//		BulkRequestBuilder bulkRequest = client.prepareBulk();
//		processors.put("java.sql.Date", new SQLDateProcessor(SHORTFORMAT));
//		processors.put("java.util.Date", new UtilDateProcessor(SHORTFORMAT));
//		processors.put("java.sql.Timestamp",
//				new TimestampProcessor(SHORTFORMAT));
//		try {
//			for (Map map : list) {
//				String id = null;
//				if (null != map.get("id")) {
//					id = map.get("id").toString();
//					// 如果id为空
//					if (id == null || id.equals("")) {
//						bulkRequest.add(client.prepareIndex(index, type)
//								.setSource(JSONUtil.toJson(map, processors)));
//					} else {
//						bulkRequest.add(client.prepareIndex(index, type, id)
//								.setSource(JSONUtil.toJson(map, processors)));
//					}
//				} else {// map中不存在id属性
//					bulkRequest.add(client.prepareIndex(index, type).setSource(
//							JSONUtil.toJson(map, processors)));
//				}
//			}
//			bulkRequest.execute().actionGet();
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return "error";
//		}
		return "success";
	}

	
	/**
	 * 
	 * 作者:覃飞剑
	 * 日期:2017年7月12日
	 * @param queryStr
	 * @param from
	 * @param size
	 * @param type
	 * @return
	 * 返回:List<IndexClass>
	 * 说明:条件query_string模糊查询，可以有type需求
	 */
	public  List<IndexClass> query_string(//String index, String type,
			String queryStr,Integer from, Integer size, String type) {
		List<IndexClass> result = new ArrayList<IndexClass>();
		
		BoolQueryBuilder bq =  QueryBuilders.boolQuery();
		
		QueryStringQueryBuilder qb = QueryBuilders.queryStringQuery(queryStr);
//		qb.analyzer("ik_max_word");
		qb.field("content.pinyin").field("title.pinyin");
		/**
		 * must 添加第一个条件
		 */
		bq.must(qb);
		
		/**
		 * 有type要求的时候
		 */
		if (!StringUtil.isEmpty(type)){
		    MatchPhraseQueryBuilder mpqb = QueryBuilders.matchPhraseQuery("type", type);
		    bq.must(mpqb);
		}
		
		SearchRequestBuilder searchRequestBuilder = client.prepareSearch()
				.setIndices("importdatademo").setTypes("sum")
				.setQuery(bq);
		// 设置是否按查询匹配度排序
		searchRequestBuilder.setExplain(true);
		// 设置查询类型 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询 2.SearchType.SCAN
        // = 扫描查询,无序
//        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		

        // 分页应用
        if(null == from || from.equals("")){
        	from = 0;
        }
        if(null == size || size.equals("")){
        	size = 10;
        }
        searchRequestBuilder.setFrom(from).setSize(size);
        
		// 设置高亮显示
		HighlightBuilder hiBuilder=new HighlightBuilder();
        hiBuilder.preTags("<span style=\"color:red\">");
        hiBuilder.postTags("</span>");
        hiBuilder.field("content.pinyin");
        hiBuilder.field("title.pinyin");
        searchRequestBuilder.highlighter(hiBuilder);

        MultiSearchRequestBuilder sr = client.prepareMultiSearch().add(searchRequestBuilder);
        
        
//		SearchResponse response = searchRequestBuilder.execute().actionGet();
        for(MultiSearchResponse.Item item : sr.get()){
            SearchResponse response = item.getResponse();
            SearchHits hits = response.getHits();
            if (null == hits || hits.totalHits() == 0) {
                System.out.println("查询无结果");
            } else {
                for (int i = 0; i < hits.getHits().length; i++) {
                    SearchHit hit = hits.getAt(i);
                    
                    String json = hit.getSourceAsString();
                    Gson gson = new Gson();
                    gson.fromJson(json, IndexClass.class);
                    IndexClass bean = gson.fromJson(json, IndexClass.class);
                    
                    Map<String, HighlightField> highlightFields = hit
                            .getHighlightFields();
                    // title高亮
                    HighlightField titleField = highlightFields.get("title.pinyin");
                    if (titleField != null) {
                        Text[] fragments = titleField.fragments();
                        String title = "";
                        for (Text text : fragments) {
                            title += text;
                        }
                        bean.setTitle(title);
                    }
                    
                    // content高亮
                    HighlightField describeField = highlightFields.get("content.pinyin");
                    if (describeField != null) {
                        Text[] fragments = describeField.fragments();
                        String content = "";
                        for (Text text : fragments) {
                            content += text;
                        }
                        bean.setContent(content);
                    }
                    result.add(bean);
                    
                    
                }
            }
        }
		return result;
	}
	
	
	/**
	 * 多字段查询 mult_match
	 * 
	 * @param index
	 *            索引
	 * @param type
	 *            类型
	 * @param queryStr
	 *            要查询的字符串
	 * @param client
	 */
	public  List<IndexClass> multiSearch(String index, String type,
			String queryStr,Integer from, Integer size) {
		List<IndexClass> result = new ArrayList<IndexClass>();
		QueryStringQueryBuilder qb = QueryBuilders.queryStringQuery(queryStr);
		qb.analyzer("ik_max_word");
		qb.field("content").field("title");
		SearchRequestBuilder srb1 = client.prepareSearch()
		        .setIndices(index).setTypes(type).setQuery(qb);
		// 设置是否按查询匹配度排序
		srb1.setExplain(true);
		
		MatchQueryBuilder mq1 = QueryBuilders.matchQuery("title", queryStr).analyzer("ik_max_word");
		SearchRequestBuilder srb2 = client.prepareSearch()
		        .setIndices(index).setTypes(type).setQuery(mq1);
		// 设置是否按查询匹配度排序
		srb2.setExplain(true);
		
		MatchQueryBuilder mq2 = QueryBuilders.matchQuery("content", queryStr).analyzer("ik_max_word");
		SearchRequestBuilder srb3 = client.prepareSearch()
		        .setIndices(index).setTypes(type).setQuery(mq2);
		// 设置是否按查询匹配度排序
		srb3.setExplain(true);
		
		
		
		// 设置查询类型 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询 2.SearchType.SCAN
        // = 扫描查询,无序
//        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        // 分页应用
        if(null == from || from.equals("")){
        	from = 0;
        }
        if(null == size || size.equals("")){
        	size = 10;
        }
        srb1.setFrom(from).setSize(size);
        srb2.setFrom(from).setSize(size);
        srb3.setFrom(from).setSize(size);
        
		// 设置高亮显示
		HighlightBuilder hiBuilder=new HighlightBuilder();
        hiBuilder.preTags("<span style=\"color:red\">");
        hiBuilder.postTags("</span>");
        hiBuilder.field("content");
        hiBuilder.field("title");
        srb1.highlighter(hiBuilder);
        srb2.highlighter(hiBuilder);
        srb3.highlighter(hiBuilder);

        MultiSearchRequestBuilder sr = client.prepareMultiSearch().add(srb1).add(srb2).add(srb3);
        MultiSearchResponse responses = sr.execute().actionGet();
        for(MultiSearchResponse.Item item : sr.get()){
        	SearchResponse response = item.getResponse();
//		SearchResponse response = searchRequestBuilder.execute().actionGet();
        	SearchHits hits = response.getHits();
        	if (null == hits || hits.totalHits() == 0) {
        		System.out.println("查询无结果");
        	} else {
        		for (int i = 0; i < hits.getHits().length; i++) {
        			SearchHit hit = hits.getAt(i);
        			
        			String json = hit.getSourceAsString();
        			Gson gson = new Gson();
        			IndexClass bean = gson.fromJson(json, IndexClass.class);
        			
        			Map<String, HighlightField> highlightFields = hit
        					.getHighlightFields();
        			// title高亮
        			HighlightField titleField = highlightFields.get("title");
        			if (titleField != null) {
        				Text[] fragments = titleField.fragments();
        				String title = "";
        				for (Text text : fragments) {
        					title += text;
        				}
        				bean.setTitle(title);
        			}
        			
        			// content高亮
        			HighlightField describeField = highlightFields.get("content");
        			if (describeField != null) {
        				Text[] fragments = describeField.fragments();
        				String content = "";
        				for (Text text : fragments) {
        					content += text;
        				}
        				bean.setContent(content);
        			}
        			result.add(bean);
        		}
        	}
        }
		return result;
	}
	
	
	
}
