package com.oleafs.ratemenow;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class RateActivity extends AppCompatActivity {

    public RatingBar rate_bar;
    public EditText comment;
    public RateActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        activity = this;

        Place place = (Place) getIntent().getSerializableExtra("place");

        TextView name = (TextView) findViewById(R.id.rate_place_name);
        name.setText(place.getName());

        TextView description = (TextView) findViewById(R.id.rate_place_description);
        description.setText(place.getDescription());

        TextView owner = (TextView) findViewById(R.id.rate_place_owner);
        owner.setText(place.getOwner());

        rate_bar = (RatingBar) findViewById(R.id.rate_place);
        comment = (EditText) findViewById(R.id.rate_comment);
        Button button = (Button) findViewById(R.id.rate_button);


        if(place.getIs_rated()){
            rate_bar.setVisibility(View.INVISIBLE);
            comment.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
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
        Toast.makeText(activity, "Ini mambu" + Integer.toString(start), Toast.LENGTH_LONG).show();
    }

    private class SendRateHandler extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            return null;
        }
    }
}
