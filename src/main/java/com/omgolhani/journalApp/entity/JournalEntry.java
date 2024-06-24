package com.omgolhani.journalApp.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "journal_entries")      // mapping collection "journal_entries" to class JournalEntry
@Data
@NoArgsConstructor
public class JournalEntry {
    @Id     // annotation for indicating unique key in the collection
    private ObjectId id;
    private String course;
    private String title;
    private String content;
    private LocalDateTime date;
}
