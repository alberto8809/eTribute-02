package org.example.users.controller;


import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.users.model.User;
import org.example.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/")
public class UserController {

    Logger LOGGER = LogManager.getLogger(UserController.class);
    @Autowired
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /* Get User by Id */
    @GetMapping("users/{user_id}")
    public Optional<User> getUserById(@PathVariable(name = "user_id") int user_id) {
        return userService.getUserById(user_id);
    }

    /* Get all users - List users */
    @GetMapping("users")
    public List<User> getAllUser() {
        return userService.getAllUsers();
    }

    /* Login User by token */
    @GetMapping("login/{token}")
    public ResponseEntity<User> getUserLogin(@PathVariable(name = "token") String token) {
        User user = userService.getUserLogin(token);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    /* Delete User */
    @Modifying
    @Transactional
    @GetMapping("usersDelete/{token}")
    public ResponseEntity<HttpStatus> deleteUser(@PathVariable(name = "token") String token) {
        if (1 == userService.deleteUserByToken(token)) {
            return new ResponseEntity<>(HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /* Update User */
    @Transactional
    @PostMapping(value = "usersUpdate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@RequestBody User user) {

        User newUser = userService.updateUser(user);
        if (newUser != null) {
            return new ResponseEntity<>(newUser, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);

    }

    /* Create User */
    @Transactional
    @PostMapping(value = "users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> addNewUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

}
