package com.project.raidho.config;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

public class SwaggerConfig {

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

    @Bean
    public Docket apiPostHeart() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .apiInfo(apiPostLikeInfo())
                .groupName("Raidho PostHeart")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.project.raidho.controller"))
                .paths(PathSelectors.ant("/api/postheart/**"))
                .build();
    }

    @Bean
    public Docket apiMyPage() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .apiInfo(apiEditMemberInfo())
                .groupName("Raidho EditMember")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.project.raidho.controller"))
                .paths(PathSelectors.ant("/api/mypage/**"))
                .build();
    }

    @Bean
    public Docket apiChatRoom() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .apiInfo(apiChatRoomInfo())
                .groupName("Raidho ChatRoom")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.project.raidho.controller"))
                .paths(PathSelectors.ant("/api/chat/**"))
                .build();
    }

    @Bean
    public Docket apiCategory() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .apiInfo(apiCategoryInfo())
                .groupName("Raidho Category")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.project.raidho.controller"))
                .paths(PathSelectors.ant("/api/category/**"))
                .build();
    }

    @Bean
    public Docket apiTagSearch() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .apiInfo(apiTagSearchInfo())
                .groupName("Raidho TagSearch")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.project.raidho.controller"))
                .paths(PathSelectors.ant("/api/search/**"))
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

    private ApiInfo apiPostLikeInfo() {
        return new ApiInfoBuilder()
                .title("Post Heart API")
                .description("[Raidho] Post Heart API")
                .version("1.0")
                .build();
    }

    private ApiInfo apiEditMemberInfo() {
        return new ApiInfoBuilder()
                .title("Edit Member API")
                .description("[Raidho] Edit Member API")
                .version("1.0")
                .build();
    }

    private ApiInfo apiChatRoomInfo() {
        return new ApiInfoBuilder()
                .title("Chat Room API")
                .description("[Raidho] Chat Room API")
                .version("1.0")
                .build();
    }

    private ApiInfo apiCategoryInfo() {
        return new ApiInfoBuilder()
                .title("Category API")
                .description("[Raidho] Category API")
                .version("1.0")
                .build();
    }

    private ApiInfo apiTagSearchInfo() {
        return new ApiInfoBuilder()
                .title("Tag Search API")
                .description("[Raidho] Tag Search API")
                .version("1.0")
                .build();
    }
}
