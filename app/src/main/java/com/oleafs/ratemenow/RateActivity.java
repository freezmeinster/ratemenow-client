package com.oleafs.ratemenow;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class RateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        Place place = (Place) getIntent().getSerializableExtra("place");

        TextView name = (TextView) findViewById(R.id.rate_place_name);
        name.setText(place.getName());

        TextView description = (TextView) findViewById(R.id.rate_place_description);
        description.setText(place.getDescription());

        TextView owner = (TextView) findViewById(R.id.rate_place_owner);
        owner.setText(place.getOwner());

        RatingBar rate_bar = (RatingBar) findViewById(R.id.rate_place);
        EditText comment = (EditText) findViewById(R.id.rate_comment);
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
}
