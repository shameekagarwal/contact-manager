package com.boot.contactmanager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name = "password_reset")
@Data
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "otp")
    private Long otp;

    @Transient
    @NotBlank(message = "password must not be blank")
    @Size(min = 5, message = "password must be longer than five characters")
    private String password;

    @Column(name = "reset_request_time")
    private Long resetRequestTime;
}
