package com.chdlsp.patronpage.config;

import com.sun.beans.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.awt.print.Pageable;
import java.util.Collections;

/* swagger 적용 참고 : https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api/ */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/project/**"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Patron Page API",
                "simple patron-page service project",
                "Beta",
                "https://github.com/chdlsp/patron-page#patron-page",
                new Contact("Sungmin Park", "https://github.com/chdlsp/", "chdlsp@gmail.com"),
                "Apache 2.0", "http://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());
    }
}
