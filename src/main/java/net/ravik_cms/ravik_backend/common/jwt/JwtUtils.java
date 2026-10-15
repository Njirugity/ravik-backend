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
import java.util.UUID;
@Component
public class JwtUtils {
    private static final String USER_ID_CLAIM = "userId";

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;
    private final SecretKey key;

    public JwtUtils (@Value("${jwt.secret}") String jwtSecret){
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    //Generate token
    public String generateToken(UUID userId, String userName){
        return Jwts.builder()
                .subject(userName)
                .claim(USER_ID_CLAIM, userId.toString())
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
    //Extract userId from Jwt token
    public UUID extractUserId(String token){
        String userId = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(USER_ID_CLAIM, String.class);
        return userId == null ? null : UUID.fromString(userId);
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

    public long getExpirationMs(){
        return jwtExpirationMs;
    }
}
