package com.omgolhani.journalApp.services;

import com.omgolhani.journalApp.entity.JournalEntry;
import com.omgolhani.journalApp.entity.User;
import com.omgolhani.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntryService {

    private static final Logger log = LoggerFactory.getLogger(JournalEntryService.class);
    @Autowired // for injecting beans (here interface implementation)
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String userName){
        try{
            User user = userService.findByUserName(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        } catch(Exception e) {
            log.error("Exception" , e);
        }
    }

    public void updateEntry(JournalEntry journalEntry, String userName){
        try{
            User user = userService.findByUserName(userName);
            JournalEntry saved = journalEntryRepository.save(journalEntry);
        } catch(Exception e) {
            log.error("Exception" , e);
        }
    }



    public List<JournalEntry> getAllEntry(){
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getEntryById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public void deleteEntryById(ObjectId id, String userName){
        try {
            User user = userService.findByUserName(userName);
            // first deleting the reference of journalEntry from that user's journalEntryList and updating the new list in the user.
            boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if(removed){
                userService.saveUser(user);
                // now deleting the journalEntry from the journal_entries database.
                journalEntryRepository.deleteById(id);
            }
        }
        catch (Exception e){
            throw new RuntimeException("An error occured while deleting the entry!" , e);
        }
    }

}
