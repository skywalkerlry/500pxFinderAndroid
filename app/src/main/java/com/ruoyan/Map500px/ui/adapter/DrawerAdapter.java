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

public class DrawerAdapter
        extends RecyclerView.Adapter
        <DrawerAdapter.ListItemViewHolder> {

    private List<String> thumbnailUrlList;
    ImageLoader imageLoader = RequestManager.getInstance().getImageLoader();


    public DrawerAdapter(List<String> thumbnailUrlList) {

        this.thumbnailUrlList = thumbnailUrlList;
    }

    public String getItem(int position) {
        return thumbnailUrlList.get(position);
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
        String thumbnailUrl = thumbnailUrlList.get(position);
        if (imageLoader == null)
            imageLoader = RequestManager.getInstance().getImageLoader();
        viewHolder.order.setText(Integer.toString(position+1));
        viewHolder.thumbNail.setImageUrl(thumbnailUrl,imageLoader);
        viewHolder.thumbNail.setDefaultImageResId(R.drawable.default_thumbnail);
    }

    @Override
    public int getItemCount() {
        return thumbnailUrlList.size();
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
