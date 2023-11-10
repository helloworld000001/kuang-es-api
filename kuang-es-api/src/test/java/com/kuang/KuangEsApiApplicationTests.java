package com.kuang;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KuangEsApiApplicationTests {
	@Autowired
	RestHighLevelClient restHighLevelClient;

	@Test
	void contextLoads() {
	}

}
