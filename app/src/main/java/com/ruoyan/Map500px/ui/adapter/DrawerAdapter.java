package com.ruoyan.map500px.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ruoyan.map500px.R;
import com.ruoyan.map500px.data.RequestManager;

import java.util.List;
import java.util.Map;

public class DrawerAdapter
        extends RecyclerView.Adapter
        <DrawerAdapter.ListItemViewHolder> {

    private List<Map<String,Object>> photoInfoList;
    ImageLoader imageLoader = RequestManager.getInstance().getImageLoader();


    public DrawerAdapter(List<Map<String,Object>> photoInfoList) {

        this.photoInfoList = photoInfoList;
    }

    public Map<String,Object> getItem(int position) {
        return photoInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.drawer_listitem, viewGroup, false);
        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListItemViewHolder viewHolder, int position) {
        Map<String,Object> photoInfo = photoInfoList.get(position);
        if (imageLoader == null)
            imageLoader = RequestManager.getInstance().getImageLoader();
        viewHolder.order.setText(Integer.toString(position));
        viewHolder.thumbNail.setImageUrl(photoInfo.get("image_url").toString(),imageLoader);
    }

    @Override
    public int getItemCount() {
        return photoInfoList.size();
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        TextView order;
        NetworkImageView thumbNail;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            order = (TextView) itemView.findViewById(R.id.photo_order);
            thumbNail = (NetworkImageView) itemView.findViewById(R.id.thumb_nail);
        }
    }
}
