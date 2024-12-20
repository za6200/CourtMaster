package com.example.courtmaster;

public class Exercise {
    private String name;
    private int repeat;
    private String level;
    private String Url;
    private String description;

    public Exercise()
    {

    }
    public Exercise(String name, int repeat, String level, String Url, String description)
    {
        this.name = name;
        this.repeat = repeat;
        this.level = level;
        this.Url = Url;
        this.description = description;
    }

    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public int getRepeat()
    {
        return this.repeat;
    }
    public void setRepeat(int repeat)
    {
        this.repeat = repeat;
    }

    public String getLevel()
    {
        return this.level;
    }
    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getUrl()
    {
        return this.Url;
    }
    public void setUrl(String Url)
    {
        this.Url = Url;
    }

    public String getDescription()
    {
        return this.description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
}
