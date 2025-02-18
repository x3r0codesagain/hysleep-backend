package com.app.octo.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * Used for CORS issue; so backend will take frontend request
 */
@Configuration
@EnableWebMvc
public class WebConfig {

  @Bean
  public FilterRegistrationBean corsFilterRegistration(){
    UrlBasedCorsConfigurationSource source = new
        UrlBasedCorsConfigurationSource();
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowCredentials(true);
    configuration.addAllowedOrigin("http://localhost:3000");
    configuration.addAllowedOrigin("http://localhost:3200");
    configuration.addAllowedOrigin("http://localhost:8082");
    configuration.addAllowedOrigin("http://localhost:8081");
    configuration.addAllowedOrigin("http://localhost:5173");
    List<String> allowedHeaders = new ArrayList<>();
    allowedHeaders.add("*");
    List<String> allowedMethods = new ArrayList<>();
    allowedMethods.add("*");
    addAllowedHeaders(allowedHeaders);
    addAllowedMethods(allowedMethods);

    configuration.setAllowedHeaders(allowedHeaders);
    configuration.setAllowedMethods(allowedMethods);

    //time when the request is accepted
    configuration.setMaxAge(4000L);

    source.registerCorsConfiguration("/**", configuration);
    FilterRegistrationBean registrationBean = new FilterRegistrationBean(new CorsFilter(source));
    registrationBean.setOrder(-102);
    return registrationBean;
  }

  private void addAllowedHeaders(List<String> allowedHeaders) {
    allowedHeaders.add(HttpHeaders.AUTHORIZATION);
    allowedHeaders.add(HttpHeaders.CONTENT_TYPE);
    allowedHeaders.add(HttpHeaders.ACCEPT);
  }

  private void addAllowedMethods(List<String> allowedMethods) {
    allowedMethods.add(HttpMethod.POST.name());
    allowedMethods.add(HttpMethod.GET.name());
    allowedMethods.add(HttpMethod.PUT.name());
  }

}
