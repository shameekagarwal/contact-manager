package com.boot.contactmanager.controllers;

import java.security.Principal;
import java.util.List;

import com.boot.contactmanager.daos.ContactRepository;
import com.boot.contactmanager.daos.UserRepository;
import com.boot.contactmanager.entities.Contact;
import com.boot.contactmanager.entities.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("contacts/{query}")
    public ResponseEntity<List<Contact>> searchContactsByName(@PathVariable("query") String query,
            Principal principal) {
        List<User> users = userRepository.getUsersByEmail(principal.getName());
        
        // non existent email, return error
        if (users.size() == 0) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // return 5 contacts max that match search
        User user = users.get(0);
        List<Contact> contacts = contactRepository.getContactsByName(query, user.getId());
        return ResponseEntity.status(HttpStatus.OK).body(contacts);
    }
}
