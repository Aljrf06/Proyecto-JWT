package co.edu.login.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public String extraerUsuario(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    public <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extraerTodosLosClaims(token));
    }

    public String generarToken(UserDetails userDetails) {
        return generarToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    public String generarRefreshToken(UserDetails userDetails) {
        return generarToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String generarToken(Map<String, Object> extraClaims,
                                UserDetails userDetails, long expiracion) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiracion))
                .signWith(obtenerClave(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean esTokenValido(String token, UserDetails userDetails) {
        return extraerUsuario(token).equals(userDetails.getUsername())
                && !esTokenExpirado(token);
    }

    private boolean esTokenExpirado(String token) {
        return extraerClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extraerTodosLosClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(obtenerClave())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key obtenerClave() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}