package com.example.carforum.converters;

import com.example.carforum.models.AdminPhone;
import com.example.carforum.models.User;
import com.example.carforum.models.dtos.AdminPhoneDto;
import com.example.carforum.models.dtos.UserOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminPhoneDtoToAdminPhoneConverter implements Converter<AdminPhoneDto, AdminPhone> {

    @Override
    public AdminPhone convert(AdminPhoneDto adminPhoneDto) {
        return AdminPhone.builder()
                .phoneNumber(adminPhoneDto.getPhoneNumber())
                .build();
    }
}
