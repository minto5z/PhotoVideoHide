package com.mintosoft.hidephotovideo.picker.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mintosoft.hidephotovideo.R;
import com.mintosoft.hidephotovideo.picker.adapter.VideoFolderPickerAdapter;
import com.mintosoft.hidephotovideo.picker.adapter.VideoPickerAdapter;
import com.mintosoft.hidephotovideo.picker.helper.Constants;
import com.mintosoft.hidephotovideo.picker.helper.ImageUtils;
import com.mintosoft.hidephotovideo.picker.listeners.OnFolderClickListener;
import com.mintosoft.hidephotovideo.picker.listeners.OnImageClickListener;
import com.mintosoft.hidephotovideo.picker.model.Folder;
import com.mintosoft.hidephotovideo.picker.model.Image;
import com.mintosoft.hidephotovideo.picker.view.GridSpacingItemDecoration;
import com.mintosoft.hidephotovideo.picker.view.ProgressWheel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoPickerActivity extends AppCompatActivity implements OnImageClickListener {

    private static final String TAG = "ImagePickerActivity";

    public static final int MODE_SINGLE = 1;
    public static final int MODE_MULTIPLE = 2;

    public static final String INTENT_EXTRA_SELECTED_IMAGES = "selectedImages";
    public static final String INTENT_EXTRA_LIMIT = "limit";
    public static final String INTENT_EXTRA_SHOW_CAMERA = "showCamera";
    public static final String INTENT_EXTRA_MODE = "mode";
    public static final String INTENT_EXTRA_FOLDER_MODE = "folderMode";
    public static final String INTENT_EXTRA_FOLDER_TITLE = "folderTitle";
    public static final String INTENT_EXTRA_IMAGE_TITLE = "imageTitle";
    public static final String INTENT_EXTRA_IMAGE_DIRECTORY = "imageDirectory";


    private List<Folder> folders;
    private ArrayList<Image> images;
    private String currentImagePath;
    private String imageDirectory;

    private ArrayList<Image> selectedImages;
    private boolean showCamera;
    private int mode;
    private boolean folderMode;
    private int limit;
    private String folderTitle, imageTitle;

    private ActionBar actionBar;

    private MenuItem menuDone, menuCamera;
    private final int menuDoneId = 100;
    private final int menuCameraId = 101;

    private RelativeLayout mainLayout;
    private ProgressWheel progressBar;
    private TextView emptyTextView;
    private RecyclerView recyclerView;

    private GridLayoutManager layoutManager;
    private GridSpacingItemDecoration itemOffsetDecoration;

    private int imageColumns;
    private int folderColumns;

    private VideoPickerAdapter imageAdapter;
    private VideoFolderPickerAdapter folderAdapter;

    private ContentObserver observer;
    private Handler handler;
    private Thread thread;

    private final String[] projection = new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

    private Parcelable foldersState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        mainLayout = (RelativeLayout) findViewById(R.id.main);
        progressBar = (ProgressWheel) findViewById(R.id.progress_bar);
        emptyTextView = (TextView) findViewById(R.id.tv_empty_images);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        /** Get extras */
        limit = intent.getIntExtra(VideoPickerActivity.INTENT_EXTRA_LIMIT, Constants.MAX_LIMIT);
        mode = intent.getIntExtra(VideoPickerActivity.INTENT_EXTRA_MODE, VideoPickerActivity.MODE_MULTIPLE);
        folderMode = intent.getBooleanExtra(VideoPickerActivity.INTENT_EXTRA_FOLDER_MODE, false);

        if (intent.hasExtra(INTENT_EXTRA_FOLDER_TITLE)) {
            folderTitle = intent.getStringExtra(VideoPickerActivity.INTENT_EXTRA_FOLDER_TITLE);
        } else {
            folderTitle = "Folder";
        }

        if (intent.hasExtra(INTENT_EXTRA_IMAGE_TITLE)) {
            imageTitle = intent.getStringExtra(VideoPickerActivity.INTENT_EXTRA_IMAGE_TITLE);
        } else {
            imageTitle = "Tap to select images";
        }

        imageDirectory = intent.getStringExtra(VideoPickerActivity.INTENT_EXTRA_IMAGE_DIRECTORY);
        if (imageDirectory == null || TextUtils.isEmpty(imageDirectory)) {
            imageDirectory = "Camera";
        }

        showCamera = intent.getBooleanExtra(VideoPickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        if (mode == VideoPickerActivity.MODE_MULTIPLE && intent.hasExtra(VideoPickerActivity.INTENT_EXTRA_SELECTED_IMAGES)) {
            selectedImages = intent.getParcelableArrayListExtra(VideoPickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
        }
        if (selectedImages == null)
            selectedImages = new ArrayList<>();
        images = new ArrayList<>();


        /** Set activity title */
        if (actionBar != null) {
            actionBar.setTitle(folderMode ? folderTitle : imageTitle);
        }

        /** Init folder and image adapter */
        imageAdapter = new VideoPickerAdapter(this, images, selectedImages, this);
        folderAdapter = new VideoFolderPickerAdapter(this, new OnFolderClickListener() {
            @Override
            public void onFolderClick(Folder bucket) {
                foldersState = recyclerView.getLayoutManager().onSaveInstanceState();
                setImageAdapter(bucket.getImages());
            }
        });

        orientationBasedUI(getResources().getConfiguration().orientation);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataWithPermission();
    }

    private void setImageAdapter(ArrayList<Image> images) {
        imageAdapter.setData(images);
        setItemDecoration(imageColumns);
        recyclerView.setAdapter(imageAdapter);
        updateTitle();
    }

    private void setFolderAdapter() {
        folderAdapter.setData(folders);
        setItemDecoration(folderColumns);
        recyclerView.setAdapter(folderAdapter);

        if (foldersState != null) {
            layoutManager.setSpanCount(folderColumns);
            recyclerView.getLayoutManager().onRestoreInstanceState(foldersState);
        }
        updateTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (menu.findItem(menuCameraId) == null) {
            menuCamera = menu.add(Menu.NONE, menuCameraId, 1, "CAMERA");
            menuCamera.setIcon(R.drawable.ic_camera_white);
            menuCamera.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menuCamera.setVisible(showCamera);
        }

        if (menu.findItem(menuDoneId) == null) {
            menuDone = menu.add(Menu.NONE, menuDoneId, 2, "DONE");
            menuDone.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        updateTitle();

        return true;
    }

    /**
     * Handle option menu's click event
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (id == menuDoneId) {
            if (selectedImages != null && selectedImages.size() > 0) {

                /** Scan selected images which not existed */
                for (int i = 0; i < selectedImages.size(); i++) {
                    Image image = selectedImages.get(i);
                    File file = new File(image.getPath());
                    if (!file.exists()) {
                        selectedImages.remove(i);
                        i--;
                    }
                }

                Intent data = new Intent();
                data.putParcelableArrayListExtra(VideoPickerActivity.INTENT_EXTRA_SELECTED_IMAGES, selectedImages);
                setResult(RESULT_OK, data);
                finish();
            }
            return true;
        }
        if (id == menuCameraId) {
            //captureImage();
            captureImageWithPermission();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Config recyclerView when configuration changed
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        orientationBasedUI(newConfig.orientation);
    }

    /**
     * Set item size, column size base on the screen orientation
     */
    private void orientationBasedUI(int orientation) {
        imageColumns = orientation == Configuration.ORIENTATION_PORTRAIT ? 3 : 5;
        folderColumns = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 4;

        int columns = isDisplayingFolderView() ? folderColumns : imageColumns;
        layoutManager = new GridLayoutManager(this, columns);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        setItemDecoration(columns);
    }

    /**
     * Set item decoration
     */
    private void setItemDecoration(int columns) {
        layoutManager.setSpanCount(columns);
        if (itemOffsetDecoration != null)
            recyclerView.removeItemDecoration(itemOffsetDecoration);
        itemOffsetDecoration = new GridSpacingItemDecoration(columns, getResources().getDimensionPixelSize(R.dimen.item_padding), false);
        recyclerView.addItemDecoration(itemOffsetDecoration);
    }


    /**
     * Check permission
     */
    private void getDataWithPermission() {
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED)
            getData();
        else
            requestWriteExternalPermission();
    }

    /**
     * Get data
     */
    private void getData() {
        abortLoading();

        ImageLoaderRunnable runnable = new ImageLoaderRunnable();
        thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Request for permission
     * If permission denied or app is first launched, request for permission
     * If permission denied and user choose 'Nerver Ask Again', show snackbar with an action that navigate to app settings
     */
    private void requestWriteExternalPermission() {
        Log.w(TAG, "Write External permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            if (!isPermissionRequested(Constants.PREF_WRITE_EXTERNAL_STORAGE_REQUESTED)) {
                ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                setPermissionRequested(Constants.PREF_WRITE_EXTERNAL_STORAGE_REQUESTED);
            } else {
                Snackbar snackbar = Snackbar.make(mainLayout, "Please grant storage permission to select images",
                        Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAppSettings();
                    }
                });
                snackbar.show();
            }
        }

    }


    private void requestCameraPermission() {
        Log.w(TAG, "Write External permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_CAMERA);
        } else {
            if (!isPermissionRequested(Constants.PREF_CAMERA_REQUESTED)) {
                ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_REQUEST_CAMERA);
                setPermissionRequested(Constants.PREF_CAMERA_REQUESTED);
            } else {
                Snackbar snackbar = Snackbar.make(mainLayout, "Please grant camera permission to capture image",
                        Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openAppSettings();
                    }
                });
                snackbar.show();
            }
        }
    }

    /**
     * Handle permission results
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Write External permission granted");
                    getData();
                    return;
                }
                Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
                finish();
            }
            case Constants.PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Camera permission granted");
                    captureImage();
                    return;
                }
                Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                        " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
                break;
            }
            default: {
                Log.d(TAG, "Got unexpected permission result: " + requestCode);
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
            }
        }
    }

    /**
     * Open app settings screen
     */
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Set a permission is requested
     */
    private void setPermissionRequested(String permission) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(permission, true);
        editor.apply();
    }

    /**
     * Check if a permission is requestted or not (false by default)
     */
    private boolean isPermissionRequested(String permission) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean(permission, false);
    }

    @Override
    public void onClick(View view, int position) {
        clickImage(position);
    }

    /**
     * Handle image selection event: add or remove selected image, change title
     */
    private void clickImage(int position) {
        int selectedItemPosition = selectedImagePosition(images.get(position));
        if (mode == VideoPickerActivity.MODE_MULTIPLE) {
            if (selectedItemPosition == -1) {
                if (selectedImages.size() < limit) {
                    imageAdapter.addSelected(images.get(position));
                } else {
                    Toast.makeText(this, "Image selection limit", Toast.LENGTH_SHORT).show();
                }
            } else {
                imageAdapter.removeSelectedPosition(selectedItemPosition, position);
            }
        } else {
            if (selectedItemPosition != -1)
                imageAdapter.removeSelectedPosition(selectedItemPosition, position);
            else {
                if (selectedImages.size() > 0) {
                    imageAdapter.removeAllSelectedSingleClick();
                }
                imageAdapter.addSelected(images.get(position));
            }
        }
        updateTitle();
    }

    private int selectedImagePosition(Image image) {
        for (int i = 0; i < selectedImages.size(); i++) {
            if (selectedImages.get(i).getPath().equals(image.getPath())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Check if the captured image is stored successfully
     * Then reload data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_CAPTURE) {
            if (resultCode == RESULT_OK && currentImagePath != null) {
                Uri imageUri = Uri.parse(currentImagePath);
                if (imageUri != null) {
                    MediaScannerConnection.scanFile(this,
                            new String[]{imageUri.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.v(TAG, "File " + path + " was scanned successfully: " + uri);
                                    getDataWithPermission();
                                }
                            });
                }
            }
        }
    }

    /**
     * Request for camera permission
     */
    private void captureImageWithPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Log.w(TAG, "Camera permission is not granted. Requesting permission");
                requestCameraPermission();
            }
        } else {
            captureImage();
        }
    }

    /**
     * Start camera intent
     * Create a temporary file and pass file Uri to camera intent
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File imageFile = ImageUtils.createImageFile(imageDirectory);
            if (imageFile != null) {
                String authority = getPackageName() + ".fileprovider";
                Uri uri = FileProvider.getUriForFile(this, authority, imageFile);
                currentImagePath = "file:" + imageFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, Constants.REQUEST_CODE_CAPTURE);
            } else {
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No camera found", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Init handler to handle loading data results
     */
    @Override
    protected void onStart() {
        super.onStart();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Constants.FETCH_STARTED: {
                        showLoading();
                        break;
                    }
                    case Constants.FETCH_COMPLETED: {
                        ArrayList<Image> temps = new ArrayList<>();
                        temps.addAll(selectedImages);

                        ArrayList<Image> newImages = new ArrayList<>();
                        newImages.addAll(images);


                        if (folderMode) {
                            setFolderAdapter();
                            if (folders.size() != 0)
                                hideLoading();
                            else
                                showEmpty();

                        } else {
                            setImageAdapter(newImages);
                            if (images.size() != 0)
                                hideLoading();
                            else
                                showEmpty();
                        }

                        break;
                    }
                    default: {
                        super.handleMessage(msg);
                    }
                }
            }
        };
        observer = new ContentObserver(handler) {
            @Override
            public void onChange(boolean selfChange) {
                getData();
            }
        };
        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer);
    }

    /**
     * Stop loading data task
     */
    private void abortLoading() {
        if (thread == null)
            return;
        if (thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if displaying folders view
     */
    private boolean isDisplayingFolderView() {
        return (folderMode &&
                (recyclerView.getAdapter() == null || recyclerView.getAdapter() instanceof VideoFolderPickerAdapter));
    }

    /**
     * Update activity title
     * If we're displaying folder, set folder title
     * If we're displaying images, show number of selected images
     */
    private void updateTitle() {
        if (menuDone != null && menuCamera != null) {
            if (isDisplayingFolderView()) {
                actionBar.setTitle(folderTitle);
                menuDone.setVisible(false);
            } else {
                if (selectedImages.size() == 0) {
                    actionBar.setTitle(imageTitle);
                    if (menuDone != null)
                        menuDone.setVisible(false);
                } else {
                    if (mode == VideoPickerActivity.MODE_MULTIPLE) {
                        if (limit == Constants.MAX_LIMIT)
                            actionBar.setTitle(selectedImages.size() + " selected");
                        else
                            actionBar.setTitle( selectedImages.size()+"/"+ limit + " selected");
                    }
                    if (menuDone != null)
                        menuDone.setVisible(true);
                }
            }
        }
    }

    /**
     * Show progessbar when loading data
     */
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.GONE);
    }

    /**
     * Hide progressbar when data loaded
     */
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyTextView.setVisibility(View.GONE);
    }

    /**
     * Show empty data
     */
    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        abortLoading();

        getContentResolver().unregisterContentObserver(observer);

        observer = null;

        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    /**
     * Loading data task
     */
    private class ImageLoaderRunnable implements Runnable {

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            Message message;
            if (recyclerView.getAdapter() == null) {
                /*
                If the adapter is null, this is first time this activity's view is
                being shown, hence send FETCH_STARTED message to show progress bar
                while images are loaded from phone
                 */
                message = handler.obtainMessage();
                message.what = Constants.FETCH_STARTED;
                message.sendToTarget();
            }

            if (Thread.interrupted()) {
                return;
            }

            Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null, MediaStore.Images.Media.DATE_ADDED);

            if (cursor == null) {
                message = handler.obtainMessage();
                message.what = Constants.ERROR;
                message.sendToTarget();
                return;
            }

            ArrayList<Image> temp = new ArrayList<>(cursor.getCount());
            File file;
            folders = new ArrayList<>();

            if (cursor.moveToLast()) {
                do {
                    if (Thread.interrupted()) {
                        return;
                    }

                    long id = cursor.getLong(cursor.getColumnIndex(projection[0]));
                    String name = cursor.getString(cursor.getColumnIndex(projection[1]));
                    String path = cursor.getString(cursor.getColumnIndex(projection[2]));
                    String bucket = cursor.getString(cursor.getColumnIndex(projection[3]));

                    file = new File(path);
                    if (file.exists()) {
                        Image image = new Image(id, name, path, false);
                        temp.add(image);

                        if (folderMode) {
                            Folder folder = getFolder(bucket);
                            if (folder == null) {
                                folder = new Folder(bucket);
                                folders.add(folder);
                            }

                            folder.getImages().add(image);
                        }
                    }

                } while (cursor.moveToPrevious());
            }
            cursor.close();
            if (images == null) {
                images = new ArrayList<>();
            }
            images.clear();
            images.addAll(temp);

            if (handler != null) {
                message = handler.obtainMessage();
                message.what = Constants.FETCH_COMPLETED;
                message.sendToTarget();
            }

            Thread.interrupted();

        }
    }

    /**
     * Return folder base on folder name
     */
    public Folder getFolder(String name) {
        for (Folder folder : folders) {
            if (folder.getFolderName().equals(name)) {
                return folder;
            }
        }
        return null;
    }

    /**
     * When press back button, show folders if view is displaying images
     */
    @Override
    public void onBackPressed() {
        if (folderMode && !isDisplayingFolderView()) {
            setFolderAdapter();
            return;
        }

        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
