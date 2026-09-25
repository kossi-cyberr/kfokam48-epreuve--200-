package com.kfokam.kos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.kfokam.kos.dto.PresenceResponse;
import com.kfokam.kos.exception.ApiBusinessException;
import com.kfokam.kos.model.Presence;
import com.kfokam.kos.model.Session;
import com.kfokam.kos.repository.PresenceRepository;
import com.kfokam.kos.repository.SessionRepository;
import com.kfokam.kos.repository.StudentRepository;
import com.kfokam.kos.service.impl.PresenceServiceImpl;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test unitaire B6 : il prouve des règles métier réelles, sans base ni Docker.
 * - RG1 / Q2 : un code expiré est refusé en 410 CODE_EXPIRE
 * - RG6 / Q3 : un étudiant déjà présent est refusé en 409 DEJA_PRESENT
 * - Q4 : au 5e code erroné, l'étudiant est bloqué 2 minutes
 */
class PresenceServiceImplTest {

    private PresenceServiceImpl service;
    private PresenceRepository presenceRepository;
    private SessionRepository sessionRepository;
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp() {
        presenceRepository = Mockito.mock(PresenceRepository.class);
        sessionRepository = Mockito.mock(SessionRepository.class);
        studentRepository = Mockito.mock(StudentRepository.class);
        service = new PresenceServiceImpl(presenceRepository, sessionRepository, studentRepository);
    }

    private Session session(String code, Instant expiration, boolean clotee) {
        return Session.builder()
                .id(1L)
                .titre("Test")
                .code(code)
                .ouvertureAt(expiration.minusSeconds(900))
                .expirationAt(expiration)
                .clotee(clotee)
                .promotionId(1L)
                .build();
    }

    @Test
    @DisplayName("RG1 : un code expiré renvoie CODE_EXPIRE avec le statut 410")
    void codeExpire() {
        Session expiree = session("ABCD1234", Instant.now().minusSeconds(60), false);
        when(sessionRepository.findByCode("ABCD1234")).thenReturn(Optional.of(expiree));
        when(studentRepository.existsById(2L)).thenReturn(true);

        ApiBusinessException e = new ApiBusinessException("probe", "probe", null);
        try {
            service.markPresence("ABCD1234", 2L);
        } catch (ApiBusinessException levee) {
            e = levee;
        }
        assertThat(e.getCode()).isEqualTo("CODE_EXPIRE");
        assertThat(e.getStatus().value()).isEqualTo(410);
    }

    @Test
    @DisplayName("RG6 : un étudiant déjà présent renvoie DEJA_PRESENT (409)")
    void dejaPresent() {
        Session valide = session("ABCD1234", Instant.now().plusSeconds(600), false);
        when(sessionRepository.findByCode("ABCD1234")).thenReturn(Optional.of(valide));
        when(studentRepository.existsById(2L)).thenReturn(true);
        when(presenceRepository.existsBySessionIdAndEtudiantId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> service.markPresence("ABCD1234", 2L))
                .isInstanceOf(ApiBusinessException.class)
                .extracting(ex -> ((ApiBusinessException) ex).getCode())
                .isEqualTo("DEJA_PRESENT");
    }

    @Test
    @DisplayName("Cas nominal : la présence est enregistrée avec source ETUDIANT")
    void casNominal() {
        Session valide = session("ABCD1234", Instant.now().plusSeconds(600), false);
        when(sessionRepository.findByCode("ABCD1234")).thenReturn(Optional.of(valide));
        when(studentRepository.existsById(2L)).thenReturn(true);
        when(presenceRepository.existsBySessionIdAndEtudiantId(1L, 2L)).thenReturn(false);
        when(presenceRepository.save(any())).thenReturn(
                Presence.builder().id(9L).sessionId(1L).etudiantId(2L).source("ETUDIANT").build());

        PresenceResponse response = service.markPresence("ABCD1234", 2L);
        assertThat(response.getSource()).isEqualTo("ETUDIANT");
        assertThat(response.getSessionId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Q4 : au 5e code erroné, l'étudiant est bloqué, même avec un code valide ensuite")
    void blocageApresCinqErreurs() {
        when(studentRepository.existsById(3L)).thenReturn(true);
        when(sessionRepository.findByCode(anyString())).thenReturn(Optional.empty());

        for (int i = 0; i < 5; i++) {
            try {
                service.markPresence("MAUVAIS", 3L);
            } catch (ApiBusinessException e) {
                // 400 CODE_INCONNU attendu à chaque essai
            }
        }
        // Le 6e essai, même avec un code VALIDE, l'étudiant est bloqué
        Session valide = session("ABCD1234", Instant.now().plusSeconds(600), false);
        when(sessionRepository.findByCode("ABCD1234")).thenReturn(Optional.of(valide));

        assertThatThrownBy(() -> service.markPresence("ABCD1234", 3L))
                .isInstanceOf(ApiBusinessException.class)
                .extracting(ex -> ((ApiBusinessException) ex).getCode())
                .isEqualTo("ETUDIANT_BLOQUE");
    }
}
