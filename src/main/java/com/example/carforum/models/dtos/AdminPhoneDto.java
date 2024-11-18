package com.example.carforum.models.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPhoneDto {

    @Size(min = 10, max = 15, message = "Phone number shout be valid")
    private String phoneNumber;

}
