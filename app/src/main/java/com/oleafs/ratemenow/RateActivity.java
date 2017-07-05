package com.oleafs.ratemenow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.BoringLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.oleafs.ratemenow.utils.Config;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RateActivity extends AppCompatActivity {

    public RatingBar rate_bar;
    public EditText comment;
    public RateActivity activity;
    public Button button;
    public TextView thanks;
    public Place place;
    public Config config;
    public int obj_id;
    public boolean is_rated;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("is_rated", is_rated);
        intent.putExtra("obj_id", obj_id);
        setResult(Activity.RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        activity = this;
        config = new Config(getApplicationContext());

        this.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        place = (Place) getIntent().getSerializableExtra("place");
        obj_id = getIntent().getIntExtra("obj_id", 0);
        is_rated = place.getIs_rated();

        TextView name = (TextView) findViewById(R.id.rate_place_name);
        name.setText(place.getName());

        TextView description = (TextView) findViewById(R.id.rate_place_description);
        description.setText(place.getDescription());

        TextView owner = (TextView) findViewById(R.id.rate_place_owner);
        owner.setText(place.getOwner());

        thanks = (TextView) findViewById(R.id.rate_place_thanks);

        rate_bar = (RatingBar) findViewById(R.id.rate_place);
        comment = (EditText) findViewById(R.id.rate_comment);
        button = (Button) findViewById(R.id.rate_button);

        rate_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating > 0.0 ){
                    if (button.getVisibility() == View.INVISIBLE ){
                        button.setVisibility(View.VISIBLE);
                    }
                } else {
                    button.setVisibility(View.INVISIBLE);
                }
            }
        });


        if(place.getIs_rated()){
            rate_bar.setVisibility(View.INVISIBLE);
            comment.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
            thanks.setVisibility(View.VISIBLE);
        }

        ImageView picture = (ImageView) findViewById(R.id.rate_place_picture);
        String pic_url = getString(R.string.url)+place.getPicture();
        System.out.println(pic_url);
        Glide.with(this)
                .load(pic_url)
                .placeholder(R.drawable.jar_loading)
                .error(R.drawable.shield_error_icon)
                .fitCenter()
                .into(picture);

    }

    public void sendRate(View v){
        final int start = (int) rate_bar.getRating();
        SendRateHandler handler = new SendRateHandler(this);
        handler.execute(place.getId(),
                        Integer.toString(start),
                        comment.getText().toString());
    }

    private class SendRateHandler extends AsyncTask<String, Integer, Boolean> {


        private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        private ProgressDialog progress;
        private RateActivity activity;
        private String base_url;
        OkHttpClient client = new OkHttpClient();

        private SendRateHandler(RateActivity activity){
            this.activity = activity;
            progress = new ProgressDialog(this.activity);
            progress.setMessage("Sending your rate, please wait !!");
            progress.setIndeterminate(true);
            base_url = config.getApiUrl() + "/place/";
        }


        @Override
        protected void onPreExecute(){
            progress.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String url = base_url + params[0] + "/rate/";

            JSONObject req_json = new JSONObject();
            try {
                req_json.put("rate_star", Integer.parseInt(params[1]));
                req_json.put("rate_comment", params[2]);
            } catch (JSONException e){
                e.printStackTrace();
            }

            Request.Builder builder = new Request.Builder();
            builder.url(url)
                    .post(RequestBody.create(JSON, req_json.toString()))
                    .addHeader("Authorization", "Token " + config.getToken());
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                try {
                    JSONObject json_resp = new JSONObject(response.body().string());
                    if (json_resp.getString("status").equals("success")){
                        is_rated = true;
                        return true;
                    } else {
                        return false;
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean res){
            progress.dismiss();
            if (res) {
                activity.rate_bar.setVisibility(View.INVISIBLE);
                activity.comment.setVisibility(View.INVISIBLE);
                activity.button.setVisibility(View.INVISIBLE);
                activity.thanks.setVisibility(View.VISIBLE);
                Toast.makeText(activity, R.string.rate_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, R.string.rate_error, Toast.LENGTH_LONG).show();
            }

        }
    }
}
