package com.murphy.es01.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murphy.es01.service.BookService;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

/**
 * @author Murphy
 */
@RestController
public class UserController {
    @Autowired
    RestHighLevelClient restHighLevelClient;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    BookService bookService;
    @GetMapping (value = "/search/{id}")
    public Object getBookInfo(@PathVariable String id) throws IOException {
        GetRequest getRequest = new GetRequest("test002", id);
        getRequest.fetchSourceContext();
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        //搜索书名   .get("name")
        return getResponse.getSource().get("name");
    }
    @GetMapping (value = "/search2/{keyword}")
    public List searchBook(@PathVariable String keyword) throws IOException {
       return bookService.getBook(keyword);
    }

    @GetMapping("/base")
    public ModelAndView getBase(){
        return new ModelAndView("base");
    }
}


