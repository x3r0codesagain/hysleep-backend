package com.app.octo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

  private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
  private final UserAuthProvider userAuthProvider;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .exceptionHandling((exception) ->
            exception.authenticationEntryPoint(userAuthenticationEntryPoint))
        .addFilterBefore(new JwtAuthFilter(userAuthProvider),
            BasicAuthenticationFilter.class)
        .csrf(csrf -> csrf.disable())
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(requests -> requests
            .requestMatchers("/api/v1/*/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/*/public/**").authenticated()
            .requestMatchers("/api/v1/*/public-all-access/**").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/users/**").permitAll()
            .anyRequest().authenticated());

    return httpSecurity.build();
  }

}
