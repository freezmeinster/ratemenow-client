package com.oleafs.ratemenow;

import java.io.Serializable;

/**
 * Created by brambut on 7/2/2017.
 */

public class Place implements Serializable {
    private String name;
    private String description;
    private String owner;
    private String id;
    private String picture;
    private Boolean is_rated;

    public Place(String id, String name, String desc, String owner, String picture, Boolean is_rated){
        this.id = id;
        this.name = name;
        this.description = desc;
        this.owner = owner;
        this.picture = picture;
        this.is_rated = is_rated;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public String getOwner(){
        return "By " + this.owner;
    }

    public String getId(){
        return this.id;
    }

    public String getPicture(){
        return this.picture;
    }

    public Boolean getIs_rated(){
        return this.is_rated;
    }

    public void setIsRated(Boolean status){
        this.is_rated = status;
    }
}
