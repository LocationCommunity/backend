package com.easytrip.backend.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String userHome = System.getProperty("user.home");

        String desktopPath = userHome + File.separator + "Desktop";

        String imagePath = desktopPath + File.separator + "images" + File.separator;
        registry.addResourceHandler("/images/**")
                .setCachePeriod(0)
                .addResourceLocations("file:" + imagePath);



    }

}
