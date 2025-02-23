package ma.hahn.tickets.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/tickets/**").hasAnyRole("EMPLOYEE", "IT_SUPPORT")
//                        .requestMatchers(HttpMethod.PUT, "/api/tickets/*/status").hasRole("IT_SUPPORT")
//                        .requestMatchers(HttpMethod.POST, "/api/tickets/*/comments").hasRole("IT_SUPPORT")
//                        .anyRequest().permitAll() // Allow other requests
//                )
                .csrf(csrf -> csrf.disable()) // Disable CSRF if using POST/PUT
                .formLogin(login -> login.disable()) // Disable login form
                .httpBasic(basic -> basic.disable()); // Disable basic auth
        return http.build();
    }
}
