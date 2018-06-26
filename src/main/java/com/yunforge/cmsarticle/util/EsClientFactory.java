package com.yunforge.cmsarticle.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EsClientFactory {
	private static  Settings setting = null;
	// 饿汉式单利 天生线程安全 饿汉式在类创建的同时就已经创建好一个静态的对象供系统使用，以后不再改变，所以天生是线程安全的。
	private static final Map<String, TransportClient> CLIENTMAP = new ConcurrentHashMap<String, TransportClient>();

	private static Map<String, Integer> ips = new HashMap<String, Integer>(); // hostname
																				// port
	
	
	@Value("${es.ip}")
	private String conf_ips;
	@Value("${es.clusterName}")
	private String clusterName;
	@Value("${es.port}")
	private Integer port;
	
	
	
	
//	/**
//	 * 初始化默认的client
//	 */
//	public static void init() {
//		ips.put("127.0.0.1", 9300);
//		setting = Settings.builder()
//				.put("cluster.name", "elasticsearch").build();
//		addClient(setting, getAllAddress(ips));
//	}

	/**
	 * 获得所有的地址端口
	 *
	 * @return
	 */
	private static List<InetSocketTransportAddress> getAllAddress(
			Map<String, Integer> ips) {
		List<InetSocketTransportAddress> addressList = new ArrayList<InetSocketTransportAddress>();
		for (String ip : ips.keySet()) {
			try {
				addressList.add(new InetSocketTransportAddress(InetAddress
						.getByName(ip), ips.get(ip)));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return addressList;
	}
	
	@Bean(name="esClient")
	public TransportClient getClient() {
		return getClient(clusterName);
	}
	public TransportClient getClient(String name) {
		for(String ip:conf_ips.split(",")) {
			ips.put(ip, port);
		}
		setting = Settings.builder()
				.put("cluster.name", clusterName).build();
		addClient(setting, getAllAddress(ips));
		return CLIENTMAP.get(name);
	}

	/**
	 * 把client加入静态Map中
	 * 
	 * @param setting
	 * @param transportAddress
	 */
	private static void addClient(Settings setting,
			List<InetSocketTransportAddress> transportAddress) {
		TransportClient client = new PreBuiltTransportClient(setting)
				.addTransportAddresses(transportAddress
						.toArray(new InetSocketTransportAddress[transportAddress
								.size()]));
		CLIENTMAP.put(setting.get("cluster.name"), client);
	}
}
