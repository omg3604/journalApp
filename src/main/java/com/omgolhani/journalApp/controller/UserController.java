package com.omgolhani.journalApp.controller;

import com.omgolhani.journalApp.entity.JournalEntry;
import com.omgolhani.journalApp.entity.User;
import com.omgolhani.journalApp.services.UserService;
import org.apache.coyote.Response;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("user")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

//    @GetMapping
//    public ResponseEntity<?> getAll(){
//        List<User> userList = userService.getAllUsers();
//        return new ResponseEntity<>(userList , HttpStatus.OK);
//    }

    // update user details
    @PutMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestBody User newUser){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();         // SecurityContextHolder is a central class in Spring Security that holds the details of the authenticated principal (user).
        String userName = authentication.getName();
        User oldUser = userService.findByUserName(userName);
        oldUser.setUserName(newUser.getUserName());
        oldUser.setPassword(newUser.getPassword());
        userService.saveNewUser(oldUser);
        return new ResponseEntity<>(oldUser , HttpStatus.OK);
    }

    // delete a user
    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        userService.deleteByUserName(userName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
