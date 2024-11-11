package com.example.carforum.services;

import com.example.carforum.config.JwtService;
import com.example.carforum.controllers.AuthenticationRequest;
import com.example.carforum.controllers.AuthenticationResponse;
import com.example.carforum.controllers.RegisterRequest;
import com.example.carforum.exceptions.DuplicateEntityException;
import com.example.carforum.exceptions.EntityNotFoundException;
import com.example.carforum.models.Role;
import com.example.carforum.models.User;
import com.example.carforum.repositories.UserRepository;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public Page<User> getUsersWithFilters(String username, String firstName, String email, int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        String usernameLike = username != null ? "%" + username + "%" : null;
        String firstNameLike = firstName != null ? "%" + firstName + "%" : null;
        String emailLike = email != null ? "%" + email + "%" : null;

        return userRepository.findUsersByMultipleFields(usernameLike, firstNameLike, emailLike, pageable);
    }

    @Override
    public User getById(int id){
        return userRepository.findByIdAndIsActiveTrue(id);
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsernameAndIsActiveTrue(username);
    }

    @PermitAll
    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email);
    }

    @Override
    public User createUser(User user) {
        boolean duplicate = true;
        try {
            userRepository.findByUsernameAndIsActiveTrue(user.getUsername());
        }catch (EntityNotFoundException e){
            duplicate = false;
        }

        if(duplicate){
            throw new DuplicateEntityException("User", "name", user.getUsername());
        }

        //user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .profilePhoto(null)
                .role(Role.USER)
                .isBlocked(false)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        var user = userRepository.findByUsernameAndIsActiveTrue(request.getUsername());

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
