package com.example.carforum.converters;

import com.example.carforum.models.User;
import com.example.carforum.models.dtos.UserUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserUpdateToUserConverter implements Converter<UserUpdate, User> {

    @Override
    public User convert(UserUpdate user) {
        return User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .password(user.getPassword())
                .email(user.getEmail())
                .build();
    }
}
