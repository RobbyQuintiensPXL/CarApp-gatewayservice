package be.carshop.gatewayservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchange -> exchange
                                .pathMatchers("/actuator/**").permitAll()
                                .pathMatchers("/car/actuator/**").permitAll()
                                .pathMatchers(HttpMethod.GET, "/car/public/**").permitAll()
//                        .pathMatchers(HttpMethod.POST,"/event/locations/add_location").hasAuthority("SCOPE_jevents-office")
//                        .pathMatchers(HttpMethod.GET, "/ticket/tickets/validate/**").permitAll()
//                        .pathMatchers("/event/office/**").hasAuthority("SCOPE_jevents-office")
//                        .pathMatchers("/event/admin/**").hasAuthority("SCOPE_jevents-admin")
//                        .pathMatchers("/token").hasAuthority("SCOPE_jevents-office")
                                .anyExchange().authenticated()
                )
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder(OAuth2ResourceServerProperties properties, @Value("${auth.audience}") String audience) {
        String issuerUri = properties.getJwt().getIssuerUri();
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }
}
