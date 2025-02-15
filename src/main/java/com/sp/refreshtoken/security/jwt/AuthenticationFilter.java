package com.sp.refreshtoken.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.refreshtoken.payload.request.SigninRequest;
import com.sp.refreshtoken.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private Environment environment;

	private JwtUtils jwtUtils = new JwtUtils();

	public AuthenticationFilter(AuthenticationManager authenticationManager,
								Environment environment) {
		super(authenticationManager);
		this.environment = environment;
	}


	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
			throws AuthenticationException {
		try {

			SigninRequest creds = new ObjectMapper().readValue(req.getInputStream(), SigninRequest.class);

			return getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword(), new ArrayList<>()));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
			Authentication auth) throws IOException, ServletException {

		String username = ((User) auth.getPrincipal()).getUsername();
//		UserDetailEntity userDetailEntity = userService.getUserByEmail(username);
//		String tokenSecret = environment.getProperty("token.secret");
//		byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
//		SecretKey secretKey = new SecretKeySpec(secretKeyBytes, SignatureAlgorithm.HS512.getJcaName());
//
//		Instant now = Instant.now();
//		String token = Jwts.builder()
//				.setSubject(userDetailEntity.getId())
//				.claim("scope",auth.getAuthorities())
//				.setExpiration(
//						Date.from(now.plusMillis(Long.parseLong(environment.getProperty("token.expiration_time")))))
//				.setIssuedAt(Date.from(now))
//				.signWith(secretKey, SignatureAlgorithm.HS512).compact();

		String token = jwtUtils.generateToken(username);

		res.addHeader("token", token);
		res.addHeader("username", username);

	}
}