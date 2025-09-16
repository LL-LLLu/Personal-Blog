package com.luqi.weblog.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author: lu qi
 * @url: www.luqi.com
 * @date: 2023-08-16 7:53
 * @description: Knife4j config
 **/
@Configuration
@EnableSwagger2WebMvc
@Profile("dev") // only activate in Dev environment
public class Knife4jConfig {

    @Bean("webApi")
    public Docket createApiDoc() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                // 分组名称
                .groupName("Web Front-end Interface")
                .select()
                // 这里指定 Controller 扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.luqi.weblog.web.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 构建 API 信息
     * @return
     */
    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title("Weblog Front-end Interface Doc") // Title
                .description("Weblog is a personal blog using Spring Boot + Vue 3.2 + Vite 4.3") // Description
                .termsOfServiceUrl("https://www.luqi.com/") // API Service Code
                .contact(new Contact("luqi", "https://www.luqi.com", "qilutx@qq.com")) // contact info
                .version("1.0") // version #
                .build();
    }
}