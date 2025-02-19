package com.app.octo.security;

import com.app.octo.model.response.UserResponse;
import com.app.octo.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class UserAuthProvider {
  public static final int VALIDITY = 3600000;

  @Value("${security.jwt.token.secret-key:secret-key}")
  private String secretKey;

  private final UserService userService;

  @PostConstruct
  protected void initialise() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  public String generateToken(String login, String role) {
    Date now = new Date();
    Date validUntil = new Date(now.getTime() + VALIDITY);

    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    String[] roles = new String[]{role};

    return JWT.create().withIssuer(login).withIssuedAt(now)
        .withExpiresAt(validUntil)
        .withArrayClaim("roles", roles)
        .sign(algorithm);
  }

  public Authentication validateToken(String token) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);

    JWTVerifier jwtVerifier = JWT.require(algorithm).build();

    DecodedJWT decodedJWT = jwtVerifier.verify(token); //Decoded to check validity

    UserResponse user = userService.findByEmail(decodedJWT.getIssuer());
    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
    Collection<SimpleGrantedAuthority> authorityCollections = new ArrayList<>();
    Arrays.stream(roles).forEach(role ->
      authorityCollections.add(new SimpleGrantedAuthority(role))
    );

    return new UsernamePasswordAuthenticationToken(user, null, authorityCollections);
  }
}
