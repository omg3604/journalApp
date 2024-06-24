package com.omgolhani.journalApp.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class CustomResponse {

    private String _id;
    private String course;
    private String title;
    private String content;

    public CustomResponse(){

    }

    public CustomResponse(String _id , String course , String title , String content) {
        this._id = _id;
        this.course = course;
        this.title = title;
        this.content = content;
    }

}
