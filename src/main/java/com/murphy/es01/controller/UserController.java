package com.murphy.es01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.es01.entity.User;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * @author Murphy
 */
@RestController
public class UserController {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    ObjectMapper objectMapper;
    @GetMapping (value = "/search/{id}")
    public Object getBookInfo(@PathVariable String id) throws IOException {
        GetRequest getRequest = new GetRequest("test002", id);
        getRequest.fetchSourceContext();
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        //搜索书名   .get("name")
        return getResponse.getSource().get("name");
    }
    @GetMapping (value = "/search2/{keyword}")
    public Object searchBook(@PathVariable String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest("test002");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name.keyword", keyword);
        searchSourceBuilder.query(termQueryBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit documentFields:searchResponse.getHits().getHits()){
            return documentFields.getSourceAsMap();
        }
        return "";
    }

    @GetMapping("/base")
    public ModelAndView getBase(){
        return new ModelAndView("base");
    }
}


