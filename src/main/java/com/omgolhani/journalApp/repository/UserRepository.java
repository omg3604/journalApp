package com.omgolhani.journalApp.repository;

import com.omgolhani.journalApp.entity.JournalEntry;
import com.omgolhani.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId>{
    User findByUserName(String userName);
    void deleteByUserName(String userName);
}
