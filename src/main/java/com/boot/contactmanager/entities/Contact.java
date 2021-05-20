package com.boot.contactmanager.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name = "contact")
@Data
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    @NotBlank(message = "name must not be empty")
    @Size(min = 3, max = 15, message = "name must be between three and fifteen characters")
    private String name;

    @Pattern(regexp = "^$|^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "email, if entered, must have valid format")
    @Column(name = "email")
    private String email;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "phone number, if entered, must have valid format")
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

}
