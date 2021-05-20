package com.boot.contactmanager.daos;

import java.util.List;

import com.boot.contactmanager.entities.PasswordReset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer> {

    // get all users given email (empty / list with 1 element)
    @Query(value = "select * from password_reset where password_reset.email = :email", nativeQuery = true)
    public List<PasswordReset> getEntriesByEmail(@Param("email") String email);

}
