package com.xwq;

import com.xwq.service.NovelIndexService;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NovelEsApplicationTests {

	@Autowired
	TransportClient transportClient;

	@Autowired
	NovelIndexService novelIndexService;

	@Test
	public void testSearch() {
		SearchResponse searchResponse = transportClient.prepareSearch("company").setTypes("employee")
				.setQuery(QueryBuilders.matchQuery("position", "technique"))
				.setPostFilter(QueryBuilders.rangeQuery("age").from(30).to(40))
				.setFrom(0).setSize(1)
				.get();

		SearchHit[] hits = searchResponse.getHits().getHits();

		for(SearchHit hit : hits) {
			System.out.println(hit.getSourceAsString());
		}
	}

	@Test
	public void testBulk() throws IOException {
		BulkRequestBuilder builder = transportClient.prepareBulk();

		IndexRequestBuilder requestBuilder = transportClient.prepareIndex("company", "employee")
				.setSource(XContentFactory.jsonBuilder().startObject());
		builder.add(requestBuilder);

		BulkResponse response = builder.get();

		boolean hasFailures = response.hasFailures();
	}

	@Test
	public void testIndex() throws Exception {
		novelIndexService.indexNovel();
	}
}
