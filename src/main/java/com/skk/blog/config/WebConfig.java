package com.skk.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置本地文件访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/Users/jiangshikang/IdeaProjects/myblog/uploads/");

        // 配置静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // Swagger UI
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/swagger-ui/");
    }
}
