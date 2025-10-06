package com.salma.usersmicroservice.service;

import com.salma.usersmicroservice.entities.Role;
import com.salma.usersmicroservice.entities.User;
import com.salma.usersmicroservice.service.register.RegistrationRequest;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User findUserByUsername (String username);
    Role addRole(Role role);
    User addRoleToUser(String username, String rolename);
    User registerUser(RegistrationRequest request);

    public void sendEmailUser(User u, String code);
    public User validateToken(String code);
    List<User> findAllUsers();
}
