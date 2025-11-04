package ar.edu.unq.pdss22025.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Compra-tu-auto API")
                        .version("0.0.1")
                        .description("Documentación OpenAPI para el backend Compra-tu-auto"));
    }
}

