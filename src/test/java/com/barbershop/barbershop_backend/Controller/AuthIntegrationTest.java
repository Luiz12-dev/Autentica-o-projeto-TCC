package com.barbershop.barbershop_backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.barbershop.barbershop_backend.Enum.Role;
import com.barbershop.barbershop_backend.dto.LoginRequestDto;
import com.barbershop.barbershop_backend.dto.RegisterRequestDto;
import com.barbershop.barbershop_backend.models.User;
import com.barbershop.barbershop_backend.repositorys.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void before() {

        userRepository.deleteAll();
    }

    private String performLogin(String email, String password) throws Exception {

        LoginRequestDto login = new LoginRequestDto(email, password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())

                .andReturn();

        return result.getResponse().getContentAsString();

    }

    private String generateUniqueEmail(String prefix) {

        return prefix + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    }

    private void registerUser(String email, String password, Role role) throws Exception {

        RegisterRequestDto req = new RegisterRequestDto("Luiz", email, password);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))

                .andExpect(status().isCreated());

        // O registro publico so cria CLIENT. Para os cenarios de OWNER, promovemos
        // pelo repositorio — que e exatamente o caminho previsto em producao.
        if (role == Role.OWNER) {
            User user = userRepository.findByEmail(email).orElseThrow();
            user.setRole(Role.OWNER);
            userRepository.save(user);
        }
    }

    private String extractAccessToken(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        return root.get("accessToken").asText();

    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {

        String email = "luiz@teste.com";
        String password = "Luiz123";

        registerUser(email, password, Role.CLIENT);

        performLogin(email, password);
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        String emailEx = "Luiz@teste.com";
        String name = "Luiz";
        Role role = Role.CLIENT;

        RegisterRequestDto requestDto = new RegisterRequestDto(name, emailEx, "SuperSecret");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                // username carrega o nome real (fullName), nao o e-mail.
                .andExpect(jsonPath("$.username").value(name))
                .andExpect(jsonPath("$.email").value(emailEx))
                .andExpect(jsonPath("$.role").value(role.name()))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.password").doesNotExist());

    }

    @Test
    void shouldFailedLoginWithIncorrectPassword() throws Exception {

        String email = "fail_Test@teste.com";

        registerUser(email, "senhaIncorreta123", Role.CLIENT);

        LoginRequestDto login = new LoginRequestDto(email, "senhaIncorreta99");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void OwnerRoleShouldAccessRoute() throws Exception {
        String uniqueEmail = generateUniqueEmail("owner");
        String password = "ownerPass123";

        registerUser(uniqueEmail, password, Role.OWNER);

        String loginResponseJson = performLogin(uniqueEmail, password);

        String accessToken = extractAccessToken(loginResponseJson);

        mockMvc.perform(get("/api/test/protected")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void clientRoleShouldAccessRoute() throws Exception {
        String uniqueEmail = generateUniqueEmail("cliente_route");
        String password = "ClientePass123";

        registerUser(uniqueEmail, password, Role.CLIENT);

        String loginResponseJson = performLogin(uniqueEmail, password);

        String accessToken = extractAccessToken(loginResponseJson);

        mockMvc.perform(get("/api/test/protected")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void ownerShouldAccessOwnerOnlyRoute() throws Exception {
        String uniqueEmail = generateUniqueEmail("Luiz_Owner");
        String password = "OwnerPassword";

        registerUser(uniqueEmail, password, Role.OWNER);

        String loginJsonResponse = performLogin(uniqueEmail, password);

        String accessToken = extractAccessToken(loginJsonResponse);

        mockMvc.perform(get("/api/test/owner-only")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void clientShouldBeForbiddenOwnerRout() throws Exception {
        String email = generateUniqueEmail("Cliente");
        String password = "client123";

        registerUser(email, password, Role.CLIENT);

        String accessToken = extractAccessToken(performLogin(email, password));

        mockMvc.perform(get("/api/test/owner-only")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    void clientShouldAccesClientRoute() throws Exception {
        String email = generateUniqueEmail("Client");
        String password = "CLient123";

        registerUser(email, password, Role.CLIENT);

        String accessToken = extractAccessToken(performLogin(email, password));

        mockMvc.perform(get("/api/test/client-only")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void ownerShouldBeForbiddenClientRout() throws Exception {
        String email = generateUniqueEmail("owner");
        String password = "Owner123";

        registerUser(email, password, Role.OWNER);

        String accessToken = extractAccessToken(performLogin(email, password));

        mockMvc.perform(get("/api/test/client-only")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    void unauthenticatedUserShouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/test/protected")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerShouldAlwaysCreateClientEvenIfOwnerIsRequested() throws Exception {
        // JSON cru com role=OWNER, simulando quem ignora o formulario e chama a API direto.
        String maliciousPayload = """
                {
                  "username": "Atacante",
                  "email": "atacante@email.com",
                  "password": "123456",
                  "role": "OWNER"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(maliciousPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("CLIENT"));

        assertThat(userRepository.findByEmail("atacante@email.com").orElseThrow().getRole())
                .isEqualTo(Role.CLIENT);
    }

    @Test
    void registerShouldReturnRealNameAndCreationDate() throws Exception {
        RegisterRequestDto req = new RegisterRequestDto("Luiz Otávio", "luiz.nome@email.com", "123456");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("Luiz Otávio"))
                .andExpect(jsonPath("$.email").value("luiz.nome@email.com"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }
}
