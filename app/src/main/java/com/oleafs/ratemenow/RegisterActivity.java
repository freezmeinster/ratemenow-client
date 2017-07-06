package com.oleafs.ratemenow;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.oleafs.ratemenow.utils.Config;
import com.oleafs.ratemenow.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    public EditText server_url, username, password;
    public RegisterActivity activity;
    public Config config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        config = new Config(this);
        activity = this;

        server_url = (EditText) findViewById(R.id.register_url);
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);

        Button reg_button = (Button) findViewById(R.id.register_confirm_button);
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url_content = server_url.getText().toString();
                String username_content = username.getText().toString();
                String password_content = password.getText().toString();

                if (!TextUtils.isEmpty(url_content) && !TextUtils.isEmpty(username_content) && !TextUtils.isEmpty(password_content)){
                    RegisterHandler handler = new RegisterHandler();
                    handler.execute(url_content, username_content, password_content);
                } else if (TextUtils.isEmpty(url_content)) {
                    server_url.setError("This item cannot be empty !!");
                } else if (TextUtils.isEmpty(username_content)) {
                    username.setError("This item cannot be empty !!");
                } else if (TextUtils.isEmpty(password_content)) {
                    password.setError("This item cannot be empty !!");
                }
            }
        });

    }

    public class RegisterHandler extends AsyncTask<String, Integer, Integer>{

        ProgressDialog progress;
        private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        final private int SUCCESS = 0;
        final private int ERROR_JSON_ENCODE = 1;
        final private int ERROR_SERVER_URL_INVALID = 2;
        final private int ERROR_USER_EXIST = 3;
        final private int ERROR_UNKNOWN = 4;


        OkHttpClient client = new OkHttpClient();

        public RegisterHandler() {
            progress = new ProgressDialog(activity);
            progress.setMessage("Sending registration data!");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute(){
            progress.show();
        }


        @Override
        protected Integer doInBackground(String... params) {
            Url url_util = new Url(params[0]);
            String reg_url = url_util.getSanitize() + "api/user/";

            JSONObject req_json = new JSONObject();
            try {
                req_json.put("username", params[1]);
                req_json.put("password", params[2]);
            } catch (JSONException e){
                e.printStackTrace();
                return ERROR_JSON_ENCODE;
            }

            Request.Builder builder = new Request.Builder();
            builder.url(reg_url)
                    .post(RequestBody.create(JSON, req_json.toString()));
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                try {
                    JSONObject json_resp = new JSONObject(response.body().string());
                    System.out.println("Login Response " + json_resp.toString());
                    if (json_resp.has("token")) {
                        config.setToken(json_resp.getString("token"));
                        config.setBaseUrl(url_util.getSanitize());
                        return SUCCESS;
                    } else if (json_resp.has("username")){
                        return ERROR_USER_EXIST;
                    } else {
                        return ERROR_UNKNOWN;
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    return ERROR_JSON_ENCODE;
                }
            } catch ( Exception e){
                e.printStackTrace();
                return ERROR_SERVER_URL_INVALID;
            }
        }

        @Override
        protected void onPostExecute(Integer resp) {
            progress.dismiss();
            if (resp == SUCCESS ){
                Toast.makeText(activity, "Welcome, "+username.getText().toString(), Toast.LENGTH_SHORT).show();
                Intent main = new Intent(activity, MainActivity.class);
                startActivity(main);
                finish();
            } else if (resp == ERROR_JSON_ENCODE ) {
                Toast.makeText(activity, "Opps, JSON encoding error", Toast.LENGTH_SHORT).show();
            } else if (resp == ERROR_SERVER_URL_INVALID ) {
                Toast.makeText(activity, "Please give valid Server URL!", Toast.LENGTH_SHORT).show();
            } else if (resp == ERROR_USER_EXIST ) {
                Toast.makeText(activity, "Username already registered!", Toast.LENGTH_SHORT).show();
            } else if (resp == ERROR_UNKNOWN ) {
                Toast.makeText(activity, "Opps, unknown error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
