package com.example.carforum.services.contracts;

import com.example.carforum.controllers.authentication.AuthenticationRequest;
import com.example.carforum.controllers.authentication.AuthenticationResponse;
import com.example.carforum.controllers.authentication.RegisterRequest;
import com.example.carforum.models.User;
import com.example.carforum.models.dtos.UserOutput;
import com.example.carforum.models.dtos.UserUpdate;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    List<UserOutput> getAll();

    Page<User> getUsersWithFilters(String username, String firstName, String email, int page, int size, String sortBy, String sortDirection);

    UserOutput getById(int id);

    UserOutput getByUsername(String username);

    UserOutput getByEmail(String email);

    public AuthenticationResponse register(RegisterRequest registerRequest);

    public AuthenticationResponse authenticate(AuthenticationRequest request);

    UserOutput edit(String currentUsername, String usernameWhoseUserEdit, UserUpdate userUpdate);

    void deactivateUser(int userId, String currentUsername);

    void userToBeModerator(String loggedUserUsername, int userToModeratorId);

    void moderatorToBeUser(String loggedUserUsername, int moderatorToUserId);

    void blockUser(String loggedUsername, int id);

    void unblockUser(String loggedUsername, int id);
}
