package com.murphy.es01.service;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Murphy
 */
@Service
public class BookService {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    public List getBook(String keyword) throws IOException {
        List <Object>list=new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest("test002");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //模糊搜索
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", keyword);
        MatchQueryBuilder matchQueryBuilder2 = QueryBuilders.matchQuery("tags", keyword);
        //bool查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(matchQueryBuilder)
                .should(matchQueryBuilder2);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name")
                .field("tags")
                .preTags("<span class='test' style='color:red'>")
                .postTags("</span>");

        searchSourceBuilder
                .query(matchQueryBuilder)
                .query(boolQueryBuilder)
                .highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit documentFields:searchResponse.getHits().getHits()){
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            HighlightField tags = highlightFields.get("tags");
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            //解析高亮的字段，将原来的字段替换成新的高亮字段
            if (name!=null){
                Text[] texts = name.fragments();
                String new_name="";
                for (Text text : texts) {
                    new_name+=text;
                }
                sourceAsMap.put("name",new_name);
            }
            if (tags!=null){
                Text[] texts2 = tags.fragments();
                String new_tags="";
                for (Text text2 : texts2) {
                    new_tags+=text2;
                }
                sourceAsMap.put("tags",new_tags);
            }
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }
}
