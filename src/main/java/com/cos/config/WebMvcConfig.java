package com.cos.config;

import org.springframework.boot.web.servlet.view.MustacheViewResolver; //..web.react... 는 안 됨
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    MustacheViewResolver resolver = new MustacheViewResolver();
    resolver.setCharset("UTF-8");
    resolver.setContentType("text/html; charset=UTF-8");
    resolver.setPrefix("classpath:/templates/"); // classpath: -> project경로
    resolver.setSuffix(".html");

    registry.viewResolver(resolver);
  }
}
