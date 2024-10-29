package com.paymybuddy.service;

import com.paymybuddy.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserService {


    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findAll();

    Optional<User> loadByUsername(String username);

    public User registerUser(User user);

    public void updateUser(User user);

    public void addConnection(User user, User connection);

    public void updatePassword(User user, String newPassword);

    public boolean isConnection(User user, User connection);

    public UserDetails loadUserByUsername(String email);
}
