🛡️ Auth API - Módulo de Identidade e Segurança (BarberShop Backend)

Este projeto é um Módulo de Autenticação RESTful autônomo, desenhado para ser o provedor de identidade de qualquer aplicação moderna que exija segurança e controle de acesso por papéis.

O foco principal foi construir uma fundação robusta que garanta a segurança Stateless da API antes da implementação de qualquer lógica de negócio (como agendamentos ou faturamento).

✨ Destaques da Arquitetura e Segurança

A API foi desenvolvida em Java com Spring Boot, seguindo uma arquitetura limpa e orientada à segurança.

1. Segurança Nível Enterprise (JWT & BCrypt):

    O sistema implementa o Spring Security para autenticação e autorização.

    Utiliza JSON Web Tokens (JWT) para gerenciar sessões stateless, o que é crucial para performance e escalabilidade.

    Garante a segurança dos dados do usuário através da criptografia de senhas com o algoritmo BCrypt antes de qualquer persistência.

2. Controle de Acesso por Papéis (RBAC):

    A segurança é configurada para gerenciar o acesso de dois perfis essenciais: CLIENT (usuário comum) e OWNER (administrador).

    As regras de autorização verificam o papel do usuário (a role) no token antes de permitir o acesso a rotas específicas, prevenindo o acesso não autorizado.

3. Arquitetura Limpa e Reuso:

    A aplicação segue o padrão de Arquitetura em Camadas (Controller/Service/Repository), aderindo estritamente aos princípios SOLID.

    A lógica de serviço é desacoplada e utiliza DTOs (Java Records) para garantir a imutabilidade e a clareza do transporte de dados.

⚙️ Configuração e Execução

Tecnologias Core

O projeto utiliza PostgreSQL (Dockerizado) para persistência de dados e Spring Data JPA / Hibernate para a comunicação com o banco.

Inicialização

Para rodar este módulo localmente:

    Requisitos: Certifique-se de que Docker e JDK 21+ estão instalados.

    Variáveis de Ambiente: Defina a chave secreta (jwt-secret-key) em seu ambiente ou no application.properties.

    Docker Compose: Inicie o contêiner do banco de dados:
    Bash

docker compose up -d

Iniciar o Servidor: Inicie a aplicação Spring Boot:
Bash

    ./mvnw spring-boot:run

Documentação e Prova de Vida

A documentação interativa da API é gerada automaticamente pelo Swagger/OpenAPI e está acessível em:

http://localhost:8081/swagger-ui/index.html
