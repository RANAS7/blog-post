package com.example.blogPost;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class BlogPostApplication {

    public static void main(String[] args) {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                .filename(".env") // specify the filename (optional if named .env)
                .load();

//		Set system properties for database
        System.setProperty("SPRING_DATASOURCE_URL", dotenv.get("SPRING_DATASOURCE_URL"));
        System.setProperty("SPRING_DATASOURCE_USERNAME", dotenv.get("SPRING_DATASOURCE_USERNAME"));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", dotenv.get("SPRING_DATASOURCE_PASSWORD"));
        System.setProperty("SERVER_PORT", dotenv.get("SERVER_PORT"));

//		Set system properties for Cloudinary
        System.setProperty("CLOUDINARY_CLOUD_NAME", dotenv.get("CLOUDINARY_CLOUD_NAME"));
        System.setProperty("CLOUDINARY_API_KEY", dotenv.get("CLOUDINARY_API_KEY"));
        System.setProperty("CLOUDINARY_API_SECRET", dotenv.get("CLOUDINARY_API_SECRET"));
        System.setProperty("CLOUDINARY_FOLDER_NAME", dotenv.get("CLOUDINARY_FOLDER_NAME"));
        System.setProperty("CLOUDINARY_URL",dotenv.get("CLOUDINARY_URL"));

//		Set System properties for Jwt Authentication
        System.setProperty("JWT_SECRET_KEY", dotenv.get("JWT_SECRET_KEY"));

//        Set system properties for Mail
        System.setProperty("SMTP_HOST", dotenv.get("SMTP_HOST"));
        System.setProperty("SMTP_PORT", dotenv.get("SMTP_PORT"));
        System.setProperty("EMAIL_USERNAME", dotenv.get("EMAIL_USERNAME"));
        System.setProperty("EMAIL_PASSWORD", dotenv.get("EMAIL_PASSWORD"));

        //        Set system properties for Mail
        System.setProperty("DOMAIN", dotenv.get("DOMAIN"));

        SpringApplication.run(BlogPostApplication.class, args);
    }

}
