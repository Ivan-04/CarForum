package com.example.carforum.services.contracts;

import com.example.carforum.models.AdminPhone;
import com.example.carforum.models.User;
import com.example.carforum.models.dtos.AdminPhoneDto;

public interface AdminPhoneService {
    AdminPhone getPhoneNumberById(int phoneNumberId, User user);

    void addPhoneNumber(AdminPhoneDto adminPhone, String username);

    void removePhoneFromAdmin(AdminPhoneDto adminPhoneDto, String username);
}
