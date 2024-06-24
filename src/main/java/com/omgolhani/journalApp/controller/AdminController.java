package com.omgolhani.journalApp.controller;

import com.omgolhani.journalApp.entity.User;
import com.omgolhani.journalApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/get-all-users")
    public ResponseEntity<?> getAllUsers(){
        List<User> allUsers = userService.getAllUsers();
        if(allUsers!=null && !allUsers.isEmpty()){
            return new ResponseEntity<>(allUsers , HttpStatus.OK);
        }
        return new ResponseEntity<>("No users found in the database" , HttpStatus.NOT_FOUND);
    }

    @PostMapping("/add-new-admin")
    public void addNewAdmin(@RequestBody User user){
        userService.saveAdmin(user);
    }
}
