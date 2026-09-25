package com.kfokam.kos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Sécurité de base, prête à recevoir l'authentification JWT.
 *
 * EN ATTENTE DU SUJET D'ÉPREUVE : toutes les routes sont ouvertes (permitAll).
 * Quand l'auth sera décidée, il faudra :
 *   1. ajouter JwtAuthenticationFilter + AuthenticationProvider,
 *   2. remplacer anyRequest().permitAll() par anyRequest().authenticated()
 *      (+ .hasRole("ADMIN") sur les routes d'administration),
 *   3. renvoyer 401/403 au format {@code ApiError} via AuthenticationEntryPoint.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API REST : pas de session ni de cookie, on utilisera des tokens
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Pas de formulaire de login ni de cookie : le CSRF classique est inutile
                .csrf(csrf -> csrf.disable())
                // CORS délégué : Spring Security cherche le bean corsConfigurationSource (CorsConfig)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Documentation + santé de l'application toujours accessibles
                        .requestMatchers(
                                "/",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/health")
                        .permitAll()
                        // TODO(épreuve) : passer en .authenticated() dès que l'auth est connue
                        .anyRequest().permitAll())
                // TODO(épreuve) : .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(b -> b.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }
}
