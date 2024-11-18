package com.example.carforum.controllers;

import com.example.carforum.config.JwtService;
import com.example.carforum.models.User;
import com.example.carforum.models.dtos.UserOutput;
import com.example.carforum.models.dtos.UserUpdate;
import com.example.carforum.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping
    public List<UserOutput> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserOutput> getUserById(@PathVariable int id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/username")
    public ResponseEntity<UserOutput> getUserByUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.getByUsername(username));
    }

    @GetMapping("/email")
    public ResponseEntity<UserOutput> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getByEmail(email));
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserOutput> editUser(@Valid @PathVariable String username, @RequestBody UserUpdate userUpdate) {
        String loggedUserUsername = jwtService.getCurrentUsername();
        return ResponseEntity.ok(userService.edit(loggedUserUsername, username, userUpdate));
    }


}
