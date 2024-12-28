package com.example.courtmaster;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Training_Program extends Exercise implements Serializable{
    private String name;
    private String description;
    private List<Exercise> program;
    private float rating;
    private List<Float> ratings;
    private String creator;

    public Training_Program(){
        this.program = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.ratings.add(0f);
    }
    public Training_Program(String name, String description,List<Exercise> program, float rating, List<Float> ratings, String creator)
    {
        this.name = name;
        this.description = description;
        this.program = program;
        this.rating = rating;
        this.ratings = ratings;
        this.creator = creator;
    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public List<Exercise> getProgram()
    {
        return this.program;
    }
    public void setProgram(List<Exercise> program)
    {
        this.program = program;
    }
    public String getDescription()
    {
        return this.description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<Float> getRatings()
    {
        return this.ratings;
    }
    public void addRating(float rating)
    {
        this.ratings.add(rating);
    }
    public void setRatings(List<Float> ratings)
    {
        this.ratings = ratings;
    }

    public float getRating()
    {
        return this.rating;
    }
    public void setRating(float rating)
    {
        this.rating = rating;
    }

    public String getCreator()
    {
        return this.creator;
    }
    public void setCreator(String creator)
    {
        this.creator = creator;
    }
}
