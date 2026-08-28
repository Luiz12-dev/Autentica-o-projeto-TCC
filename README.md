# 🛡️ Auth API — Módulo de Autenticação Reutilizável

API RESTful de autenticação e autorização, construída com **Java 21** e **Spring Boot 3.3.4**. Projetada como um **módulo independente e reutilizável** — pronto para ser integrado em qualquer aplicação que precise de registro, login, JWT e controle de acesso por roles.

> A ideia é simples: você clona esse repositório, adapta as roles e os endpoints para o seu domínio, e sai com uma camada de segurança completa sem precisar construir do zero.

---

## 🚀 Tecnologias

| Tecnologia | Versão | Descrição |
|-----------|--------|-----------|
| **Java** | 21 | Linguagem principal (LTS) |
| **Spring Boot** | 3.3.4 | Framework principal |
| **Spring Security** | 6.x | Autenticação e autorização |
| **Spring Data JPA** | 3.x | Persistência e ORM |
| **PostgreSQL** | 15 | Banco de dados relacional |
| **JJWT** | 0.11.5 | Geração e validação de tokens JWT (HS512) |
| **Lombok** | — | Redução de boilerplate |
| **Bean Validation** | 3.x | Validação de dados de entrada |
| **SpringDoc OpenAPI** | 2.6.0 | Swagger UI interativo |
| **Spring Actuator** | — | Health checks e monitoramento |
| **H2 Database** | — | Banco em memória para testes de integração |
| **Docker Compose** | — | Infraestrutura containerizada |

---

## 📐 Arquitetura

O projeto segue a **Arquitetura em Camadas** com separação clara de responsabilidades, organizada para ser facilmente plugável em qualquer aplicação:

```
src/main/java/com/barbershop/barbershop_backend/
├── controllers/           # Endpoints REST (Auth + Test)
├── dto/                   # Java Records para request/response
├── Enum/                  # Roles do sistema (CLIENT, OWNER)
├── exceptions/            # Exceções customizadas + GlobalHandler
├── models/                # Entidade User (implements UserDetails)
├── repositorys/           # Spring Data JPA Repository
├── security/
│   ├── config/            # SecurityConfig (SecurityFilterChain, CORS, BCrypt)
│   ├── Jwt/               # JwtUtils, JwtAuthenticationFilter, JwtAuthEntryPoint
│   └── services/          # AuthService, UserDetailsServiceImpl
└── BarbershopBackendApplication.java
```

---

## 🔐 Funcionalidades

### Autenticação & Autorização
- **Registro** com validação de dados e detecção de e-mail duplicado
- **Login** com geração de **Access Token + Refresh Token** (JWT HS512)
- **Criptografia BCrypt** para todas as senhas
- **Filtro JWT customizado** (`OncePerRequestFilter`) com validação automática
- **Entry Point customizado** — retorna JSON estruturado em caso de 401

### Controle de Acesso (RBAC)
- Dois perfis: `CLIENT` e `OWNER`
- Autorização via `@PreAuthorize` com `hasRole()` e `hasAnyRole()`
- Rotas públicas, protegidas e exclusivas por role

### Endpoints de Teste
O projeto inclui um `TestController` com 3 endpoints para validar o RBAC na prática:

| Endpoint | Acesso | Descrição |
|----------|--------|-----------|
| `GET /api/test/protected` | CLIENT + OWNER | Rota compartilhada |
| `GET /api/test/client-only` | Apenas CLIENT | Acesso exclusivo |
| `GET /api/test/owner-only` | Apenas OWNER | Acesso exclusivo |

### Testes de Integração
**13 testes** com `@SpringBootTest` + `MockMvc` + H2 em memória (perfil `test`, em `src/test/resources/application-test.properties`), cobrindo:

| Teste | Cenário |
|-------|---------|
| ✅ `shouldRegisterUserSuccessfully` | Registro retorna 201 + dados sem senha |
| ✅ `shouldLoginUserSuccessfully` | Login retorna 200 + accessToken |
| ✅ `shouldFailedLoginWithIncorrectPassword` | Senha errada retorna 401 |
| ✅ `OwnerRoleShouldAccessRoute` | OWNER acessa rota compartilhada |
| ✅ `clientRoleShouldAccessRoute` | CLIENT acessa rota compartilhada |
| ✅ `ownerShouldAccessOwnerOnlyRoute` | OWNER acessa rota exclusiva |
| ✅ `clientShouldAccesClientRoute` | CLIENT acessa rota exclusiva |
| ✅ `clientShouldBeForbiddenOwnerRout` | CLIENT bloqueado em rota de OWNER (403) |
| ✅ `ownerShouldBeForbiddenClientRout` | OWNER bloqueado em rota de CLIENT (403) |
| ✅ `unauthenticatedUserShouldBeForbidden` | Sem token retorna 401 |
| ✅ `registerShouldAlwaysCreateClientEvenIfOwnerIsRequested` | Payload com `"role": "OWNER"` mesmo assim cria CLIENT |
| ✅ `registerShouldReturnRealNameAndCreationDate` | Resposta traz `fullName` e `createdAt` preenchidos |
| ✅ `contextLoads` | Contexto Spring sobe no perfil `test` |

---

## 📋 Endpoints de Autenticação

### `POST /api/auth/register`
Registra um novo usuário no sistema.

**Request:**
```json
{
  "username": "Luiz Otávio",
  "email": "luiz@email.com",
  "password": "senhaSegura123"
}
```

> O campo `role` **não faz parte do contrato**. Como `/api/auth/register` é
> público, tudo que o cliente envia é controlado por ele — aceitar a role
> permitiria que qualquer pessoa se cadastrasse como `OWNER`. O servidor
> sempre cria `CLIENT`; contas `OWNER` vêm do seed de inicialização ou de
> promoção direta no banco.

**Response (201):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "Luiz Otávio",
  "email": "luiz@email.com",
  "role": "CLIENT",
  "createdAt": "2026-04-10T12:00:00"
}
```

### `POST /api/auth/login`
Autentica o usuário e retorna o token JWT.

**Request:**
```json
{
  "email": "luiz@email.com",
  "password": "senhaSegura123"
}
```

**Response (200):**
```json
{
  "accessToken": "eyJhbGciOi...",
  "type": "Bearer"
}
```

---

## 🔄 Fluxo de Segurança

```
1. POST /api/auth/register → Senha criptografada com BCrypt → Usuário salvo no banco
2. POST /api/auth/login → AuthenticationManager valida credenciais → JWT gerado (HS512) com roles
3. Request com token → JwtAuthenticationFilter intercepta → Valida token → Carrega UserDetails
4. SecurityContext preenchido → @PreAuthorize verifica role → Acesso concedido ou 403
5. Sem token → JwtAuthEntryPoint retorna 401 com JSON estruturado
```

---

## 🔧 Como Reutilizar em Outro Projeto

Esta é a proposta central do projeto. Para adaptar para sua aplicação:

1. **Altere o enum `Role`** — substitua `CLIENT`/`OWNER` pelas roles do seu domínio (ex: `USER`, `ADMIN`, `MODERATOR`)
2. **Adapte o `SecurityConfig`** — configure quais rotas são públicas e quais exigem quais roles
3. **Adicione seus Controllers** — qualquer novo endpoint já estará protegido pelo filtro JWT
4. **Customize o `AuthResponseDto`** — adicione campos extras se necessário (nome, role, etc.)
5. **Configure o `application.properties`** — ajuste a secret key, expiração e dados do banco

---

## ⚙️ Como Executar

### Pré-requisitos
- Java 21+
- Maven 3.9+
- Docker e Docker Compose

### 1. Subir o banco de dados
```bash
docker compose up -d
```

### 2. Executar a aplicação
```bash
./mvnw spring-boot:run
```

### 3. Acessar o Swagger UI
```
http://localhost:8081/swagger-ui/index.html
```

### 4. Executar os testes de integração
```bash
./mvnw test
```

---

## 🛠️ Melhorias Futuras

- [ ] Endpoint de refresh token (infraestrutura já implementada no `JwtUtils`)
- [ ] Rate limiting para proteção contra brute force
- [ ] Endpoint para alterar senha
- [ ] Soft delete de usuários (campo `isActive` já existe na entidade)
- [ ] CI/CD com GitHub Actions para rodar testes automaticamente

---

## 👨‍💻 Autor

Desenvolvido por **Luiz Otávio** como módulo reutilizável de autenticação para portfólio.
