package com.omgolhani.journalApp.controller;

import com.omgolhani.journalApp.entity.User;
import com.omgolhani.journalApp.services.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/public")
@CrossOrigin(origins = "http://localhost:3000")
public class PublicController {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck(){
        return "Ok!";
    }

    @PostMapping("/create-user")
    public void createUser(@RequestBody User newUser){
        userService.saveNewUser(newUser);
        // userService.saveUser(newUser);
    }

    @PostMapping("/login-user")
    public ResponseEntity<?> loginUser(@RequestBody User user){

        User userDB = userService.findByUserName(user.getUserName());
        if(userDB!=null){
            if(passwordEncoder.matches(user.getPassword() , userDB.getPassword())){
                return new ResponseEntity<>(user , HttpStatus.OK);
            }
            else{
                return new ResponseEntity<>("Wrong Credentials ", HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>("No users found in the database" , HttpStatus.NOT_FOUND);
    }

}
