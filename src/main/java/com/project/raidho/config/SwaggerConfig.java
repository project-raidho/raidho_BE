package com.project.raidho.config;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

public class SwaggerConfig {

    // http://localhost:8080/swagger-ui/index.html

    @Bean
    public Docket apiPost() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false) // swagger 기본 응답 변경 (200~, 400~, 500~), false 로 설정하면 기본 응답 코드 노출하지 않음
                .apiInfo(apiPostInfo())
                .groupName("Raidho POST")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.project.raidho.controller"))
                .paths(PathSelectors.ant("/api/post/**"))
                .build();
    }

    @Bean
    public Docket apiMeetingPost() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .apiInfo(apiMeetingPostInfo())
                .groupName("Raidho MeetingPOST")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.project.raidho.controller"))
                .paths(PathSelectors.ant("/api/meeting/**"))
                .build();
    }


    private ApiInfo apiPostInfo() {
        return new ApiInfoBuilder()
                .title("Post API")
                .description("[Raidho] POST API")
                .version("1.0")
                .build();
    }

    private ApiInfo apiMeetingPostInfo() {
        return new ApiInfoBuilder()
                .title("Meeting Post API")
                .description("[Raidho] Meeting POST API")
                .version("1.0")
                .build();
    }
}
