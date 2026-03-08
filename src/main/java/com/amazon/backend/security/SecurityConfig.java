package com.amazon.backend.security;

import com.amazon.backend.ratelimit.RateLimitProperties;
import com.amazon.backend.ratelimit.RateLimiterFilter;
import com.amazon.backend.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsConfig userDetailsConfig;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RateLimitProperties rateLimitProperties;

    public SecurityConfig(UserDetailsConfig userDetailsConfig, JwtService jwtService, UserRepository userRepository,
                          RateLimitProperties rateLimitProperties) {
        this.userDetailsConfig = userDetailsConfig;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.rateLimitProperties = rateLimitProperties;
    }

    @Bean
    public RateLimiterFilter rateLimiterFilter() {
        return new RateLimiterFilter(rateLimitProperties);
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter(jwtService, userRepository);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Allow preflight OPTIONS requests
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Auth endpoints - public
                        .requestMatchers("/api/auth/**").permitAll()
                        // Products - public GET
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        // Reviews - public GET only
                        .requestMatchers(HttpMethod.GET, "/api/products/*/reviews/**").permitAll()
                        // Health check - public
                        .requestMatchers("/api/health").permitAll()
                        // Everything else requires JWT:
                        // /api/cart/**, /api/orders/**, /api/addresses/**
                        // /api/wishlist/**, POST/PUT/DELETE on reviews
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .userDetailsService(userDetailsConfig.userDetailsService())
                .addFilterBefore(rateLimiterFilter(), JwtAuthFilter.class)
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
