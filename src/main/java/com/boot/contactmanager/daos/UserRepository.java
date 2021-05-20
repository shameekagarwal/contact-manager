package com.boot.contactmanager.daos;

import java.util.List;

import com.boot.contactmanager.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    // get all users given email (empty / list with 1 element)
    @Query(value = "select * from user where user.email = :email", nativeQuery = true)
    public List<User> getUsersByEmail(@Param("email") String email);

}
