package com.salma.usersmicroservice.restControllers;

import java.util.List;

import com.salma.usersmicroservice.entities.User;
import com.salma.usersmicroservice.repos.UserRepository;
import com.salma.usersmicroservice.service.UserService;
import com.salma.usersmicroservice.service.register.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class UserRestController {
    @Autowired
    UserService userService;
    @Autowired
    private UserRepository userRep;

    @RequestMapping(path = "all",method = RequestMethod.GET)
    public List<User> getAllUsers() {
        return userRep.findAll();
    }

    @PostMapping("/register")
    public User register(@RequestBody RegistrationRequest request) {
        return userService.registerUser(request);
    }
    @GetMapping("/verifyEmail/{token}")
    public User verifyEmail(@PathVariable("token") String token){
        return userService.validateToken(token);
    }
}
