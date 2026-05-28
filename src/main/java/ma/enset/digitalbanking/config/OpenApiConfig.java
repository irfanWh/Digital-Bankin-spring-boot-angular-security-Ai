package ma.enset.digitalbanking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI digitalBankingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Digital Banking API")
                        .description("REST API for the Digital Banking application")
                        .version("v1")
                        .contact(new Contact()
                                .name("Digital Banking Team")
                                .email("support@example.com")));
    }
}
