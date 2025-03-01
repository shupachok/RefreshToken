package com.sp.refreshtoken.controller;

import com.sp.refreshtoken.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUser(){
        return ResponseEntity.ok().body(userRepository.findAll());
    }

    @GetMapping("/hello-world")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getHelloWorld(){
        return ResponseEntity.ok().body("Hello World");
    }
}
