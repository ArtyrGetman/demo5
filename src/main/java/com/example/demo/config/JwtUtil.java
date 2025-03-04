package com.example.demo.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {


    private static final String SECRET_KEY = "U29tZXN1cGVyc2VjcmV0a2V5Zm9ySlhUVG9rZW5zMTIzNDU2"; // Base64 key

    public  String generateToken(String userName){
        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+36000000))
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY)
                .compact();
    }

    public String extractUserName(String token){
        return  getClaims(token).getSubject();
    }
    public  boolean validateToken(String token, String userName){
        return extractUserName(token).equals(userName) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        return  getClaims(token).getExpiration().before(new Date());
    }


    private Claims getClaims(String token){
        return  Jwts.parser().setSigningKey(SECRET_KEY).build().parseSignedClaims(token).getPayload();
    }
}
