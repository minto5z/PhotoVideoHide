package com.mintosoft.hidephotovideo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.mintosoft.hidephotovideo.R;

import java.io.File;

public class ImageViewAdapter extends PagerAdapter {

    private Activity _activity;
    private File[] _imagePaths;

    public ImageViewAdapter(Activity activity, File[] imagePaths) {
        this._activity = activity;
        this._imagePaths = imagePaths;
    }

    @Override
    public int getCount() {
        return this._imagePaths.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SubsamplingScaleImageView imgDisplay;

        LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.image_view_item, container, false);

        imgDisplay = (SubsamplingScaleImageView) viewLayout.findViewById(R.id.imgDisplay);
        imgDisplay.setImage(ImageSource.uri(_imagePaths[position].getAbsolutePath()));

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
