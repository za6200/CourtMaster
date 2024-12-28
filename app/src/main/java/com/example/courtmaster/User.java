package com.example.courtmaster;

import java.util.ArrayList;
import java.util.List;

public class User extends Training_Program {
    private String uid;
    private String username;
    private List<String> programs;


    public User() {
        this.programs = new ArrayList<>();
    }

    public User(String uid, String username,  List<String> programs) {
        this.uid = uid;
        this.username = username;
        this.programs = new ArrayList<>();
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

    public List<String> getPrograms()
    {
        return this.programs;
    }
    public void setPrograms(List<String> program)
    {
        this.programs = program;
    }
}
