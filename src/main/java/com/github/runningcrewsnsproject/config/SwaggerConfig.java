package com.github.runningcrewsnsproject.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

//    @Value("${aws.server-url}")
//    private String serverUrl;
    @Value("${spring.datasource.server-url}")
    private String newServerUrl;


    @Bean
    public OpenAPI openAPI(@Value("${springdoc.version}") String openApiVersion) {

        Info info = new Info()
                .title("Running Crew SNS Project")
                .version(openApiVersion)
                .description("런닝크루 SNS 프로젝트 API 명세서");



//        Server server = new Server();
//        server.setUrl(serverUrl);
//        server.setDescription("Spare Server");

        Server newServer = new Server();
        newServer.setUrl(newServerUrl);
        newServer.setDescription("AWS Server");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080/");
        localServer.setDescription("Local Server");

        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("access",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("Authorization"))
                        .addSecuritySchemes("refresh",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.HEADER).name("RefreshToken"))
                        .addSecuritySchemes("cookie",
                                new SecurityScheme().type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.COOKIE).name("RefreshToken"))
                )
                .info(info)
                .servers(List.of(localServer, newServer))

                .addSecurityItem(new SecurityRequirement().addList("access"))
                .addSecurityItem(new SecurityRequirement().addList("refresh"))
                .addSecurityItem(new SecurityRequirement().addList("cookie"));
//                .addServersItem(new Server().url(serverUrl).description("HTTPS Production Server")) // HTTPS 서버 추가
//                .addServersItem(new Server().url("http://localhost:8080").description("로컬 서버")); // 로컬 서버 추가
    }


}
