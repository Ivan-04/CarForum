package com.example.carforum.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


public enum Role {

    USER,
    MODERATOR,
    ADMIN

}
