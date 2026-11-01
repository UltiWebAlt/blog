package org.jhipster.blog.config;

import static org.jhipster.blog.security.SecurityUtils.AUTHORITIES_KEY;
import static org.jhipster.blog.security.SecurityUtils.JWT_ALGORITHM;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import java.util.Locale;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.jhipster.blog.management.SecurityMetersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

@Configuration
public class SecurityJwtConfiguration {

    @Value("${jhipster.security.authentication.jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public JwtDecoder jwtDecoder(SecurityMetersService metersService) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (JwtException e) {
                String errorMessage = getErrorMessage(e);
                if (errorMessage.contains("invalid signature")) {
                    metersService.trackTokenInvalidSignature();
                } else if (errorMessage.contains("jwt expired at")) {
                    metersService.trackTokenExpired();
                } else if (errorMessage.contains("unsupported")) {
                    metersService.trackTokenUnsupported();
                } else if (errorMessage.contains("invalid jwt serialization") || errorMessage.contains("malformed token")) {
                    metersService.trackTokenMalformed();
                }
                throw e;
            }
        };
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_KEY);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }

    private String getErrorMessage(Throwable error) {
        StringBuilder message = new StringBuilder();
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause.getMessage() != null) {
                message.append(cause.getMessage()).append(' ');
            }
            if (cause.getCause() == cause) {
                break;
            }
        }
        return message.toString().toLowerCase(Locale.ROOT);
    }
}
