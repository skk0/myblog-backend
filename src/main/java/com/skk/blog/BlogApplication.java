package com.skk.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.skk.blog.mapper")
public class BlogApplication {
    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
        System.out.println("============================================");
        System.out.println("  MyBlog 后端服务启动成功!");
        System.out.println("  API文档: http://localhost:8080/api/doc.html");
        System.out.println("============================================");
    }
}
