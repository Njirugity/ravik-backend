package net.ravik_cms.ravik_backend.common.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Component
public class JwtUtils {
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    private final SecretKey key;

    public JwtUtils (@Value("${jwt.secret}") String jwtSecret){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //Generate token
    public String generateToken(String userName){
        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExpirationMs)))
                .signWith(key)
                .compact();
    }
    //Extract userName from Jwt token
    public String extractUsername(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
    //Validate token
    public boolean validateToken(String token){
        try {
            extractUsername(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }
}
