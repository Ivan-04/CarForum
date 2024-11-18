package com.example.carforum.converters;

import com.example.carforum.models.User;
import com.example.carforum.models.dtos.UserOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserToUserOutputConverter implements Converter<User, UserOutput> {

    @Override
    public UserOutput convert(User user) {
        return UserOutput.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePhoto())
                .build();
    }

}
