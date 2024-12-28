package com.example.courtmaster;

import java.util.ArrayList;
import java.util.List;

public class User extends Training_Program {
    private String uid;
    private String username;


    public User() {
        //this.program = new ArrayList<>();
        //this.ratedPrograms = new ArrayList<>();
    }

    public User(String uid, String username, List<Training_Program> program, List<Training_Program> ratedPrograms) {
        this.uid = uid;
        this.username = username;
        //this.program = new ArrayList<>();
        //this.ratedPrograms = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /*public List<Training_Program> getPrograms()
    {
        return this.program;
    }
    public void setPrograms(List<Training_Program> program)
    {
        this.program = program;
    }

    public List<Training_Program> getRatedPrograms()
    {
        return this.ratedPrograms;
    }
    public void setRatedPrograms(List<Training_Program> program)
    {
        this.ratedPrograms = ratedPrograms;
    }*/
}
