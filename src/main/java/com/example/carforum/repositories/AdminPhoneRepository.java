package com.example.carforum.repositories;

import com.example.carforum.models.AdminPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminPhoneRepository extends JpaRepository<AdminPhone, Integer> {

    Optional<AdminPhone> findByPhoneNumber(String phone);

    Optional<AdminPhone> findByPhoneId(int id);

}
