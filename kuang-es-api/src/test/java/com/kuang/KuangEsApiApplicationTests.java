package com.kuang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.kuang.pojo.User;
import com.kuang.utils.ESconst;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonbTester;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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

	// 测试添加文档
	@Test
	void testAddDocument() throws IOException {
		// 创建对象
		User user = new User("狂神说", 3);

		// 创建请求
		IndexRequest request = new IndexRequest("kuang_index");

		// 规则 PUT /kuang_index/_doc/1
		// 设置过期时间为1s
		request.id("1");
		request.timeout(TimeValue.timeValueSeconds(1));
		request.timeout("1s");

		// 将我们的数据放入请求
		request.source(JSON.toJSONString(user), XContentType.JSON);

		// 客户端发送请求,获取响应的结果
		IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
		System.out.println(indexResponse.toString());
		System.out.println(indexResponse.status()); // CREATED
	}

	// 获取文档，判断是否存在。规则：GET /kuang_index/_doc/1
	@Test
	void testIsExists() throws IOException {
		GetRequest request = new GetRequest("kuang_index" ,"1");
		// 不过滤字段_source
		request.fetchSourceContext(new FetchSourceContext(false));
		// 排序的字段
		request.storedFields("_none_");

		boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);

		System.out.println(exists);
	}

	// 获取文档信息
	@Test
	void testGetDocument() throws IOException {
		// 获取文档请求
		GetRequest request = new GetRequest("kuang_index" ,"1");

		// 获取请求，生成响应
		GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);

		// 打印文档的内容: {"age":3,"name":"狂神说"}
		System.out.println(response.getSourceAsString());
		// 返回的全部内容和命令是一样的
		/* {"_index":"kuang_index","_type":"_doc","_id":"1","_version":1,"_seq_no":0,
		"_primary_term":1,"found":true,"_source":{"age":3,"name":"狂神说"}}*/
		System.out.println(response);
	}

	// 更新文档记录
	@Test
	void testUpdateDocument() throws IOException {
		// 获取文档请求:update请求
		UpdateRequest request = new UpdateRequest("kuang_index" ,"1");
		request.timeout("1s");

		User user = new User("狂神说Java", 18);

		request.doc(JSON.toJSONString(user), XContentType.JSON);

		// 获取请求，生成响应
		UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);

		System.out.println(response.status());
	}

	// 删除文档记录
	@Test
	void testDeleteRequest() throws IOException {
		// 获取request请求
		DeleteRequest request = new DeleteRequest("kuang_index", "1");
		request.timeout("1s");

		DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
		System.out.println(response);
	}

	// 批量插入数据
	@Test
	void testBulkRequest() throws IOException {
		// 创建请求
		BulkRequest request = new BulkRequest();
		request.timeout("10s");

		ArrayList<User> userList = new ArrayList<>();
		userList.add(new User("kuangshen1", 1));
		userList.add(new User("kuangshen2", 2));
		userList.add(new User("kuangshen3", 3));
		userList.add(new User("kuangshen4", 4));
		userList.add(new User("kuangshen5", 5));

		// 如果不设置id，默认生成随机id
		for (int i = 0; i < userList.size(); i++) {
			request.add(
					new IndexRequest("kuang_index")
					.id("" + (i+1))
					.source(JSON.toJSONString(userList.get(i)),XContentType.JSON)
			);
		}

		BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
		// 是否失败
		System.out.println(response.hasFailures());
	}

	// 查询
	// SearchRequest 搜索请求
	/* SearchSourceBuidler 条件构造：
		HighlightBuilder	高亮,
		MatchAllQueryBuilder 匹配所有,
		TermQueryBuilder 	精确查询
		分页等*/
	@Test
	void testSearch() throws IOException {
		// 创建查询请求
		SearchRequest request = new SearchRequest(ESconst.ES_INDEX);

		// 构建搜索的条件
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		// 高亮搜索
		sourceBuilder.highlighter(new HighlightBuilder());

		// 精确查询 QueryBuilders.termQuery
		TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "kuangshen1");
		// 匹配所有QueryBuilders.matchAllQuery()
		// QueryBuilders.matchAllQuery();

		sourceBuilder.query(termQueryBuilder);
		// 分页功能
		// sourceBuilder.from();
		// sourceBuilder.size();
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

		request.source(sourceBuilder);
		SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
		System.out.println(JSON.toJSONString(response.getHits()));
		System.out.println("=======================");
		// 获取每个对象.getHits()中的每个值.getHits().getHits()
		for (SearchHit documentFields : response.getHits().getHits()) {
			// 将获取的值打印成集合
			System.out.println(documentFields.getSourceAsMap());
		}
	}

	@Test
	void contextLoads() {
	}

}
