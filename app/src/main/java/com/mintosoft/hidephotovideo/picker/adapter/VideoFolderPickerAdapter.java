package com.mintosoft.hidephotovideo.picker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.FileDescriptorBitmapDecoder;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.mintosoft.hidephotovideo.R;
import com.mintosoft.hidephotovideo.picker.listeners.OnFolderClickListener;
import com.mintosoft.hidephotovideo.picker.model.Folder;

import java.util.List;

public class VideoFolderPickerAdapter extends RecyclerView.Adapter<VideoFolderPickerAdapter.FolderViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private final OnFolderClickListener folderClickListener;

    private List<Folder> folders;

    public VideoFolderPickerAdapter(Context context, OnFolderClickListener folderClickListener) {
        this.context = context;
        this.folderClickListener = folderClickListener;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_folder, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FolderViewHolder holder, int position) {

        final Folder folder = folders.get(position);

       /* holder.thumb = ThumbnailUtils.createVideoThumbnail(folder.getImages().get(0).getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
        //Glide.with(context).load(viewHolder.thumb).centerCrop().crossFade().into(viewHolder.imageView);
        holder.image.setImageBitmap(holder.thumb);*/
        /*Glide.with(context)
                .load(folder.getImages().get(0).getPath())
                .placeholder(R.drawable.folder_placeholder)
                .error(R.drawable.folder_placeholder)
                .into(holder.image);
*/

        BitmapPool bitmapPool = Glide.get(context).getBitmapPool();
        int microSecond = 6000000;// 6th second as an example
        VideoBitmapDecoder videoBitmapDecoder = new VideoBitmapDecoder(microSecond);
        FileDescriptorBitmapDecoder fileDescriptorBitmapDecoder = new FileDescriptorBitmapDecoder(videoBitmapDecoder, bitmapPool, DecodeFormat.PREFER_ARGB_8888);
        Glide.with(context)
                .load(folder.getImages().get(0).getPath())
                .asBitmap()
                .centerCrop()
                .override(100,100)// Example
                .videoDecoder(fileDescriptorBitmapDecoder)
                .into( holder.image);

        holder.name.setText(folders.get(position).getFolderName());
        holder.number.setText(String.valueOf(folders.get(position).getImages().size()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (folderClickListener != null)
                    folderClickListener.onFolderClick(folder);
            }
        });
    }

    public void setData(List<Folder> folders) {
        this.folders = folders;

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    public static class FolderViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private TextView number;
        private Bitmap thumb;

        public FolderViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            number = (TextView) itemView.findViewById(R.id.tv_number);
        }
    }

}
