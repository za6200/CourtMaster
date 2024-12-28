package com.example.courtmaster;

import java.util.ArrayList;
import java.util.List;

public class User extends Training_Program {
    private String uid;
    private String username;
    private List<String> ratedPrograms;


    public User() {
        this.ratedPrograms = new ArrayList<>();
    }

    public User(String uid, String username,  List<String> ratedPrograms) {
        this.uid = uid;
        this.username = username;
        this.ratedPrograms = new ArrayList<>();
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

    public List<String> getRatedPrograms()
    {
        return this.ratedPrograms;
    }
    public void setRatedPrograms(List<String> program)
    {
        this.ratedPrograms = ratedPrograms;
    }
}
