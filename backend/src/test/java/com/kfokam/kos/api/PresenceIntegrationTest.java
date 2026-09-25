package com.kfokam.kos.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kfokam.kos.model.Session;
import com.kfokam.kos.repository.PresenceRepository;
import com.kfokam.kos.repository.SessionRepository;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Test d'intégration B6 : POST /api/presences de bout en bout
 * (contrôleur -> service -> vraie PostgreSQL via Testcontainers, migrations Flyway jouées).
 * Ne nécessite qu'un moteur Docker, aucune base locale.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class PresenceIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    private Session creerSession(String code, Instant expiration) {
        return sessionRepository.save(Session.builder()
                .titre("Session de test")
                .code(code)
                .ouvertureAt(expiration.minusSeconds(900))
                .expirationAt(expiration)
                .clotee(false)
                .promotionId(1L)
                .build());
    }

    @Test
    @DisplayName("Cas nominal : 201 avec id, sessionId, etudiantId, source")
    void presenceCreee() throws Exception {
        creerSession("TEST0001", Instant.now().plusSeconds(600));

        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"TEST0001\",\"etudiantId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.sessionId").exists())
                .andExpect(jsonPath("$.etudiantId").value(1))
                .andExpect(jsonPath("$.source").value("ETUDIANT"));
    }

    @Test
    @DisplayName("Code inconnu : 400 { code: CODE_INCONNU }")
    void codeInconnu() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"INCONNU0\",\"etudiantId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CODE_INCONNU"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Code expiré : 410 { code: CODE_EXPIRE }")
    void codeExpire() throws Exception {
        creerSession("TEST0002", Instant.now().minusSeconds(60));

        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"TEST0002\",\"etudiantId\":1}"))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.code").value("CODE_EXPIRE"));
    }

    @Test
    @DisplayName("Déjà présent : 409 { code: DEJA_PRESENT }")
    void dejaPresent() throws Exception {
        Session session = creerSession("TEST0003", Instant.now().plusSeconds(600));
        presenceRepository.save(com.kfokam.kos.model.Presence.builder()
                .sessionId(session.getId()).etudiantId(1L).source("ETUDIANT").build());

        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":\"TEST0003\",\"etudiantId\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DEJA_PRESENT"));
    }
}
