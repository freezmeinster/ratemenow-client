package com.oleafs.ratemenow.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.oleafs.ratemenow.R;

/**
 * Created by brambut on 7/5/2017.
 */

public class Config {
    public Context mContext;
    public SharedPreferences mPreferences;
    public SharedPreferences.Editor editor;

    public Config(Context context){
        mContext = context;
        mPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    public String getBaseURL(){
        String base_url = mPreferences.getString("base_url", "");

        if (!base_url.equals("")){
            String last_char = String.valueOf(base_url.charAt(base_url.length() - 1));
            if ( last_char.equals("/")){
                return  base_url.substring(0, base_url.length() - 1);
            } else {
                return base_url;
            }
        }
        String default_url = mContext.getResources().getString(R.string.url);
        return default_url;
    }

    public String getApiUrl(){
        String api = mContext.getResources().getString(R.string.base_api_url);
        return this.getBaseURL() + api;
    }

    public String getToken(){
        String token = mPreferences.getString("token", "");
        return token;
    }

    public void setToken(String token){
        editor.putString("token", token);
        editor.commit();
    }

    public void setBaseUrl(String base_url){
        editor.putString("base_url", base_url);
        editor.commit();
    }

    public void clearConfig(){
        editor.clear();
        editor.commit();
    }
}
