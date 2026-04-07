package br.com.links_vault_back_springboot.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class JWTService {

    @Value("${jwt-token-secret}")
    private String jwtTokenSecret;

    @Value("${jwt-token-issuer}")
    private String jwtTokenIssuer;

    public String generateToken(UUID subject) {
        Algorithm algorithm = Algorithm.HMAC256(jwtTokenSecret);

        return JWT
                .create()
                .withIssuer(jwtTokenIssuer)
                .withSubject(subject.toString())
                .withExpiresAt(Instant.now().plus(Duration.ofHours(2)))
                .sign(algorithm);
    }

}
