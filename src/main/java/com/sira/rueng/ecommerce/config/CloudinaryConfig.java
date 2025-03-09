package com.sira.rueng.ecommerce.config;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${CLOUDINARY_URL}")
    private String cloudinary_URL;

    @Bean
    public Cloudinary cloudinary() {
//        Dotenv dotenv = Dotenv.load();
//        return new Cloudinary(dotenv.get("CLOUDINARY_URL"));
//        System.out.println("test: " + cloudinary_URL);
        return new Cloudinary(cloudinary_URL);

    }
}