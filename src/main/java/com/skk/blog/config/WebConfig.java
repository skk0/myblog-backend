package com.skk.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadPath;

    public WebConfig(@Value("${blog.upload.path}") String uploadPath) {
        this.uploadPath = uploadPath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置本地文件访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + ensurePathEndWithSlash(uploadPath));

        // 配置静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // Swagger UI
        registry.addResourceHandler("/doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/swagger-ui/");
    }

    private String ensurePathEndWithSlash(String path) {
        if (path.endsWith("/")) {
            return path;
        }
        return path + "/";
    }
}
