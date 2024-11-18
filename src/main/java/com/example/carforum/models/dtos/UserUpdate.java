package com.example.carforum.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdate {

    private MultipartFile profilePhoto;

    @NotNull(message = "First name can not be empty!")
    @Size(min = 3, max = 32, message = "First name should be between 3 and 32 symbols!")
    private String firstName;

    @NotNull(message = "Last name can not be empty!")
    @Size(min = 3, max = 32, message = "Last name should be between 3 and 32 symbols!")
    private String lastName;

    @NotNull(message = "Email can not be null!")
    @Email(message = "Email is invalid!")
    private String email;

    @NotNull(message = "Password can not be empty!")
    @Size(min = 8)
    private String password;

}
