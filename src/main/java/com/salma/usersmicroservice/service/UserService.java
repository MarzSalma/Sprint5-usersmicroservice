package com.salma.usersmicroservice.service;

import com.salma.usersmicroservice.entities.Role;
import com.salma.usersmicroservice.entities.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User findUserByUsername (String username);
    Role addRole(Role role);
    User addRoleToUser(String username, String rolename);

    List<User> findAllUsers();
}
