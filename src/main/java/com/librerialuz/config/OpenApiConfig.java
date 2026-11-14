package com.librerialuz.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos generales de la documentación OpenAPI.
 * La documentación interactiva queda en /swagger-ui.html y el JSON en /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI libreriaLuzOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Librería Luz - API REST")
                .version("1.0.0")
                .description("Servicios web del proyecto formativo Librería Luz: autenticación, "
                        + "usuarios, categorías, libros y ventas. Evidencia GA7-220501096-AA5-EV03.")
                .contact(new Contact().name("Juan Sebastián Ávila Patiño - ADSO 3235898")));
    }
}
