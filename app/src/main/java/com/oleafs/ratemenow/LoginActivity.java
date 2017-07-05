package com.oleafs.ratemenow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oleafs.ratemenow.utils.Config;
import com.oleafs.ratemenow.utils.Url;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.SyncFailedException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private LoginActivity activity;
    private Config config;

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.activity = this;
        this.config = new Config(this);

        Button login_button = (Button) findViewById(R.id.login_button);
        final EditText url_field = (EditText) findViewById(R.id.login_url);
        final EditText username_field = (EditText) findViewById(R.id.login_username);
        final EditText password_field = (EditText) findViewById(R.id.login_password);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = activity.getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(activity);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                LoginHandler handler = new LoginHandler(activity);
                String url = url_field.getText().toString();
                String username = username_field.getText().toString();
                String password = password_field.getText().toString();
                handler.execute(url, username, password);
            }
        });

    }

    public class LoginHandler extends AsyncTask<String, Integer, Integer> {

        private MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        private ProgressDialog progress;
        private Activity activity;

        OkHttpClient client = new OkHttpClient();

        public LoginHandler(Activity activity){
            this.activity = activity;
            progress = new ProgressDialog(activity);
            progress.setIndeterminate(false);
            progress.setMessage("Try login, please wait !!");
        }

        @Override
        protected void onPreExecute(){
            progress.show();
        }

        @Override
        protected Integer doInBackground(String... params) {

            Url url_util = new Url(params[0]);
            String auth_url = url_util.getSanitize() + "api-auth-token/";

            JSONObject req_json = new JSONObject();
            try {
                req_json.put("username", params[1]);
                req_json.put("password", params[2]);
            } catch (JSONException e){
                e.printStackTrace();
            }

            Request.Builder builder = new Request.Builder();
            builder.url(auth_url)
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
                        return 0;
                    } else {
                        return 3;
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    return 1;
                }
            } catch (Exception e){
                e.printStackTrace();
                return 2;
            }
        }

        @Override
        protected void onPostExecute(Integer resp){
            progress.dismiss();
              if (resp == 0 ){
                   Intent main = new Intent(this.activity, MainActivity.class);
                   startActivity(main);
                   finish();
               } else if (resp == 1 ){
                  Snackbar snak = Snackbar.make(activity.findViewById(R.id.login_layout),
                                                "JSON Format Error",
                                                10000);

                  snak.show();


              } else if (resp == 2 ) {
                  Snackbar snak = Snackbar.make(activity.findViewById(R.id.login_layout),
                          "Server URL Invalid",
                          10000);

                  snak.show();

              }  else if (resp == 3 ) {
                  Snackbar snak = Snackbar.make(activity.findViewById(R.id.login_layout),
                          "Check Username & Password",
                          10000);

                  snak.show();

              }else {
                  Snackbar snak = Snackbar.make(activity.findViewById(R.id.login_layout),
                          "Nggak tau apa ini errornya ",
                          10000);

                  snak.show();

              }
        }
    }
}
