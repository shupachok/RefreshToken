package com.sp.refreshtoken.controller;

import com.sp.refreshtoken.entity.app.RefreshToken;
import com.sp.refreshtoken.entity.app.Role;
import com.sp.refreshtoken.entity.app.User;
import com.sp.refreshtoken.entity.enums.ERole;
import com.sp.refreshtoken.exception.TokenRefreshException;
import com.sp.refreshtoken.payload.request.SigninRequest;
import com.sp.refreshtoken.payload.request.TokenRefreshRequest;
import com.sp.refreshtoken.payload.response.JwtResponse;
import com.sp.refreshtoken.payload.response.TokenRefreshResponse;
import com.sp.refreshtoken.security.service.RefreshTokenService;
import com.sp.refreshtoken.security.service.UserDetailsImpl;
import com.sp.refreshtoken.util.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.sp.refreshtoken.payload.request.SignupRequest;
import com.sp.refreshtoken.payload.response.MessageResponse;
import com.sp.refreshtoken.repository.RoleRepository;
import com.sp.refreshtoken.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    RefreshTokenService refreshTokenService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SigninRequest req) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                roles,
                refreshToken.getToken()
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username is already exist", "error", null));
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already in use!", "error", null));
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());

        Set<Role> roles = new HashSet<>();


        Optional<Role> userRole = roleRepository.findByName(ERole.ROLE_USER);

        if(userRole.isPresent()){
            roles.add(userRole.get());
            user.setRoles(roles);

            userRepository.save(user);
        } else{
            return ResponseEntity.badRequest().body(new MessageResponse("Role not found", "error", null));
        }

        return ResponseEntity.ok(new MessageResponse("User registered successfully.","success",user));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                            String token = jwtUtil.generateToken(user.getUsername());
                            return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                        }
                )
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,"Refresh token is not in database!"));
    }

}
