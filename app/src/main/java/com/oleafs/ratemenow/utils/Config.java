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

    public Config(Context context){
        mContext = context;
        mPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    public String getBaseURL(){
        String base_url = mPreferences.getString("base_url", "");
        String api = mContext.getResources().getString(R.string.base_api_url);
        if (!base_url.equals("")){
            String last_char = String.valueOf(base_url.charAt(base_url.length() - 1));
            if ( !last_char.equals("/")){
                return base_url + "/" + api;
            } else {
                return base_url + api;
            }
        }
        String default_url = mContext.getResources().getString(R.string.url);
        return default_url + "/" + api;
    }
}
