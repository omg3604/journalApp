package com.omgolhani.journalApp.controller;

import com.omgolhani.journalApp.entity.CustomResponse;
import com.omgolhani.journalApp.entity.JournalEntry;
import com.omgolhani.journalApp.entity.User;
import com.omgolhani.journalApp.services.JournalEntryService;
import com.omgolhani.journalApp.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("journal")
@CrossOrigin(origins = "http://localhost:3000")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    // ResponseEntity<?> denotes that it can return any type of class.
    // get all entries of all users : Admin action
    public ResponseEntity<?> getAll() {
        List<JournalEntry> allEntry = journalEntryService.getAllEntry();
        if(allEntry!=null && !allEntry.isEmpty()){
            return new ResponseEntity<>(allEntry , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get all entries of a specific user
    @GetMapping("/get-entries")
    // ResponseEntity<?> denotes that it can return any type of class.
    public ResponseEntity<?> getAllEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        List<JournalEntry> allEntry = user.getJournalEntries();
        List<CustomResponse> allEntryCustom = customizeEntries(allEntry);
        if(allEntry!=null && !allEntryCustom.isEmpty()){
            return new ResponseEntity<>(allEntryCustom , HttpStatus.OK);
        }
        if(allEntry.isEmpty()){
            return new ResponseEntity<>(allEntryCustom , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public List<CustomResponse> customizeEntries(List<JournalEntry> entryList){
        List<CustomResponse> res = new ArrayList<>();
        for(JournalEntry j : entryList){
            String id = j.getId().toHexString();
            CustomResponse c1 = new CustomResponse(id , j.getCourse() , j.getTitle() , j.getContent());
            res.add(c1);
        }
        return res;
    }

    @PostMapping("/get-entries-by-course")
    public ResponseEntity<?> getEntriesByCourse(@RequestBody JournalEntry jentry){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        String courseName = jentry.getCourse();
        List<JournalEntry> allEntry = user.getJournalEntries();
        List<CustomResponse> allEntryCustom = customizeEntries(allEntry);
        List<CustomResponse> finalEntry = new ArrayList<>();
        for(CustomResponse c : allEntryCustom){
            if(c.getCourse().equals(courseName)){
                finalEntry.add(c);
            }
        }
        if(allEntry!=null && !allEntryCustom.isEmpty()){
            return new ResponseEntity<>(finalEntry , HttpStatus.OK);
        }
        if(allEntry.isEmpty()){
            return new ResponseEntity<>(finalEntry , HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // create new journal entry for a specific user in database and add its reference to the user's journalEntry list.
    @PostMapping("/create-entry")
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry newEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            journalEntryService.saveEntry(newEntry , userName);
            String res = newEntry.getId().toHexString();
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("ObjectID", res);
            CustomResponse customResponse = new CustomResponse(res , newEntry.getTitle(), newEntry.getTitle(), newEntry.getContent());
            ResponseEntity<?> response = new ResponseEntity<>(customResponse , HttpStatus.CREATED);
            return response;
        } catch (Exception e){
            ResponseEntity<?> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return response;
        }
    }

    // fetching an entry by its id.
    @GetMapping("/get-entry/{myId}")
    public ResponseEntity<JournalEntry> getById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
        // checking if journal entry with provided id is in user's journalEntryList or not.
        List<JournalEntry> userEntries = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        ResponseEntity<JournalEntry> response;
        if(!userEntries.isEmpty()){
            Optional<JournalEntry> fetchedEntry = journalEntryService.getEntryById(myId);
            if(fetchedEntry.isPresent() ){
                response = new ResponseEntity<>(fetchedEntry.get() , HttpStatus.OK);
            }
            else{
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return response;
        }
        response = new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return response;
    }

    // get all entries with specific course name
//    @GetMapping("/get-entries-by-course/{courseName}")
//    public ResponseEntity<?> getAllEntriesByCourse(@PathVariable String courseName){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String userName = authentication.getName();
//        User user = userService.findByUserName(userName);
//        List<JournalEntry> courseEntry = user.getJournalEntries().stream().filter(x -> x.getCourse().equals(courseName)).collect(Collectors.toList());
//        if(!courseEntry.isEmpty()){
//            return new ResponseEntity<>(courseEntry , HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }

    // delete an entry by its id
    @DeleteMapping("/delete-entry/{myId}")
    public ResponseEntity<?> deleteById(@PathVariable ObjectId myId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userService.findByUserName(userName);
        // checking if journal entry with provided id is in user's journalEntryList or not.
        List<JournalEntry> userEntries = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!userEntries.isEmpty()){
            journalEntryService.deleteEntryById(myId , userName);
            return new ResponseEntity<>("Entry deleted successfully!",HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("No Entry found with ID : " + myId , HttpStatus.NOT_FOUND);
        }
    }

    // update an entry by its id
    @PutMapping("/update-entry/{myId}")
    public ResponseEntity<?> updateById(
            @RequestBody JournalEntry newEntry,
            @PathVariable ObjectId myId
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userService.findByUserName(userName);
        // checking if journal entry with provided id is in user's journalEntryList or not.
        List<JournalEntry> userEntries = user.getJournalEntries().stream().filter(x -> x.getId().equals(myId)).collect(Collectors.toList());
        if(!userEntries.isEmpty()){
            JournalEntry oldEntry = journalEntryService.getEntryById(myId).orElse(null);
            if(newEntry.getTitle()!=null && !newEntry.getTitle().equals("")){
                oldEntry.setTitle(newEntry.getTitle());
            }
            if(newEntry.getContent()!=null && !newEntry.getContent().equals("")){
                oldEntry.setContent(newEntry.getContent());
            }
            if(newEntry.getCourse()!=null && !newEntry.getCourse().equals("")){
                oldEntry.setCourse(newEntry.getCourse());
            }
            journalEntryService.updateEntry(oldEntry , userName);
            return new ResponseEntity<>(oldEntry , HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("No Entry found with ID : " + myId , HttpStatus.NOT_FOUND);
        }
    }
}
