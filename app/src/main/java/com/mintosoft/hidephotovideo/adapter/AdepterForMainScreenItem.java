package com.mintosoft.hidephotovideo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mintosoft.hidephotovideo.R;

public class AdepterForMainScreenItem extends BaseAdapter {

    Context context;
    LayoutInflater inflater;
    ImageView imageForOrignal;
    TextView textView;
    private String[] image_name;
    private Integer[] image_orignal;

    public AdepterForMainScreenItem(Context context, String[] item_name, Integer[] item_image) {
        this.context = context;
        this.image_name = item_name;
        this.image_orignal = item_image;
    }

    @Override
    public int getCount() {
        return image_name.length;
    }

    @Override
    public Object getItem(int position) {
        return image_name[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.items_grid_main, parent, false);
        }
        imageForOrignal = (ImageView) convertView.findViewById(R.id.image_orignal);
        textView = (TextView) convertView.findViewById(R.id.text);
        imageForOrignal.setImageResource(image_orignal[position]);
        textView.setText(image_name[position]);
        return convertView;
    }
}
