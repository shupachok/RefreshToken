package com.sp.refreshtoken.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  Jwt<?,?> jwtObject;

//  @Value("${app.jwtSecret}")
//  private String jwtSecret;

  @Value("${token.expiration_time}")
  private int jwtExpirationMs;

  MacAlgorithm SECRET_KEY = Jwts.SIG.HS256;

  private SecretKey getSignInKey() {
    return SECRET_KEY.key().build();
  }

  public String getJwtSubject(String token) {
    return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload().getSubject();
  }

  private Claims extractAllClaims(String token){
    return Jwts.parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

//  public String generateToken(UserDetails userDetails) {
//
//    return Jwts.builder()
//            .subject(userDetails.getUsername())
//            .issuedAt(new Date(System.currentTimeMillis()))
//            .expiration(new Date(System.currentTimeMillis() +    jwtExpirationMs))
//            .signWith(getSignInKey())
//            .compact();
//  }

  public String generateToken(String username) {

    return Jwts.builder()
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() +    jwtExpirationMs))
            .signWith(getSignInKey())
            .compact();
  }

  public Collection<? extends GrantedAuthority> getUserAuthorities(String token){

    Collection<Map<String,String>> scopes = extractAllClaims(token).get("scope", List.class);

    return scopes.stream()
            .map(scopeMap -> new SimpleGrantedAuthority(scopeMap.get("authority")))
            .collect(Collectors.toList());
  }

  public boolean validateJwtToken(String authToken) {
    try {
      extractAllClaims(authToken);
      return true;
    } catch (SignatureException e) {
      log.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      log.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false;
  }

}