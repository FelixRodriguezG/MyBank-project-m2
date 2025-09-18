package io.github.felix.bank_back.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret; // acepta base64 o texto plano

    @Value("${security.jwt.expiration-ms}")
    private long expirationMs; // 3600000 (=1 hora)

    private Key getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException ex) {
            // Si no es Base64, usar bytes del string directamente
            return Keys.hmacShaKeyFor(secret.getBytes());
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String username) {
        return generateToken(Map.of(), username);
    }

    public String generateToken(Map<String, Object> extraClaims, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        final String user = extractUsername(token);
        return (user.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}