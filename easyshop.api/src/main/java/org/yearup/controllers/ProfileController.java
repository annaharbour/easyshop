package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("profile")
@CrossOrigin
@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
public class ProfileController {
    private ProfileDao profileDao;
    private UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao) {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    //Creates new profile
    @PostMapping
    // https://localhost:8080/profile
    @ResponseStatus(HttpStatus.CREATED)
    public Profile create(@RequestBody Profile profile) {
        try {
            return profileDao.create(profile);
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops");
        }
    }

    // use principal to get the current user's profile
    // https://localhost:8080/profile
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Profile getProfile(Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            return profileDao.getProfile(userId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }

    // add a PUT method to update the current user's profile
    // https://localhost:8080/profile
    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ShoppingCart updateCart(
            @RequestBody Profile profile, Principal principal) {
        String userName = principal.getName();
        User user = userDao.getByUserName(userName);
        int userId = user.getId();
        if (profileDao.getProfile(userId) != null) {
            profileDao.update(userId, profile);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found in the cart.");
        }
        return null;
    }
}
