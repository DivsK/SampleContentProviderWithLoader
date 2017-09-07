package com.kellton.samplecontentproviderwithloader;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * <h1><font color="orange">MediaRecyclerViewAdapter</font></h1>
 * Adapter class for setting fetched images to the recycler grid view.
 *
 * @author Divya Khanduri
 */
class MediaRecyclerViewAdapter extends RecyclerView.Adapter<MediaRecyclerViewAdapter.ViewHolder> {
    private ArrayList<Bitmap> mMediaImages;

    MediaRecyclerViewAdapter(ArrayList<Bitmap> mediaImages) {

        mMediaImages = mediaImages;
    }

    void setmMediaImages(ArrayList<Bitmap> mMediaImages) {
        this.mMediaImages = mMediaImages;
    }

    @Override
    public MediaRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_media, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MediaRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.ivMedia.setImageBitmap(mMediaImages.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != mMediaImages ? mMediaImages.size() : 0);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMedia;

        ViewHolder(View itemView) {
            super(itemView);
            ivMedia = itemView.findViewById(R.id.iv_media);
        }
    }
}
