package com.Shopping.Shopping.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:${user.dir}/uploads}")
    private String uploadDir;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from the configured uploads directory
        String uploadsUri = Paths.get(uploadDir).toUri().toString();
        if (!uploadsUri.endsWith("/")) {
            uploadsUri = uploadsUri + "/"; // Spring expects trailing slash for directories
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsUri);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/access-denied").setViewName("access-denied");
    }
}

