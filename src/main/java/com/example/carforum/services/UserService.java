package com.example.carforum.services;

import com.example.carforum.controllers.AuthenticationRequest;
import com.example.carforum.controllers.AuthenticationResponse;
import com.example.carforum.controllers.RegisterRequest;
import com.example.carforum.models.User;
import org.springframework.data.domain.Page;

public interface UserService {

    Page<User> getUsersWithFilters(String username, String firstName, String email, int page, int size, String sortBy, String sortDirection);

    User getById(int id);

    Object getByUsername(String username);

    User getByEmail(String email);

    User createUser(User user);

    public AuthenticationResponse register(RegisterRequest registerRequest);

    public AuthenticationResponse authenticate(AuthenticationRequest request);
}
