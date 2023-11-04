package com.mashanlote.configuration;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserDetailsService service;

    public SecurityConfiguration(UserDetailsService service) {
        this.service = service;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcRequestMatcher = new MvcRequestMatcher.Builder(introspector);
        return http
                .authorizeHttpRequests(requests -> requests
//                                .requestMatchers(PathRequest.toH2Console()).permitAll()
//                                .requestMatchers(HttpMethod.POST, "/register").permitAll()
                                .requestMatchers(mvcRequestMatcher.pattern(HttpMethod.POST, "/register")).anonymous()
                                .anyRequest().authenticated()
//                                .requestMatchers(mvcRequestMatcher.pattern(HttpMethod.GET, "/**")).hasAnyAuthority("ADMIN", "USER")
//                                .requestMatchers(mvcRequestMatcher.pattern(HttpMethod.POST, "/**")).hasAnyAuthority("ADMIN")
//                                .requestMatchers(mvcRequestMatcher.pattern(HttpMethod.DELETE, "/**")).hasAnyAuthority("ADMIN")
//                                .requestMatchers(mvcRequestMatcher.pattern(HttpMethod.PUT, "/**")).hasAnyAuthority("ADMIN")
                )
                .httpBasic(withDefaults())
                .userDetailsService(service)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .build();
    }

}
