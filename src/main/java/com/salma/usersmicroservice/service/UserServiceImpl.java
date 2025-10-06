package com.salma.usersmicroservice.service;

import com.salma.usersmicroservice.service.exceptions.ExpiredTokenException;
import com.salma.usersmicroservice.service.exceptions.InvalidTokenException;
import com.salma.usersmicroservice.service.register.VerificationTokenRepository;
import com.salma.usersmicroservice.service.exceptions.EmailAlreadyExistsException;
import com.salma.usersmicroservice.service.register.RegistrationRequest;
import com.salma.usersmicroservice.entities.Role;
import com.salma.usersmicroservice.entities.User;
import com.salma.usersmicroservice.repos.RoleRepository;
import com.salma.usersmicroservice.repos.UserRepository;
import com.salma.usersmicroservice.service.register.VerificationToken;
import com.salma.usersmicroservice.util.EmailSender;
import com.salma.usersmicroservice.util.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRep;

    @Autowired
    RoleRepository roleRep;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    VerificationTokenRepository verificationTokenRepo;

    @Autowired
    EmailSender emailSender;

    @Override
    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRep.save(user);
    }

    @Override
    public User addRoleToUser(String username, String rolename) {
        User usr = userRep.findByUsername(username);
        Role r = roleRep.findByRole(rolename);
        usr.getRoles().add(r);
        return usr;
    }

    @Override
    public User registerUser(RegistrationRequest request) {
        Optional<User> optionaluser = userRep.findByEmail(request.getEmail());
        if (optionaluser.isPresent())
            throw new EmailAlreadyExistsException("Email déjà existant!");

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        newUser.setEnabled(false);

        // Enregistrer l'utilisateur avant d'ajouter le rôle
        userRep.save(newUser);

        // Ajouter rôle USER par défaut
        Role r = roleRep.findByRole("USER");
        List<Role> roles = new ArrayList<>();
        roles.add(r);
        newUser.setRoles(roles);

        userRep.save(newUser);

        // Générer le code secret
        String code = this.generateCode();

        // Créer le token de vérification
        VerificationToken token = new VerificationToken(code, newUser);
        verificationTokenRepo.save(token);

        //envoyer par email pour valider l'email de l'utilisateur
        sendEmailUser(newUser, token.getToken());
        return newUser;
    }

    @Override
    public Role addRole(Role role) {
        return roleRep.save(role);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRep.findByUsername(username);
    }

    @Override
    public List<User> findAllUsers() {
        return userRep.findAll();
    }

    private String generateCode() {
        Random random = new Random();
        Integer code = 100000 + random.nextInt(900000);
        return code.toString();
    }

    @Override
    public void sendEmailUser(User u, String code) {
        String emailBody = "Bonjour " + "<h1>" + u.getUsername() + "</h1>" + " Votre code de validation est " + "<h1>"
                + code + "</h1>";
        emailSender.sendEmail(u.getEmail(), emailBody);
    }

    @Override
    public User validateToken(String code) {
        VerificationToken token = verificationTokenRepo.findByToken(code);
        if(token == null){
            throw new InvalidTokenException("Invalid Token");
        }

        User user = token.getUser();
        Calendar calendar = Calendar.getInstance();
        if ((token.getExpirationTime().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepo.delete(token);
            throw new ExpiredTokenException("expired Token");
        }
        user.setEnabled(true);
        userRep.save(user);
        return user;
    }
}
