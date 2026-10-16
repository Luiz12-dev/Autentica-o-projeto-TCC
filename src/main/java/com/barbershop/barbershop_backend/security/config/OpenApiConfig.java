package com.barbershop.barbershop_backend.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Identifica esta API no Swagger. Sem isto a página abre como
 * "OpenAPI definition", sem dizer de qual dos dois serviços se trata.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth BarberShop API")
                        .version("1.0.0")
                        .description("""
                                Microsserviço de autenticação do sistema de gerenciamento de barbearia.

                                Cadastra clientes e emite o JWT (HS512, validade de 15 minutos) que a \
                                Core API (porta 8080) aceita. Os dois serviços compartilham apenas a \
                                chave de assinatura do token.
                                """));
    }
}
