package com.example.carforum.services;

import com.example.carforum.exceptions.DuplicateEntityException;
import com.example.carforum.exceptions.EntityNotFoundException;
import com.example.carforum.exceptions.UnauthorizedOperationException;
import com.example.carforum.models.AdminPhone;
import com.example.carforum.models.Role;
import com.example.carforum.models.User;
import com.example.carforum.models.dtos.AdminPhoneDto;
import com.example.carforum.repositories.AdminPhoneRepository;
import com.example.carforum.repositories.UserRepository;
import com.example.carforum.services.contracts.AdminPhoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminPhoneServiceImpl implements AdminPhoneService {

    private final AdminPhoneRepository adminPhoneRepository;
    private final UserRepository userRepository;
    private final ConversionService conversionService;


    @Override
    public AdminPhone getPhoneNumberById(int phoneNumberId, User user){
        if(!user.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("You are not an admin!");
        }
        return adminPhoneRepository.findById(phoneNumberId).orElseThrow(
                () -> new EntityNotFoundException("Phone", phoneNumberId));
    }

    @Override
    public void addPhoneNumber(AdminPhoneDto adminPhone, String username) {
        User user = userRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));

        if(!user.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("You are not an admin!");
        }
        boolean duplicateExists = adminPhoneRepository.findByPhoneNumber(adminPhone.getPhoneNumber()).isPresent();

        if (duplicateExists) {
            throw new DuplicateEntityException("Phone", "number", adminPhone.getPhoneNumber());
        }


        AdminPhone phone = conversionService.convert(adminPhone, AdminPhone.class);

        phone.setUser(user);
        adminPhoneRepository.save(phone);
    }

    @Override
    public void removePhoneFromAdmin(AdminPhoneDto phone, String loggedUserUsername) {

        User user = userRepository.findByUsernameAndIsActiveTrue(loggedUserUsername)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", loggedUserUsername));

        if(!user.getRole().equals(Role.ADMIN)){
            throw new UnauthorizedOperationException("You are not an admin!");
        }
        AdminPhone phoneNumber = adminPhoneRepository.findByPhoneNumber(phone.getPhoneNumber())
                .orElseThrow(() -> new EntityNotFoundException("Phone", "number", phone.getPhoneNumber()));

        if (phoneNumber.getUser().getId() != user.getId()) {
            throw new UnauthorizedOperationException("You do not have permission to remove this phone number.");
        }
        user.getAdminPhones().remove(phoneNumber);
        userRepository.save(user);
        adminPhoneRepository.delete(phoneNumber);
    }
}
