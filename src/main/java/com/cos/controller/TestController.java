package com.cos.controller;

import com.cos.model.Blog;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @RequestMapping("/rest/test")
  public Blog test() {
    System.out.println("hi");
    Blog blog = new Blog();
    blog.setTitle("테스트1");
    blog.setContent("테스트1 내용");

    return blog;
  }
}
