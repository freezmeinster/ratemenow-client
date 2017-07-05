package com.oleafs.ratemenow.utils;

/**
 * Created by brambut on 7/5/2017.
 */

public class Url {
    private String url;

    public Url(String url){
        this.url = url;
    }

    public String getSanitize(){
        String last_char = String.valueOf(this.url.charAt(this.url.length() - 1));
        if ( !last_char.equals("/")){
            return this.url + "/";
        }
        return this.url;
    }
}
