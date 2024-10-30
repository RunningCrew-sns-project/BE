////package com.github.accountmanagementproject.config;
////
////import org.springframework.context.annotation.Configuration;
////import org.springframework.web.servlet.config.annotation.CorsRegistry;
////import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
////
//
/*
    SecurityConfig에 이미 CORS 설정을 해놨는데 설정이 중복되서 주석처리 합니다.

    혹시 몰라서 충돌 해결안하고 주석 처리만 했습니다.
 */
//
//
//<<<<<<< HEAD
////@Configuration
////public class WebConfig implements WebMvcConfigurer {
////
////    private static final String DEVELOP_FRONT_ADDRESS = "http://localhost:3000";
////
////    @Override
////    public void addCorsMappings(CorsRegistry registry) {
////        registry.addMapping("/**")
////                .allowedOrigins(DEVELOP_FRONT_ADDRESS)
////                .allowedMethods("GET", "POST", "PUT", "DELETE")
////                .exposedHeaders("location")
////                .allowedHeaders("*")
////                .allowCredentials(true);
////    }
////}
//=======
//    private static final String DEVELOP_FRONT_ADDRESS = "http://localhost:3000";
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
////                .allowedOrigins(DEVELOP_FRONT_ADDRESS, "http://localhost:63342")
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .exposedHeaders("location")
//                .allowedHeaders("*")
//                .allowCredentials(true);
//    }
//}
//>>>>>>> ff54ada7d92f3600472cd04a888b9ec26b52217d
