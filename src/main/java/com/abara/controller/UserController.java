package com.abara.controller;

import com.abara.entity.User;
import com.abara.model.ApplicationUserDetails;
import com.abara.service.UserService;
import com.abara.validation.EntityValidator;
import com.abara.validation.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private EntityValidator entityValidator;

    @GetMapping("/list")
    public List<ApplicationUserDetails> list() {
        LOG.debug("Retrieving all User Details");
        return userService.list();
    }

    @GetMapping("/details/{userId}")
    public ResponseEntity<ApplicationUserDetails> details(@PathVariable Long userId) {
        LOG.debug("Getting details of User by id: " + userId);

        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            return ResponseEntity.ok(new ApplicationUserDetails(user.getId(), user.getUsername(), user.getRoles()));
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public ResponseEntity<ValidationResult> create(@RequestBody User user) {
        LOG.debug("Creating User: " + user);

        Optional<ValidationResult> validationResult = entityValidator.validate(user);
        if (validationResult.isPresent()) {
            return ResponseEntity.badRequest().body(validationResult.get());
        }

        User newUser = userService.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/details/{id}").buildAndExpand(newUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/update")
    public ResponseEntity<ValidationResult> update(@RequestBody User user) {
        LOG.debug("Updating User: " + user);

        Optional<User> existingUserOptional = userService.findById(user.getId());
        if (!existingUserOptional.isPresent()) return ResponseEntity.noContent().build();

        User existingUser = existingUserOptional.get();

        existingUser.setUsername(user.getUsername());
        if (isNotBlank(user.getPassword())) {
            existingUser.setPassword(user.getPassword());
        }
        existingUser.setRoles(user.getRoles());

        Optional<ValidationResult> validationResult = entityValidator.validate(existingUser);
        if (validationResult.isPresent()) {
            return ResponseEntity.badRequest().body(validationResult.get());
        }

        userService.save(existingUser);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/details/{id}").buildAndExpand(existingUser.getId()).toUri();
        return ResponseEntity.ok().location(location).build();
    }

    @PostMapping("/delete/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        LOG.debug("Deleting User by ID: " + userId);

        Optional<User> existingUser = userService.findById(userId);
        if (!existingUser.isPresent()) return ResponseEntity.noContent().build();

        userService.delete(userId);
        return ResponseEntity.ok().build();
    }
}
