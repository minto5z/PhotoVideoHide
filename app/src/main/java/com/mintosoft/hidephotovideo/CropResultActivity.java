package com.mintosoft.hidephotovideo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CropResultActivity extends Activity {

    private static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";

    @Bind(R.id.result_image)
    ImageView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crop_result);
        ButterKnife.bind(this);

        String filePath = getIntent().getStringExtra(EXTRA_FILE_PATH);
        File imageFile = new File(filePath);

        Picasso.with(this)
                .load(imageFile)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(resultView);

        // Or Glide
        //Glide.with(this)
        //        .load(imageFile)
        //        .diskCacheStrategy(DiskCacheStrategy.NONE)
        //        .skipMemoryCache(true)
        //        .into(resultView);

        // Or Android-Universal-Image-Loader
        //DisplayImageOptions options = new DisplayImageOptions.Builder()
        //        .cacheInMemory(false)
        //        .cacheOnDisk(false)
        //        .build();
        //ImageLoader.getInstance().displayImage("file://" + filePath, resultView, options);
    }

    static void startUsing(File croppedPath, Activity activity) {
        Intent intent = new Intent(activity, CropResultActivity.class);
        intent.putExtra(EXTRA_FILE_PATH, croppedPath.getPath());
        activity.startActivity(intent);
    }
}
