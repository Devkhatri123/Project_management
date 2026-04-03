package com.project_management.project_management.config;

import com.cloudinary.Cloudinary;
import com.project_management.project_management.config.websocket.CustomHandShakerHandler;
import com.project_management.project_management.config.websocket.CustomWebSocketInterceptor;
import com.project_management.project_management.jwt.JwtFilter;
import com.project_management.project_management.service.SchedulerService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.record.RecordModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.context.annotation.Configuration
@Slf4j
@EnableWebSocketMessageBroker
public class Configuration implements WebSocketMessageBrokerConfigurer {
    private final JwtFilter jwtFilter;
    @Value("${spring.cloudinary.cloud_name}")
    private String cloudinary_cloud_name;
    @Value("${spring.cloudinary.api_key}")
    private String cloudinary_api_key;
    @Value("${spring.cloudinary.api_secret}")
    private String cloudinary_api_secret;

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
                        .requestMatchers("/workspace/project/task/**").permitAll()
                        .anyRequest().permitAll();
                })
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public Cloudinary getCloudinary(){
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudinary_cloud_name);
        config.put("api_key", cloudinary_api_key);
        config.put("api_secret", cloudinary_api_secret);
        return new Cloudinary(config);
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
    @Bean
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(4);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduler-");
        threadPoolTaskScheduler.initialize();
        return threadPoolTaskScheduler;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
     config.enableSimpleBroker("/topic", "/queue");
     config.setUserDestinationPrefix("/user");
     config.setApplicationDestinationPrefixes("/app");
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry endpointRegistry){
        endpointRegistry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new CustomHandShakerHandler())
                .withSockJS();
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration channelRegistration){
        channelRegistration.interceptors(new CustomWebSocketInterceptor());
    }
}
