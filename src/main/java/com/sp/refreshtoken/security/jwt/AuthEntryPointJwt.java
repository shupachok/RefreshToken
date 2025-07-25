package com.sp.refreshtoken.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    @Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException authException) throws ServletException, IOException {
		log.error("Unauthorized error: {}", authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized error : " + authException.getMessage());
	}
}