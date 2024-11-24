package com.github.accountmanagementproject.config.security;

import com.github.accountmanagementproject.config.security.event.CustomAccessDeniedHandler;
import com.github.accountmanagementproject.config.security.event.CustomAuthenticationEntryPoint;
import com.github.accountmanagementproject.web.filtersAndInterceptor.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@Configuration
//웹요청과 응답이 시큐리티 필터체인을 거치게 해줌
@EnableWebSecurity
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    @Value("${spring.datasource.https-server-url}")
    private String httpsServerUrl;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(c->c.disable())
//                .httpBasic(h->h.disable())
//                .headers(h->h.frameOptions(f->f.sameOrigin()))
                .cors(c->c.configurationSource(corsConfigurationSource()))
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e->{
                    e.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
                    e.accessDeniedHandler(new CustomAccessDeniedHandler());
                })
                .authorizeHttpRequests(a->a
                        .requestMatchers("/api/auth/authorize-test").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/api/crews/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE,"/api/crews/**").authenticated()
                        .requestMatchers("/api/auth/auth-test", "/api/account/*", "/api/run-post/users", "/api/crews/*/about-user", "/api/crews/*/admin/*", "/api/notifications/connect").authenticated()
                        .requestMatchers("/resources/**","/api/auth/*", "/api/email/*",
                                "/error","/swagger-ui/**", "/v3/api-docs/**", "/running-docs.html").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)//인증이전 실행
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:*","https://runningcrew.netlify.app/", httpsServerUrl));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(List.of("GET","PUT","POST","PATCH","DELETE","OPTIONS"));
        corsConfiguration.setMaxAge(1000L*60*60);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
