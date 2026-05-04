package com.example.auth_server.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    // -------------------------------------------------------------------------
    // Filter Chain 1: Authorization Server protocol endpoints
    //
    // This chain handles the OAuth2 protocol endpoints:
    //   /oauth2/authorize  -- the authorization endpoint (redirects to login)
    //   /oauth2/token      -- the token endpoint (issues JWTs)
    //   /oauth2/jwks       -- the public key endpoint (Resource Servers fetch from here)
    //   /.well-known/...   -- discovery endpoints
    //
    // @Order(1) ensures this chain is evaluated before the login form chain below.
    // -------------------------------------------------------------------------
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                // Enable OpenID Connect 1.0 -- adds the /userinfo endpoint
                // and id_token support alongside the standard access token.
                .oidc(Customizer.withDefaults());

        http
                // When an unauthenticated browser request arrives at the
                // authorization endpoint, redirect to the login form.
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                )
                // Accept Bearer tokens for the /userinfo endpoint.
                .oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()));

        return http.build();
    }

    // -------------------------------------------------------------------------
    // Filter Chain 2: Default security for the login form
    //
    // @Order(2) means this chain is checked after the Authorization Server chain.
    // It provides the login form that users see when authenticating interactively.
    // -------------------------------------------------------------------------
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    // -------------------------------------------------------------------------
    // Users
    //
    // These are the human users who can authenticate interactively
    // via the Authorization Code flow.
    //
    // In production these would come from a database or enterprise identity
    // provider. For this exercise, in-memory users are sufficient.
    //
    // alice -- HR_MANAGER role, can read and write employee data
    // bob   -- VIEWER role, read-only access
    // -------------------------------------------------------------------------
    @Bean
    public UserDetailsService userDetailsService() {
        var alice = User.withDefaultPasswordEncoder()
                .username("alice")
                .password("password")
                .roles("HR_MANAGER")
                .build();

        var bob = User.withDefaultPasswordEncoder()
                .username("bob")
                .password("password")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(alice, bob);
    }

    // -------------------------------------------------------------------------
    // Registered Clients
    //
    // A registered client is an application that has permission to request
    // tokens from this Authorization Server.
    //
    // workforce-spa: a public client using Authorization Code + PKCE.
    //   Represents a browser-based application where a secret cannot be stored.
    //   PKCE provides the security guarantee that a client secret would give.
    //
    // workforce-service: a confidential client using Client Credentials.
    //   Represents a backend service authenticating with its own identity.
    //   No user is involved -- the service authenticates directly.
    // -------------------------------------------------------------------------
    @Bean
    public RegisteredClientRepository registeredClientRepository() {

       RegisteredClient workforceSpa = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("workforce-spa")
                // Public clients have no secret. PKCE takes its place.
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:9000/authorized")
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/workforce-spa")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("read:employees")
                .scope("write:employees")
                .clientSettings(ClientSettings.builder()
                        // Require PKCE. The Authorization Server rejects any
                        // authorization request that does not include a code_challenge.
                        .requireProofKey(true)
                        // Show the consent screen so students can see scope approval.
                        .requireAuthorizationConsent(true)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        // 5-minute access token lifetime. Intentionally short so the
                        // stale permissions scenario in Exercise 1 is observable
                        // within a single lab session.
                        .accessTokenTimeToLive(Duration.ofMinutes(5000))
                        .refreshTokenTimeToLive(Duration.ofDays(1))
                        .build())
                .build();

        RegisteredClient workforceService = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("workforce-service")
                // {noop} tells Spring not to hash this value.
                // Development only -- never use {noop} in production.
                .clientSecret("{noop}workforce-service-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("read:employees")
                .tokenSettings(TokenSettings.builder()
                        // Service tokens can have longer lifetimes because the
                        // service can re-authenticate silently at any time.
                        .accessTokenTimeToLive(Duration.ofHours(3))
                        .build())
                .build();


        RegisteredClient bankClientRead = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("bank-client-read")
                .clientSecret("{noop}bank-client-read-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("read:accounts")
                .scope("read:transactions")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                        .build())
                .build();
        RegisteredClient bankClientBff = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("bank-client-bff")
                .clientSecret("{noop}bank-client-bff-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // Direct access (Lab 4.6 standalone testing)
                .redirectUri("http://localhost:8080/login/oauth2/code/bank-auth")
                // Through Vite proxy (Lab 4.7 React integration)
                .redirectUri("http://localhost:5173/login/oauth2/code/bank-auth")
                // Direct access post-logout (Lab 4.6)
                .postLogoutRedirectUri("http://localhost:8080/")
                // Through Vite proxy post-logout (Lab 4.7+)
                .postLogoutRedirectUri("http://localhost:5173/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("read:accounts")
                .scope("read:transactions")
                .scope("write:accounts")
                .scope("write:transactions")
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(60))
                        .refreshTokenTimeToLive(Duration.ofDays(1))
                        .build())
                .build();

        RegisteredClient bankClientFull = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("bank-client-full")
                .clientSecret("{noop}bank-client-full-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("read:accounts")
                .scope("read:transactions")
                .scope("write:accounts")
                .scope("write:transactions")
                .scope("admin:users")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                        .build())
                .build();

        RegisteredClient spaclientbff = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("spa-client")
                .clientSecret("{noop}spa-secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("read:accounts")
                .scope("read:transactions")
                .scope("write:accounts")
                .scope("write:transactions")
                .scope("admin:users")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(500))
                        .build())
                .build();


        return new InMemoryRegisteredClientRepository(
                workforceSpa,
                workforceService,
                bankClientRead,
                bankClientFull,
                bankClientBff,
                spaclientbff
        );
    }

    // -------------------------------------------------------------------------
    // Token Customizer
    //
    // Adds application-specific claims beyond the standard registered claims.
    // The Resource Server exercises depend on these two custom claims:
    //
    //   roles      -- the user's application roles
    //   department -- a custom claim simulating a user profile attribute
    //
    // The customizer only runs when a user is present (Authorization Code flow).
    // Service tokens (Client Credentials) have no user, so this condition
    // is false and no user-specific claims are added. This is why the service
    // token will not contain "roles" or "department" -- a difference the
    // exercises ask you to observe and explain directly.
    // -------------------------------------------------------------------------
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            var principal = context.getPrincipal();

            if (principal != null &&
                    principal.getPrincipal() instanceof
                            org.springframework.security.core.userdetails.UserDetails user) {

                // Strip the ROLE_ prefix Spring Security adds internally.
                // Stored as "HR_MANAGER" so the Resource Server can check
                // hasRole('HR_MANAGER') in @PreAuthorize expressions.
                List<String> roles = user.getAuthorities().stream()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .toList();
                context.getClaims().claim("roles", roles);

                // Simulate a department attribute from a user profile store.
                String department = "alice".equals(user.getUsername())
                        ? "Human Resources"
                        : "Engineering";
                context.getClaims().claim("department", department);
            }
        };
    }

    // -------------------------------------------------------------------------
    // RSA Key Pair
    //
    // The Authorization Server signs JWTs with the private key.
    // Resource Servers verify JWTs using the public key, fetched from:
    //   http://localhost:9000/oauth2/jwks
    //
    // A new key pair is generated in memory on each startup. Any token issued
    // before a restart is immediately invalid because the new public key will
    // not match the old signature. This is expected in development and is
    // something Exercise 1 asks you to observe directly.
    //
    // In production the key pair is generated once, stored securely, and
    // rotated deliberately with advance notice to all Resource Servers.
    // -------------------------------------------------------------------------
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                // The kid (Key ID) appears in every JWT header.
                // Resource Servers use it to select the correct verification
                // key from the JWKS when multiple keys are present.
                .keyID(UUID.randomUUID().toString())
                .build();

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            return generator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to generate RSA key pair", ex);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    // -------------------------------------------------------------------------
    // Authorization Server Settings
    //
    // The issuer URI is embedded in the "iss" claim of every token this
    // server issues. Resource Servers are configured with this URI and reject
    // any token where "iss" does not match. This prevents a token issued for
    // one system from being replayed against a different system.
    // -------------------------------------------------------------------------
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .issuer("http://localhost:9000")
                .build();
    }
}