package com.example.carforum.services;

import com.example.carforum.config.JwtService;
import com.example.carforum.controllers.authentication.AuthenticationRequest;
import com.example.carforum.controllers.authentication.AuthenticationResponse;
import com.example.carforum.controllers.authentication.RegisterRequest;
import com.example.carforum.exceptions.EntityNotFoundException;
import com.example.carforum.exceptions.UnauthorizedOperationException;
import com.example.carforum.models.Role;
import com.example.carforum.models.User;
import com.example.carforum.models.dtos.UserOutput;
import com.example.carforum.models.dtos.UserUpdate;
import com.example.carforum.repositories.UserRepository;
import com.example.carforum.services.contracts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConversionService conversionService;


    @Override
    public List<UserOutput> getAll() {
        List<User> users = userRepository.findAllByIsActiveTrue();
        return users.stream().map(user -> conversionService.convert(user, UserOutput.class)).collect(Collectors.toList());

    }


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
    public UserOutput getById(int id){
        User user = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
        return conversionService.convert(user, UserOutput.class);
    }

    @Override
    public UserOutput getByUsername(String username) {
        User user = userRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
        return conversionService.convert(user, UserOutput.class);
    }

    @Override
    public UserOutput getByEmail(String email) {
        User user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
        return conversionService.convert(user, UserOutput.class);
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
        var user = userRepository.findByUsernameAndIsActiveTrue(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User", "username", request.getUsername()));

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public UserOutput edit(String currentUsername, String usernameWhoseUserEdit,  UserUpdate userUpdate) {

        if(!jwtService.isUserAllowedToEdit(currentUsername, usernameWhoseUserEdit)){
            throw new UnauthorizedOperationException("You can not edit this User!");
        }

        User user = userRepository.findByUsernameAndIsActiveTrue(currentUsername).orElseThrow(
                () -> new EntityNotFoundException("User", "username", currentUsername)
        );


        if (!passwordEncoder.matches(userUpdate.getPassword(), user.getPassword())) {
            String hashedPassword = passwordEncoder.encode(userUpdate.getPassword());
            user.setPassword(hashedPassword);
        }

        user.setFirstName(userUpdate.getFirstName());
        user.setLastName(userUpdate.getLastName());
        user.setEmail(userUpdate.getEmail());

        userRepository.save(user);

        return conversionService.convert(user, UserOutput.class);
    }

    @Override
    public void deactivateUser(int userId, String currentUsername) {
        User user = userRepository.findByIdAndIsActiveTrue(userId).orElseThrow(
                () -> new EntityNotFoundException("User", "username", currentUsername)
        );

        User currUser = userRepository.findByUsernameAndIsActiveTrue(currentUsername)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", currentUsername));

        if(!jwtService.isUserAllowedToEdit(currentUsername, user.getUsername()) && !currUser.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("You can not delete other user if you are not an admin!");
        }

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void userToBeModerator(String loggedUserUsername, int userToModeratorId){

        User loggedUser = userRepository.findByUsernameAndIsActiveTrue(loggedUserUsername)
                        .orElseThrow(() -> new EntityNotFoundException("User", "username", loggedUserUsername));

        User userToBeModerator = userRepository.findByIdAndIsActiveTrue(userToModeratorId)
                        .orElseThrow(() -> new EntityNotFoundException("User", userToModeratorId));


        if(!loggedUser.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("You can not moderate this User!");
        }

        if(userToBeModerator.getRole().equals(Role.USER)){
            userToBeModerator.setRole(Role.MODERATOR);
            userRepository.save(userToBeModerator);
        }else if(userToBeModerator.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("You can not moderate admins!");
        }else if(userToBeModerator.getRole().equals(Role.MODERATOR)){
            throw new UnauthorizedOperationException("This user is already moderator!");
        }

    }

    @Override
    public void moderatorToBeUser(String loggedUserUsername, int moderatorToUserId){

        User loggedUser = userRepository.findByUsernameAndIsActiveTrue(loggedUserUsername)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", loggedUserUsername));

        User moderatorToBeUser = userRepository.findByIdAndIsActiveTrue(moderatorToUserId)
                .orElseThrow(() -> new EntityNotFoundException("User", moderatorToUserId));


        if(!loggedUser.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("You can not moderate this User!");
        }

        if(moderatorToBeUser.getRole().equals(Role.MODERATOR)){
            moderatorToBeUser.setRole(Role.USER);
            userRepository.save(moderatorToBeUser);
        }else if(moderatorToBeUser.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("You can not change the role of other admins!");
        }else if(moderatorToBeUser.getRole().equals(Role.USER)){
            throw new UnauthorizedOperationException("This user is already user!");
        }
    }

    @Override
    public void blockUser(String loggedUsername, int id){

        User loggedUser = userRepository.findByUsernameAndIsActiveTrue(loggedUsername)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", loggedUsername));


        User userToBlock = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));


        if(!loggedUser.getRole().equals(Role.ADMIN) && !loggedUser.getRole().equals(Role.MODERATOR)){
            throw new UnauthorizedOperationException("You have not permission to block users!");
        }

        if(loggedUser.getRole().equals(Role.ADMIN) && userToBlock.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("Admin can not block other admins!");
        }

        if(loggedUser.getRole().equals(Role.MODERATOR) && userToBlock.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("Moderator can not block admins!");
        }

        if (loggedUser.getRole().equals(Role.MODERATOR) && userToBlock.getRole().equals(Role.MODERATOR)){
            throw new UnauthorizedOperationException("Moderator can not block other moderators!");
        }

        if(loggedUser.isBlocked()){
            throw new UnauthorizedOperationException("You are blocked!");
        }

        if(userToBlock.isBlocked()){
            throw new UnauthorizedOperationException("This user is already blocked!");
        }

        userToBlock.setBlocked(true);
        userRepository.save(userToBlock);
    }

    @Override
    public void unblockUser(String loggedUsername, int id){

        User loggedUser = userRepository.findByUsernameAndIsActiveTrue(loggedUsername)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", loggedUsername));


        User userToUnblock = userRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));


        if(!loggedUser.getRole().equals(Role.ADMIN) && !loggedUser.getRole().equals(Role.MODERATOR)){
            throw new UnauthorizedOperationException("You have not permission to unblock users!");
        }


        if (loggedUser.getRole().equals(Role.MODERATOR) && userToUnblock.getRole().equals(Role.MODERATOR)){
            throw new UnauthorizedOperationException("Moderator can not unblock other moderators!");
        }

        if(loggedUser.isBlocked()){
            throw new UnauthorizedOperationException("You are blocked!");
        }

        if(!userToUnblock.isBlocked()){
            throw new UnauthorizedOperationException("This user is not blocked!");
        }

        userToUnblock.setBlocked(false);
        userRepository.save(userToUnblock);
    }
}
