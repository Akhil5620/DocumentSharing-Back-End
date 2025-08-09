package com.documentshare.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Set max file size to 25MB
        factory.setMaxFileSize(DataSize.ofMegabytes(25));
        
        // Set max request size to 25MB (for the entire request including file)
        factory.setMaxRequestSize(DataSize.ofMegabytes(25));
        
        return factory.createMultipartConfig();
    }
} 