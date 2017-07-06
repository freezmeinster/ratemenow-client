package com.oleafs.ratemenow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.oleafs.ratemenow.utils.Config;

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
    private Config config;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("REQ CODE= " + requestCode + "RESULT_CODE= " + resultCode );
        if (requestCode == 1) {
            boolean is_rated = data.getBooleanExtra("is_rated", false);
            int obj_id = data.getIntExtra("obj_id", 0);
            placeList.get(obj_id).setIsRated(is_rated);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            startLogin();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLogin();
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        this.activity = this;
        config = new Config(getApplicationContext());

        placeList = new ArrayList<>();
        adapter = new PlaceAdapter(this, placeList);

        adapter.clear();

        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        swipelayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipelayout.setDistanceToTriggerSync(200);
        swipelayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                adapter.clear();
                new OkHttpHandler(activity).execute(config.getApiUrl() + "place/");
            }
        });

        new OkHttpHandler(this).execute(config.getApiUrl() + "place/");

    }

    private void startLogin(){
        config.clearConfig();
        Intent logout = new Intent(this, LoginActivity.class);
        startActivity(logout);
    }

    private void checkLogin(){
        config = new Config(getApplicationContext());
        if(config.getToken().equals("")){
            startLogin();
        }
    }

    private class OkHttpHandler extends AsyncTask<String, Integer, String> {
        private ProgressDialog progress;
        private MainActivity activity;
        OkHttpClient client = new OkHttpClient();

        private OkHttpHandler(MainActivity activity){
            this.activity = activity;
            progress = new ProgressDialog(this.activity);
            progress.setMessage("Fetching Place, Please Wait !!!");
            progress.setIndeterminate(false);
        }

        protected  void onPreExecute(){
            if(!swipelayout.isActivated()) {
                progress.show();
            }
        }

        protected String doInBackground(String...params) {

            Request.Builder builder = new Request.Builder();
            builder.url(params[0])
                    .addHeader("Authorization", "Token " + config.getToken());
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
                System.out.println("response-depan" +s);
                System.out.println("setting-depan"+getString(R.string.invalid_token_message));
                if (s.equals(getString(R.string.invalid_token_message))) {
                    Toast toast = Toast.makeText(this.activity, "Session Expire", Toast.LENGTH_LONG);
                    toast.show();
                } else {
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
