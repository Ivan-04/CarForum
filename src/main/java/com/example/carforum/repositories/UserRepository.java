package com.example.carforum.repositories;

import com.example.carforum.models.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u " +
            "WHERE u.isActive = true " +
            "AND (:username IS NULL OR u.username like :username) " +
            "AND (:firstName IS NULL OR u.firstName like :firstName) " +
            "AND (:email IS NULL OR u.email like :email) ")
    Page<User> findUsersByMultipleFields(
            @Param("username") String username,
            @Param("firstName") String firstName,
            @Param("email") String email,
            Pageable pageable);


    List<User> findAllByIsActiveTrue();

    Optional<User> findByIdAndIsActiveTrue(int id);

    Optional<User> findByUsernameAndIsActiveTrue(String username);

    Optional<User> findByEmailAndIsActiveTrue(String email);



}
