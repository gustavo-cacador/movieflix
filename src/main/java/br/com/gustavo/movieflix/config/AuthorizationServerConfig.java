package br.com.gustavo.movieflix.config;

import br.com.gustavo.movieflix.config.customgrant.CustomPasswordAuthenticationConverter;
import br.com.gustavo.movieflix.config.customgrant.CustomPasswordAuthenticationProvider;
import br.com.gustavo.movieflix.config.customgrant.CustomUserAuthorities;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.*;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Value("${security.client-id}")
    private String clientId;

    @Value("${security.client-secret}")
    private String clientSecret;

    @Value("${security.jwt.duration}")
    private Integer jwtDurationSeconds;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    @Order(2)
    public SecurityFilterChain asSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {

        HttpSecurity http = httpSecurity.securityMatcher("/**");

        http.with(OAuth2AuthorizationServerConfigurer.authorizationServer(), Customizer.withDefaults());

        // @formatter:off
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint(tokenEndpoint -> tokenEndpoint
                        .accessTokenRequestConverter(new CustomPasswordAuthenticationConverter())
                        .authenticationProvider(new CustomPasswordAuthenticationProvider(authorizationService(), tokenGenerator(), userDetailsService, passwordEncoder)));

        http.oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));
        // @formatter:on

        return http.build();
    }

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new InMemoryOAuth2AuthorizationService();
    }

    @Bean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService() {
        return new InMemoryOAuth2AuthorizationConsentService();
    }

    // registra o nosso client
    // configura que o client esteja autorizado para acessar a aplicação
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        // @formatter:off
        RegisteredClient registeredClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(clientSecret))
                .scope("read")
                .scope("write")
                .authorizationGrantType(new AuthorizationGrantType("password"))
                .tokenSettings(tokenSettings())
                .clientSettings(clientSettings())
                .build();
        // @formatter:on

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public TokenSettings tokenSettings() {
        // @formatter:off
        return TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .accessTokenTimeToLive(Duration.ofSeconds(jwtDurationSeconds)) // duração do token (1dia) através do resources
                .build();
        // @formatter:on
    }

    // serve para instanciar o client
    // configurações da aplicação client
    @Bean
    public ClientSettings clientSettings() {
        return ClientSettings.builder().build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    // faz algumas configurações de token
    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator() {
        NimbusJwtEncoder jwtEncoder = new NimbusJwtEncoder(jwkSource());
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtGenerator.setJwtCustomizer(tokenCustomizer());
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator);
    }

    // customiza o token
    // claim = reinvidicação
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            OAuth2ClientAuthenticationToken principal = context.getPrincipal();
            CustomUserAuthorities user = (CustomUserAuthorities) principal.getDetails();
            List<String> authorities = user.getAuthorities().stream().map(x -> x.getAuthority()).toList();
            if (context.getTokenType().getValue().equals("access_token")) {
                // @formatter:off
                context.getClaims()
                        // .claim("nome", "Gustavo") caso queira customizar, podemos
                        .claim("authorities", authorities)
                        .claim("username", user.getUsername());
                // @formatter:on
            }
        };
    }

    // decodificador do token
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    // faz a configuração do algoritmo do RSA para gerar a chave para embaralhar junto com o token, fazendo com que o token seja seguro
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    private static RSAKey generateRsa() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }
}
