package org.example.users.service;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.users.model.User;
import org.example.users.repository.UserRepository;
import org.example.users.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    final Logger LOGGER = LogManager.getLogger(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setToken(EncryptionUtil.encrypt(user.getUser_mail() + ":" + user.getUser_password()));
        user.setUser_password(passwordEncoder.encode(user.getUser_password()));
        user.setUser_type("U");
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public Optional<User> getUserById(int id) {
        return userRepository.findById((long) id);
    }

    public User getUserLogin(String token) {

        String decodedString = EncryptionUtil.decrypt(token);
        String[] user_pass = decodedString.split(":");
        User user = userRepository.getUserByMail(user_pass[0]);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(user_pass[1], user.getUser_password())) {
            user.setToken(EncryptionUtil.generateToken());
        } else {
            return null;
        }
        return userRepository.save(user);

    }

    public int deleteUserByToken(String token) {
        String decodedString = EncryptionUtil.decrypt(token);
        String[] user_pass = decodedString.split(":");
        return userRepository.deleteUser(user_pass[0]);

    }


    public User updateUser(User user) {
        String decodedString = EncryptionUtil.decrypt(user.getToken());
        String[] user_pass = decodedString.split(":");

        LOGGER.info("pass {{}} { {}}", user_pass[0], user_pass[1]);

        User userFromDB = userRepository.getUserByMail(user_pass[0]);

        if (user_pass[1].equals(userFromDB.getUser_password())) {
            userFromDB.setUser_name(user.getUser_name());
            userFromDB.setUser_maternal_lastname(user.getUser_maternal_lastname());
            userFromDB.setUser_lastname(user.getUser_lastname());
            userFromDB.setUser_phone(user.getUser_phone());
            userFromDB.setUser_city(user.getUser_city());
            userFromDB.setUser_mail(user.getUser_mail());
            LOGGER.info("new token {{}}", EncryptionUtil.encrypt(user.getUser_mail() + ":" + userFromDB.getUser_password()));
            userFromDB.setToken(EncryptionUtil.encrypt(user.getUser_mail() + ":" + userFromDB.getUser_password()));

            return userRepository.save(userFromDB);
        }
        return null;
    }

}
