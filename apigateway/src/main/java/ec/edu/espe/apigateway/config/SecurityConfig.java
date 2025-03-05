package ec.edu.espe.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // Permite todas las solicitudes OPTIONS para que se gestionen correctamente las preflight
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/candidatos/**", "/entrevistas/**", "/vacantes/**").authenticated()
                .anyExchange().permitAll()
            )
            .httpBasic(httpBasic -> {});
        return http.build();
    }

}