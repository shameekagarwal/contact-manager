package com.boot.contactmanager.controllers;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.boot.contactmanager.daos.ContactRepository;
import com.boot.contactmanager.daos.UserRepository;
import com.boot.contactmanager.entities.Contact;
import com.boot.contactmanager.entities.User;
import com.boot.contactmanager.utils.ImageUploader;
import com.boot.contactmanager.utils.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("contacts")
public class ContactController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ImageUploader imageUploader;

    // retrieve current user based on spring security session
    private User getCurrentUser(Principal principal) {
        String email = principal.getName();
        List<User> users = userRepository.getUsersByEmail(email);
        return users.get(0);
    }

    // returns null if current user is owner of existing contact
    // else returns the error message
    private Message authorizeContactAction(Principal principal, Integer contactId, String action) {

        // get user and contact
        User currentUser = getCurrentUser(principal);
        Optional<Contact> optionalContact = contactRepository.findById(contactId);

        // contact not present in db
        if (!optionalContact.isPresent()) {
            return new Message("the contact you are trying to " + action + " doesn't exist", "error");
        }

        // current user isnt owner of contact
        if (optionalContact.get().getOwner().getId() != currentUser.getId()) {
            return new Message("you need to be the owner of the contact you are trying to " + action, "error");
        }

        return null;

    }

    // uploading image
    // if unsuccessful, populate with correct error
    // if empty, use default picture
    // if successful, upload to fs
    private void uploadImage(MultipartFile image, Contact contact, BindingResult result) {
        try {
            // try uploading image, else use default and save it
            if (!image.isEmpty()) {
                contact.setImageUrl(imageUploader.uploadImage(image));
            } else if (contact.getImageUrl() == null || contact.getImageUrl().equals("")) {
                contact.setImageUrl("default_profile_pic.png");
            }
        } catch (Exception e) {
            // uploading failed e.g. files with wrong extension
            result.rejectValue("imageUrl", "contact.imageUrl", "image upload failed, please check image format");
        }
    }

    // works for all handlers of this controller
    // add user so that all templates from /contacts/** will have user information
    @ModelAttribute
    public void addUserToModel(Model model, Principal principal) {
        String email = principal.getName();
        List<User> users = userRepository.getUsersByEmail(email);
        model.addAttribute("currentUser", users.get(0));
    }

    @GetMapping("{currentPage}")
    public String dashboard(@PathVariable("currentPage") Integer currentPage, Model model, Principal principal) {
        model.addAttribute("title", "Contacts");

        // get current user and his 5 contacts
        User user = getCurrentUser(principal);
        Pageable pageable = PageRequest.of(currentPage - 1, 5);
        Page<Contact> contacts = contactRepository.getContactsByOwner(user.getId(), pageable);

        // retrun success
        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", contacts.getTotalPages());
        return "contacts";
    }

    @GetMapping("create")
    public String getCreateContact(Model model) {
        model.addAttribute("title", "Create Contact");
        model.addAttribute("action", "/contacts/create/");
        Contact contact = new Contact();
        model.addAttribute("contact", contact);
        return "contact-form";
    }

    @PostMapping("create")
    public String postCreateContact(Model model, @RequestParam("image") MultipartFile image,
            @Valid @ModelAttribute("contact") Contact contact, BindingResult result, HttpSession session,
            Principal principal) {

        // upload image to any provider only if other fields are correctly processed
        // thus reducing uploads
        if (!result.hasErrors()) {
            // upload image
            uploadImage(image, contact, result);
        }

        // validation failed
        if (result.hasErrors()) {
            model.addAttribute("title", "Create Contact");
            return "contact-form";
        }

        // set owner and save contact
        User owner = getCurrentUser(principal);
        contact.setOwner(owner);
        contactRepository.save(contact);

        // return success
        Message message = new Message("contact has been created successfully", "success");
        session.setAttribute("message", message);
        return "redirect:/contacts/1/";
    }

    @GetMapping("update/{contactId}")
    public String getUpdateContact(Model model, @PathVariable("contactId") Integer contactId, Principal principal,
            HttpSession session) {

        // cannot update contact, return error
        Message message = authorizeContactAction(principal, contactId, "update");
        if (message != null) {
            session.setAttribute("message", message);
            return "redirect:/contacts/1/";
        }

        // retrun success
        Contact contact = contactRepository.findById(contactId).get();
        model.addAttribute("title", "Update Contact");
        model.addAttribute("contact", contact);
        model.addAttribute("action", "/contacts/update/" + contactId);
        return "contact-form";
    }

    @PostMapping("update/{contactId}")
    public String postUpdateContact(@PathVariable("contactId") Integer contactId, Model model,
            @RequestParam("image") MultipartFile image, @Valid @ModelAttribute("contact") Contact contact,
            BindingResult result, HttpSession session, Principal principal) {

        // cannot update contact, return error
        Message message = authorizeContactAction(principal, contactId, "update");
        if (message != null) {
            session.setAttribute("message", message);
            return "redirect:/contacts/1/";
        }

        // upload image
        if (!result.hasErrors()) {
            uploadImage(image, contact, result);
        }

        // validation failed
        if (result.hasErrors()) {
            model.addAttribute("title", "Update Contact");
            return "contact-form";
        }

        // set owner and save contact
        User owner = getCurrentUser(principal);
        contact.setOwner(owner);
        contactRepository.save(contact);

        // return success
        message = new Message("contact has been updated successfully", "success");
        session.setAttribute("message", message);
        return "redirect:/contacts/1/";
    }

    @GetMapping("delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") Integer contactId, Principal principal,
            HttpSession session) {

        // cannot delete contact, return error
        Message message = authorizeContactAction(principal, contactId, "delete");
        if (message != null) {
            session.setAttribute("message", message);
            return "redirect:/contacts/1/";
        }

        // return success
        contactRepository.deleteById(contactId);
        message = new Message("contact has been deleted successfully", "success");
        session.setAttribute("message", message);
        return "redirect:/contacts/1/";
    }

}
