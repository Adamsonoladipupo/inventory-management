package com.inventory_management.authentication.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("JwtService")
class JwtServiceTest {

    private static final String SECRET = "TestSecretKeyForJwtServiceTesting123!";
    private static final long EXPIRATION_MS = 3_600_000L;
    private static final String ISSUER = "inventory-management";
    private static final String SUBJECT = "john@example.com";
    private static final String ROLE = "ROLE_OWNER";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION_MS, ISSUER);
    }

    @Nested
    @DisplayName("generateToken()")
    class GenerateTokenTests {

        @Test
        @DisplayName("should return a non-null, non-blank token")
        void shouldReturnNonNullToken() {
            String token = jwtService.generateToken(SUBJECT, ROLE);
            assertThat(token).isNotNull().isNotBlank();
        }

        @Test
        @DisplayName("should return a compact JWT with three dot-separated parts")
        void shouldReturnThreePartJwt() {
            String token = jwtService.generateToken(SUBJECT, ROLE);
            assertThat(token.split("\\.")).hasSize(3);
        }

        @Test
        @DisplayName("should set the correct subject (email) in the token")
        void shouldSetCorrectSubject() throws Exception {
            String token = jwtService.generateToken(SUBJECT, ROLE);
            JWTClaimsSet claims = SignedJWT.parse(token).getJWTClaimsSet();
            assertThat(claims.getSubject()).isEqualTo(SUBJECT);
        }

        @Test
        @DisplayName("should set the role claim in the token")
        void shouldSetRoleClaim() throws Exception {
            String token = jwtService.generateToken(SUBJECT, ROLE);
            JWTClaimsSet claims = SignedJWT.parse(token).getJWTClaimsSet();
            assertThat(claims.getStringClaim("role")).isEqualTo(ROLE);
        }

        @Test
        @DisplayName("should set the correct issuer in the token")
        void shouldSetCorrectIssuer() throws Exception {
            String token = jwtService.generateToken(SUBJECT, ROLE);
            JWTClaimsSet claims = SignedJWT.parse(token).getJWTClaimsSet();
            assertThat(claims.getIssuer()).isEqualTo(ISSUER);
        }

        @Test
        @DisplayName("should set an expiration time in the future")
        void shouldSetFutureExpiration() throws Exception {
            String token = jwtService.generateToken(SUBJECT, ROLE);
            JWTClaimsSet claims = SignedJWT.parse(token).getJWTClaimsSet();
            assertThat(claims.getExpirationTime()).isAfter(new Date());
        }

        @Test
        @DisplayName("should set an issue time that is in the past or present")
        void shouldSetIssuedAtInPastOrPresent() throws Exception {
            String token = jwtService.generateToken(SUBJECT, ROLE);
            JWTClaimsSet claims = SignedJWT.parse(token).getJWTClaimsSet();
            Date after = new Date();
            assertThat(claims.getIssueTime()).isBeforeOrEqualTo(after);
        }

        @Test
        @DisplayName("should NOT include a password claim in the token")
        void shouldNotIncludePasswordClaim() throws Exception {
            String token = jwtService.generateToken(SUBJECT, ROLE);
            JWTClaimsSet claims = SignedJWT.parse(token).getJWTClaimsSet();
            assertThat(claims.getClaims()).doesNotContainKey("password");
        }

        @Test
        @DisplayName("should produce a token with a valid HMAC-SHA256 signature (verifiable by NimbusJwtDecoder)")
        void shouldProduceTokenVerifiableByNimbusDecoder() {
            String token = jwtService.generateToken(SUBJECT, ROLE);

            SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();

            assertDoesNotThrow(() -> decoder.decode(token));
        }

        @Test
        @DisplayName("should produce a token whose decoded subject matches the original email")
        void shouldProduceTokenWithCorrectSubjectViaDecoder() {
            String token = jwtService.generateToken(SUBJECT, ROLE);

            SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();

            var jwt = decoder.decode(token);
            assertThat(jwt.getSubject()).isEqualTo(SUBJECT);
        }
    }

    @Nested
    @DisplayName("getExpirationSeconds()")
    class GetExpirationSecondsTests {

        @Test
        @DisplayName("should return expiration in seconds (ms / 1000)")
        void shouldReturnExpirationInSeconds() {
            assertThat(jwtService.getExpirationSeconds()).isEqualTo(3600L);
        }

        @Test
        @DisplayName("should reflect a different expiration value if configured differently")
        void shouldReflectConfiguredExpiration() {
            JwtService shortLivedService = new JwtService(SECRET, 60_000L, ISSUER);
            assertThat(shortLivedService.getExpirationSeconds()).isEqualTo(60L);
        }
    }

    @Nested
    @DisplayName("Token signature validation")
    class SignatureValidationTests {

        @Test
        @DisplayName("should reject a token signed with a different secret")
        void shouldRejectTokenSignedWithDifferentSecret() {
            JwtService otherService = new JwtService("DifferentSecret!!DifferentSecret!!", EXPIRATION_MS, ISSUER);
            String tampered = otherService.generateToken(SUBJECT, ROLE);

            SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();

            assertThatThrownBy(() -> decoder.decode(tampered))
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("Token expiration")
    class TokenExpirationTests {

        @Test
        @DisplayName("should reject a token that has already expired")
        void shouldRejectExpiredToken() {
            JwtService expiredService = new JwtService(SECRET, -1000L, ISSUER);
            String expiredToken = expiredService.generateToken(SUBJECT, ROLE);

            SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(), "HmacSHA256");
            NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(secretKey)
                    .macAlgorithm(MacAlgorithm.HS256)
                    .build();

            assertThatThrownBy(() -> decoder.decode(expiredToken))
                    .isInstanceOf(Exception.class);
        }
    }
}
