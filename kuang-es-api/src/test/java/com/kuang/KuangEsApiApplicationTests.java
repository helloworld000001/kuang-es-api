package com.kuang;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * 高级客户端测试API
 */
@SpringBootTest
class KuangEsApiApplicationTests{
	@Autowired
	RestHighLevelClient restHighLevelClient;

	// 测试索引的创建
	@Test
	void testCreateIndex() throws IOException {
		// 1. 创建索引请求:索引名为kuang_index
		CreateIndexRequest request = new CreateIndexRequest("kuang_index");

		// 2. 客户端执行请求,请求后获得响应
		CreateIndexResponse createIndexResponse =
				restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

		System.out.println(createIndexResponse);
	}

	// 测试获取索引:只能判断索引是否存在
	@Test
	void testExistIndex() throws IOException {
		// 1. 获得 kuang_index 索引请求
		GetIndexRequest request = new GetIndexRequest("kuang_index2");

		// 2. 查看 kuang_index 索引是否存在
		boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);

		System.out.println(exists);
	}

	// 测试删除索引
	@Test
	void restDeleteIndex() throws IOException {
		// 1. 获取删除索引请求
		DeleteIndexRequest request = new DeleteIndexRequest("testdb");

		// 2. 发起删除索引请求
		AcknowledgedResponse delete = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);

		// 3. 查看是否删除成功
		System.out.println(delete.isAcknowledged());
	}


	@Test
	void contextLoads() {
	}

}
