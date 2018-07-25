package com.mintosoft.hidephotovideo.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.mintosoft.hidephotovideo.R;

import java.io.File;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private File[] mThumbIds;
    private String mIntentType;
    private SparseBooleanArray mSelectedItemsIds;

    public ImageAdapter(Context context, File[] mThumbIds, String mIntentType) {
        this.context = context;
        this.mThumbIds = mThumbIds;
        this.mIntentType = mIntentType;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderItem viewHolder;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder = new ViewHolderItem();
            if (mIntentType.equalsIgnoreCase("image")) {
                convertView = mInflater.inflate(R.layout.gridview_item, null);
            } else if (mIntentType.equalsIgnoreCase("video")) {
                convertView = mInflater.inflate(R.layout.gridview_item, null);
            }

            if (convertView != null) {
                convertView.setTag(viewHolder);
            }

        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        if (mIntentType.equalsIgnoreCase("image")) {
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_gridview_item);
            viewHolder.ic_gridview_item_video_play = (ImageView) convertView.findViewById(R.id.img_gridview_item_video_play);
            Glide.with(context).load(mThumbIds[position].getPath()).centerCrop().crossFade().into(viewHolder.imageView);
        } else if (mIntentType.equalsIgnoreCase("video")) {
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_gridview_item);
            viewHolder.ic_gridview_item_video_play = (ImageView) convertView.findViewById(R.id.img_gridview_item_video_play);
            viewHolder.ic_gridview_item_video_play.setVisibility(View.VISIBLE);
            BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
            int microSecond = 2000000;
            VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
            FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
            Glide.with(context)
                    .load(mThumbIds[position].getPath())
                    .asBitmap()
                    .centerCrop()
                    .override(100, 100)
                    .videoDecoder(fileDescriptorBitmapDecoder)
                    .into(viewHolder.imageView);


        }

        viewHolder.lout_item_flag = (RelativeLayout) convertView.findViewById(R.id.lout_item_flag);
        viewHolder.lout_item_flag.setVisibility(mSelectedItemsIds.get(position) ? View.VISIBLE : View.INVISIBLE);
        return convertView;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class ViewHolderItem {
        ImageView imageView, ic_gridview_item_video_play;
        RelativeLayout lout_item_flag;
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    private void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, true);
        } else {
            mSelectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

}
