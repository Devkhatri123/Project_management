package com.project_management.project_management.config;

import com.project_management.project_management.jwt.JwtFilter;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.record.RecordModule;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@org.springframework.context.annotation.Configuration
@Slf4j
public class Configuration {
    private final JwtFilter jwtFilter;

    public Configuration(final JwtFilter jwtFilter){
        this.jwtFilter = jwtFilter;
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder(13);
    }


    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception{
        return http.csrf(csrf->csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth->{ auth
                        .requestMatchers("/workspace/**").authenticated()
                        .requestMatchers("/auth/me").authenticated()
                        .requestMatchers("/workspace/project/**").authenticated()
                        .anyRequest().permitAll();
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("localhost:8080","http://localhost:5000","http://localhost:*"));
        configuration.setAllowedHeaders(List.of(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE"));
        configuration.setAllowCredentials(true);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    @Bean
    public ModelMapper mapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.registerModule(new RecordModule());
        return modelMapper;
    }
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.warn("Task rejected, thread pool is full and queue is also full"));
        executor.initialize();
        return executor;
    }
}
