package com.barbershop.barbershop_backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.barbershop.barbershop_backend.controllers.TestController;

/**
 * Sobe a aplicação num perfil que não é dev nem test, reaproveitando só o
 * banco H2 da configuração de testes, e garante que as rotas de demonstração
 * de RBAC não existem nele.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("prod")
@TestPropertySource(locations = "classpath:application-test.properties")
public class TestControllerProfileTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testControllerShouldNotBeLoadedOutsideDevAndTestProfiles() {
        assertThat(context.getBeansOfType(TestController.class)).isEmpty();
    }

    @Test
    @WithMockUser(roles = "OWNER")
    void testRoutesShouldNotExistOutsideDevAndTestProfiles() throws Exception {
        mockMvc.perform(get("/api/test/owner-only"))
                .andExpect(status().isNotFound());
    }
}
