package com.waisl.keycloak.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import static org.springframework.security.config.Customizer.withDefaults;

@Configurable
//@EnableWebSecurity
@KeycloakConfiguration
//@EnableGlobalMethodSecurity(jsr250Enabled = true)
@EnableMethodSecurity
@Import(KeycloakSpringBootConfigResolver.class)
public class SecurityConfig  {
    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */

    String keycloakClientId="springboot-keycloak-main";
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        KeycloakAuthenticationProvider authenticationProvider = new KeycloakAuthenticationProvider();
//        authenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
//        auth.authenticationProvider(authenticationProvider);
//    }
    @Bean
    public KeycloakAuthenticationProvider authenticationProvider() {
        return new KeycloakAuthenticationProvider();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public KeycloakSpringBootConfigResolver keycloakSpringBootConfigResolver(){
        return  new KeycloakSpringBootConfigResolver();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //super.configure(http);
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/employees/getToken").permitAll()
                        .anyRequest().authenticated()
                ) .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter(keycloakClientId))
                        )
                );
               // .httpBasic(withDefaults());
//        http.authorizeHttpRequests(auth->auth
//                .requestMatchers("/employees/all").hasRole("admin")
//                .requestMatchers("/employees/*").hasRole("user")
//                .requestMatchers("/employees/1/getToken").permitAll()
//                .anyRequest().permitAll()).httpBasic(withDefaults());
        return http.build();
    }
    String jwkSetUri = "http://127.0.0.1:8080/realms/waisl/protocol/openid-connect/certs";
    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }


}
