package com.boot.contactmanager.daos;

import java.util.List;

import com.boot.contactmanager.entities.Contact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    // get all contacts of user given userid
    // Pageable needs current page and contacts per page
    @Query("from Contact as c where c.owner.id = :id")
    public Page<Contact> getContactsByOwner(@Param("id") Integer id, Pageable pageable);

    // get all contacts given name
    @Query(value = "select * from contact where contact.name like %:name% and contact.owner_id = :id limit 5", nativeQuery = true)
    public List<Contact> getContactsByName(@Param("name") String name, @Param("id") Integer id);

}
