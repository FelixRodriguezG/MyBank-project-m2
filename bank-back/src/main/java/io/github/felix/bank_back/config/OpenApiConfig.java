package io.github.felix.bank_back.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI baseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Back API")
                        .version("v1")
                        .description("API para la gestión de cuentas bancarias, transacciones y usuarios.")
                        .contact(new Contact().name("Felix Rodríguez").email("felix.rg.coding.is.fun@gmail.com"))
                        .license(new License().name("MIT"))
                )
            .components(new Components().addSecuritySchemes("bearer-jwt",
            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }

    // Grupos (secciones en Swagger UI)
    @Bean
    public GroupedOpenApi accountsGroup() {
        return GroupedOpenApi.builder()
                .group("Accounts")
                .pathsToMatch("/api/accounts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("Auth")
                .pathsToMatch("/api/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi holdersGroup() {
        return GroupedOpenApi.builder()
                .group("Holders")
                .pathsToMatch("/api/holder/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminsGroup() {
        return GroupedOpenApi.builder()
                .group("Admins")
                .pathsToMatch("/api/admins/**")
                .build();
    }

    @Bean
    public GroupedOpenApi thirdPartyGroup() {
        return GroupedOpenApi.builder()
                .group("Third-Party")
                .pathsToMatch("/api/third-party/**")
                .build();
    }

    @Bean
    public GroupedOpenApi transactionsGroup() {
        return GroupedOpenApi.builder()
                .group("Transactions")
                .pathsToMatch("/api/transactions/**")
                .build();
    }
}
