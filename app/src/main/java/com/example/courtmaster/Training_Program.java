package com.example.courtmaster;

import java.util.List;

public class Training_Program extends Exercise{
    private String name;
    private String description;
    private List<Exercise> program;

    public Training_Program(){

    }
    public Training_Program(String name, String description,List<Exercise> program )
    {
        this.name = name;
        this.description = description;
        this.program = program;
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

    public void addExercise(Exercise exercise) {
        this.program.add(exercise);
    }
}
