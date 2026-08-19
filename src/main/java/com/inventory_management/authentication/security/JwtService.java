package com.inventory_management.authentication.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final byte[] secretBytes;
    private final long expirationMs;
    private final String issuer;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMs,
            @Value("${jwt.issuer}") String issuer) {
        this.secretBytes = secret.getBytes();
        this.expirationMs = expirationMs;
        this.issuer = issuer;
    }

    public String generateToken(String subject, String role) {
        try {
            JWSSigner signer = new MACSigner(secretBytes);

            Instant now = Instant.now();
            Instant expiry = now.plusMillis(expirationMs);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issuer(issuer)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(expiry))
                    .claim("role", role)
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claims
            );
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to generate JWT token", e);
        }
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }
}
