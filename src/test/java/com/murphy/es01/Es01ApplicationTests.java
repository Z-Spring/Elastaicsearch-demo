package com.murphy.es01;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.es01.entity.User;
import net.minidev.json.JSONArray;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class Es01ApplicationTests {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    ObjectMapper objectMapper;
    @Test
    void contextLoads() throws IOException {
        //创建索引
        CreateIndexRequest request = new CreateIndexRequest("test001");
        //客户端执行请求,请求后获得响应
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);

    }
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("test001");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    /**
     * PUT /test001/_doc/1
     * @throws IOException
     */
    @Test
    void createDocument() throws IOException {
        //创建对象
        User user = new User("qq",12,"帅");
        //创建请求
        IndexRequest request = new IndexRequest("test001");
        //规则
        request.id("4");
        request.timeout("1s");
//        request.timeout(TimeValue.timeValueSeconds(1));
        //将数据user放入请求
        request.source(objectMapper.writeValueAsString(user), XContentType.JSON);
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        System.out.println(index);

    }
    @Test
    void getDocument() throws IOException{
        GetRequest request = new GetRequest("test001", "1");
        request.fetchSourceContext();
        boolean exists = restHighLevelClient.exists(request, RequestOptions.DEFAULT);
        if (exists){
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            System.out.println(response);
            System.out.println(response.getSource());
            System.out.println(response.getField(""));
            System.out.println(response.getField("age"));

        }
    }
    @Test
    void updateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("test001","1");
        User user = new User("李四", 25, "丑");
        updateRequest.doc(objectMapper.writeValueAsString(user),XContentType.JSON);
        restHighLevelClient.update(updateRequest,RequestOptions.DEFAULT);
    }
    @Test
    void searchDocument() throws IOException{
        SearchRequest searchRequest = new SearchRequest("test002");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //这里需要注意一下，termQuery的key需要加上keyword
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name.keyword", "springboot详解");

        searchSourceBuilder.query(termQueryBuilder);

        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(objectMapper.writeValueAsString(searchResponse.getHits().getHits()));
        System.out.println("*****************************************************");
        System.out.println(objectMapper.writeValueAsString(searchResponse.getHits()));
        System.out.println("*****************************************************");
        for (SearchHit documentFields:searchResponse.getHits().getHits()){
            System.out.println(documentFields.getSourceAsMap());
        }

    }

    /**
     * search document
     */
    @Test
    void searchDocument2() throws IOException {
        //创建搜索请求
        SearchRequest searchRequest = new SearchRequest();
        //其他
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query();
        searchRequest.source(searchSourceBuilder);
        //响应请求
        restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        //
    }

}
