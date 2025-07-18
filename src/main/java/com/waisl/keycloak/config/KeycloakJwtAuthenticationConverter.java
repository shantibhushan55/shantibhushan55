package com.waisl.keycloak.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private final String resourceId; // Optional: if roles are client-specific

    public KeycloakJwtAuthenticationConverter(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                        jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                        extractResourceRoles(jwt).stream())
                .collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");

        Collection<String> realmRoles = (realmAccess != null && realmAccess.containsKey("roles")) ?
                (Collection<String>) realmAccess.get("roles") : Collections.emptyList();

        Collection<String> clientRoles = Collections.emptyList();
        if (resourceId != null && resourceAccess != null && resourceAccess.containsKey(resourceId)) {
            Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(resourceId);
            if (resource != null && resource.containsKey("roles")) {
                clientRoles = (Collection<String>) resource.get("roles");
            }
        }

        return Stream.concat(realmRoles.stream(), clientRoles.stream())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())) // Add "ROLE_" prefix if needed
                .collect(Collectors.toSet());
    }
}