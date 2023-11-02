package com.myBlogNest.controller;


import com.myBlogNest.entity.Role;
import com.myBlogNest.entity.User;
import com.myBlogNest.payload.SignUpDto;
import com.myBlogNest.repository.RoleRepository;
import com.myBlogNest.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;


    //   http://localhost:8080/api/auth/signup
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/signup")
    public ResponseEntity<?> CreateUser(@RequestBody SignUpDto signDto) {


        // Now before creating a user, check whether the username or email already exists,
        // if they exist, return a response with an error message, otherwise, create a user

        if (userRepository.existsByUsername(signDto.getUsername())) {
            return new ResponseEntity<>("User Already Exist", HttpStatus.BAD_REQUEST);

        }
        if (userRepository.existsByEmail(signDto.getEmail())) {
            return new ResponseEntity<>("Email Already Exist", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(signDto.getName());
        user.setEmail(signDto.getEmail());
        user.setUsername((signDto.getUsername()));
        user.setPassword(passwordEncoder.encode(signDto.getPassword()));

//        Role roles = roleRepository.findByName("ROLE_ADMIN").get();
        Role roles = roleRepository.findByName("ROLE_USER").get();

        Set<Role> role = new HashSet<>();
        role.add(roles);
        user.setRoles(role);

        User saveUser = userRepository.save(user);

        SignUpDto signupDto1 = new SignUpDto();
        signupDto1.setName(saveUser.getName());
        signupDto1.setEmail(saveUser.getEmail());
        signupDto1.setUsername(saveUser.getUsername());

        return new ResponseEntity<>(signupDto1, HttpStatus.CREATED);


    }
}