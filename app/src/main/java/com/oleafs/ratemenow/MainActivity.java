package com.oleafs.ratemenow;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipelayout;
    private PlaceAdapter adapter;
    private List<Place> placeList;
    private MainActivity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        this.activity = this;

        placeList = new ArrayList<>();
        adapter = new PlaceAdapter(this, placeList);

        adapter.clear();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        swipelayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipelayout.setDistanceToTriggerSync(200);
        swipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                finish();
                startActivity(getIntent());
            }
        });

        new OkHttpHandler(this).execute(getString(R.string.url)+ "/api/place/");

    }

    private class OkHttpHandler extends AsyncTask<String, Integer, String> {
        private ProgressDialog progress;
        private MainActivity activity;
        OkHttpClient client = new OkHttpClient();

        private OkHttpHandler(MainActivity activity){
            this.activity = activity;
            progress = new ProgressDialog(this.activity);
            progress.setMessage("Fetching Place, Please Wait !!!");
            progress.setIndeterminate(true);
        }

        protected  void onPreExecute(){
            if(!swipelayout.isActivated()) {
                progress.show();
            }
        }

        protected String doInBackground(String...params) {

            Request.Builder builder = new Request.Builder();
            builder.url(params[0])
                    .addHeader("Authorization", "Token " + getString(R.string.token));
            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            placeList.clear();
            if (s != null ) {
                try {
                    JSONArray json = new JSONArray(s);
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jo = json.getJSONObject(i);
                        Place p = new Place(jo.getString("id"),
                                jo.getString("name"),
                                jo.getString("description"),
                                jo.getString("owner"),
                                jo.getString("picture"),
                                jo.getBoolean("is_rated"));
                        placeList.add(p);
                    }

                } catch (JSONException e) {
                    Toast toast = Toast.makeText(this.activity, "Error Parsing JSON", Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(this.activity, "Connection Lost !!", Toast.LENGTH_LONG);
                toast.show();
            }
            adapter.notifyDataSetChanged();
            if(!swipelayout.isActivated()) {
                progress.dismiss();
            }

            if(swipelayout.isRefreshing()){
                swipelayout.setRefreshing(false);
            }
        }
    }

}