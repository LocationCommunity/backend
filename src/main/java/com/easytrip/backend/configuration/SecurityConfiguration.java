package com.easytrip.backend.configuration;

import com.easytrip.backend.member.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  private final JwtTokenFilter jwtTokenFilter;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    return http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            requests -> requests
                .requestMatchers("/members/sign-up", "/members/auth", "/members/login/**",
                    "/members/password", "/weather/**").permitAll()
                .requestMatchers("/members/logout", "/members/my-info", "/members/withdrawal",
                    "/members/reissue", "/members/bookmark", "/members/interest", "/place/**",
                    "/boards/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/exhibitions/**").hasRole("USER")
                .requestMatchers("/admin/**", "/exhibitions/**").hasRole("ADMIN"))

        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
            SessionCreationPolicy.STATELESS))
        .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class).build();
  }

  @Bean
  public PasswordEncoder PasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
