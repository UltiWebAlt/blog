package org.jhipster.blog.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.jhipster.blog.security.*;
import org.jhipster.blog.web.filter.SpaWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final Environment env;

    private final JHipsterProperties jHipsterProperties;

    public SecurityConfiguration(Environment env, JHipsterProperties jHipsterProperties) {
        this.env = env;
        this.jHipsterProperties = jHipsterProperties;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, PathPatternRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(jHipsterProperties.getSecurity().getContentSecurityPolicy()))
                    .frameOptions(FrameOptionsConfig::sameOrigin)
                    .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicy(permissions ->
                        permissions.policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                        )
                    )
            )
            .authorizeHttpRequests(authz ->
                // prettier-ignore
                authz
                    .requestMatchers(mvc.matcher("/index.html"), mvc.matcher("/*.js"), mvc.matcher("/*.txt"), mvc.matcher("/*.json"), mvc.matcher("/*.map"), mvc.matcher("/*.css")).permitAll()
                    .requestMatchers(mvc.matcher("/*.ico"), mvc.matcher("/*.png"), mvc.matcher("/*.svg"), mvc.matcher("/*.webapp")).permitAll()
                    .requestMatchers(mvc.matcher("/app/**")).permitAll()
                    .requestMatchers(mvc.matcher("/i18n/**")).permitAll()
                    .requestMatchers(mvc.matcher("/content/**")).permitAll()
                    .requestMatchers(mvc.matcher("/swagger-ui/**")).permitAll()
                    .requestMatchers(mvc.matcher(HttpMethod.POST, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.matcher(HttpMethod.GET, "/api/authenticate")).permitAll()
                    .requestMatchers(mvc.matcher("/api/register")).permitAll()
                    .requestMatchers(mvc.matcher("/api/activate")).permitAll()
                    .requestMatchers(mvc.matcher("/api/account/reset-password/init")).permitAll()
                    .requestMatchers(mvc.matcher("/api/account/reset-password/finish")).permitAll()
                    .requestMatchers(mvc.matcher("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.matcher("/api/**")).authenticated()
                    .requestMatchers(mvc.matcher("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.matcher("/management/health")).permitAll()
                    .requestMatchers(mvc.matcher("/management/health/**")).permitAll()
                    .requestMatchers(mvc.matcher("/management/info")).permitAll()
                    .requestMatchers(mvc.matcher("/management/prometheus")).permitAll()
                    .requestMatchers(mvc.matcher("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions ->
                exceptions
                    .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                    .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
            http.authorizeHttpRequests(authz -> authz.requestMatchers(mvc.matcher("/h2-console/**")).permitAll());
        }
        return http.build();
    }

    @Bean
    PathPatternRequestMatcher.Builder mvc() {
        return PathPatternRequestMatcher.withDefaults();
    }
}
