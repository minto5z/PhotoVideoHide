package com.mintosoft.hidephotovideo.picker.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.mintosoft.hidephotovideo.picker.helper.Constants;
import com.mintosoft.hidephotovideo.picker.model.Image;

import java.util.ArrayList;

public abstract class VideoPicker {

    private int mode;
    private int limit;
    private boolean showCamera;
    private String folderTitle;
    private String imageTitle;
    private ArrayList<Image> selectedImages;
    private boolean folderMode;
    private String imageDirectory;

    public abstract void start(int requestCode);

    public static class ImagePickerWithActivity extends VideoPicker {

        private Activity activity;

        public ImagePickerWithActivity(Activity activity) {
            this.activity = activity;
            init(activity);
        }

        @Override
        public void start(int requestCode) {
            Intent intent = getIntent(activity);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static class ImagePickerWithFragment extends VideoPicker {

        private Fragment fragment;

        public ImagePickerWithFragment(Fragment fragment) {
            this.fragment = fragment;
            init(fragment.getActivity());
        }

        @Override
        public void start(int requestCode) {
            Intent intent = getIntent(fragment.getActivity());
            fragment.startActivityForResult(intent, requestCode);
        }
    }


    public void init(Activity activity) {
        this.mode = VideoPickerActivity.MODE_MULTIPLE;
        this.limit = Constants.MAX_LIMIT;
        this.showCamera = true;
        this.folderTitle = "Folder";
        this.imageTitle = "Tap to select images";
        this.selectedImages = new ArrayList<>();
        this.folderMode = false;
        this.imageDirectory = "Camera";
    }


    public static ImagePickerWithActivity create(Activity activity) {
        return new ImagePickerWithActivity(activity);
    }

    public static ImagePickerWithFragment create(Fragment fragment) {
        return new ImagePickerWithFragment(fragment);
    }

    public VideoPicker single() {
        mode = VideoPickerActivity.MODE_SINGLE;
        return this;
    }

    public VideoPicker multi() {
        mode = VideoPickerActivity.MODE_MULTIPLE;
        return this;
    }


    public VideoPicker limit(int count) {
        limit = count;
        return this;
    }

    public VideoPicker showCamera(boolean show) {
        showCamera = show;
        return this;
    }

    public VideoPicker folderTitle(String title) {
        this.folderTitle = title;
        return this;
    }

    public VideoPicker imageTitle(String title) {
        this.imageTitle = title;
        return this;
    }

    public VideoPicker origin(ArrayList<Image> images) {
        selectedImages = images;
        return this;
    }

    public VideoPicker folderMode(boolean folderMode) {
        this.folderMode = folderMode;
        return this;
    }

    public VideoPicker imageDirectory(String directory) {
        this.imageDirectory = directory;
        return this;
    }

    public Intent getIntent(Activity activity) {
        Intent intent = new Intent(activity, VideoPickerActivity.class);
        intent.putExtra(VideoPickerActivity.INTENT_EXTRA_MODE, mode);
        intent.putExtra(VideoPickerActivity.INTENT_EXTRA_LIMIT, limit);
        intent.putExtra(VideoPickerActivity.INTENT_EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(VideoPickerActivity.INTENT_EXTRA_FOLDER_TITLE, folderTitle);
        intent.putExtra(VideoPickerActivity.INTENT_EXTRA_IMAGE_TITLE, imageTitle);
        intent.putExtra(VideoPickerActivity.INTENT_EXTRA_SELECTED_IMAGES, selectedImages);
        intent.putExtra(VideoPickerActivity.INTENT_EXTRA_FOLDER_MODE, folderMode);
        intent.putExtra(VideoPickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY, imageDirectory);
        return intent;
    }


}
