package com.sp.refreshtoken.controller;

import com.sp.refreshtoken.entity.app.Role;
import com.sp.refreshtoken.entity.app.User;
import com.sp.refreshtoken.entity.enums.ERole;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.sp.refreshtoken.payload.request.SignupRequest;
import com.sp.refreshtoken.payload.response.MessageResponse;
import com.sp.refreshtoken.repository.RoleRepository;
import com.sp.refreshtoken.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

@RestController
public class AuthController {

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @GetMapping("hello-world")
    public ResponseEntity<?> helloWorld(){
        return ResponseEntity.ok("Hello World");
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
