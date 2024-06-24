package com.omgolhani.journalApp.services;

import com.omgolhani.journalApp.entity.JournalEntry;
import com.omgolhani.journalApp.entity.User;
import com.omgolhani.journalApp.repository.JournalEntryRepository;
import com.omgolhani.journalApp.repository.UserRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    @Autowired // for injecting beans (here interface implementation)
    private UserRepository userRepository;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // for saving user without encrypted password.
    public void saveUser(User user){
        try{
            userRepository.save(user);
        } catch(Exception e) {
            log.error("Exception" , e);
        }
    }

    // for saving user with encrypted password.
    public void saveNewUser(User user){
        try{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER"));
            userRepository.save(user);
        } catch(Exception e) {
            log.error("Exception" , e);
        }
    }

    // for saving new admin user
    public void saveAdmin(User user){
        try{
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Arrays.asList("USER","ADMIN"));
            userRepository.save(user);
        } catch(Exception e) {
            log.error("Exception" , e);
        }
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public Optional<User> getUserById(ObjectId id) {
        return userRepository.findById(id);
    }

    public boolean deleteUserById(ObjectId id){
        userRepository.deleteById(id);
        return true;
    }

    public User findByUserName(String userName){
        return userRepository.findByUserName(userName);
    }

    public void deleteByUserName(String userName){
        userRepository.deleteByUserName(userName);
    }
}
