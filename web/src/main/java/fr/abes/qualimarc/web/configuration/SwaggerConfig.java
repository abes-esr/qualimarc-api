package fr.abes.qualimarc.web.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI OpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API Qualimarc")
                        .description("Service web RESTful de l'application QUALIMARC.")
                        .version(this.getClass().getPackage().getImplementationVersion())
                        .contact(new Contact().url("https://github.com/abes-esr/qualimarc-api").name("Abes").email("scod@abes.fr")));
    }

//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("fr.abes.qualimarc.web.controller"))
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(metadata());
//    }
//
//    private ApiInfo metadata() {
//        return new ApiInfoBuilder()
//                .title("QUALIMARC API")
//                .description("Service web RESTful de l'application QUALIMARC.")
//                .version("1.0.0")
//                .contact(new Contact("Abes", "https://github.com/abes-esr/qualimarc-api.git", "scod@abes.fr"))
//                .build();
//    }
}
