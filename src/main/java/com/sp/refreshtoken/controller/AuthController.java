package com.sp.refreshtoken.controller;

import com.sp.refreshtoken.entity.app.Role;
import com.sp.refreshtoken.entity.app.User;
import com.sp.refreshtoken.entity.enums.ERole;
import com.sp.refreshtoken.payload.request.SigninRequest;
import com.sp.refreshtoken.payload.response.JwtResponse;
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

    @PostMapping("hello-world2")
    public ResponseEntity<?> helloWorld(){
        return ResponseEntity.ok("Hello World");
    }

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

//        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUsername(),
                roles
//                userDetails.getPhoneNumber(),
//                userDetails.getSalary(),
//                userDetails.getAddress(),
//                userDetails.getRefCode(),
//                userDetails.getMemberType(),
//                refreshToken.getToken(),
//                roles
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

        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        roles.add(userRole);
        user.setRoles(roles);

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully.","success",user));
    }

}
