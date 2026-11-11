
package com.medislot.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class MediSlotApplication {

    public static void main(String[] args) {

        SpringApplication.run(
            MediSlotApplication.class, args
        );

        System.out.println("==============================");
        System.out.println(" MediSlot Backend Started!");
        System.out.println("==============================");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {

        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")
                    .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*"
                    )
                    .allowedMethods(
                        "GET", "POST", "PUT", "DELETE", "OPTIONS"
                    )
                    .allowedHeaders("*");
            }
        };
    }
}
