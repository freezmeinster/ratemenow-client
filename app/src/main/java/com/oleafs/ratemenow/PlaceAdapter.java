package com.oleafs.ratemenow;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by brambut on 7/2/2017.
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder> {

    private Context mContext;
    private List<Place> placeList;

    public PlaceAdapter(Context mContext, List<Place> placeList) {
        this.mContext = mContext;
        this.placeList = placeList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,  owner;
        public ImageView picture;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.place_name);
            owner = (TextView) view.findViewById(R.id.place_owner);
            picture = (ImageView) view.findViewById(R.id.place_picture);
        }
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
            final Place place = placeList.get(position);
        holder.name.setText(place.getName());
        holder.owner.setText(place.getOwner());
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(mContext, RateActivity.class);
                intent.putExtra("place", place);
                mContext.startActivity(intent);
            }
        });
        String pic_url = mContext.getString(R.string.url)+place.getPicture();
        Glide.with(mContext)
                .load(pic_url)
                .placeholder(R.drawable.jar_loading)
                .error(R.drawable.shield_error_icon)
                .fitCenter()
                .override(640,480)
                .into(holder.picture);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.placelist_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public void clear(){
        placeList.clear();
        notifyDataSetChanged();
    }
}
