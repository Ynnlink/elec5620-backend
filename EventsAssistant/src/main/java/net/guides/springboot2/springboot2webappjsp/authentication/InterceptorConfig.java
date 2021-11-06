package net.guides.springboot2.springboot2webappjsp.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/facial/**")
                .excludePathPatterns("/profile/**")
                .excludePathPatterns("/event/**")
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/rescue_team/**");
    }

    @Bean
    public JwtInterceptor authenticationInterceptor() {
        return new JwtInterceptor();
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedOrigins("http://127.0.0.1:8080")
                .allowCredentials(true)
                .allowedHeaders(CorsConfiguration.ALL).allowedMethods(CorsConfiguration.ALL)
                .maxAge(3600 * 24);
    }


}