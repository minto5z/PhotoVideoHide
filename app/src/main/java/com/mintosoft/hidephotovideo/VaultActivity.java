package com.mintosoft.hidephotovideo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.mintosoft.hidephotovideo.picker.activity.ImagePicker;
import com.mintosoft.hidephotovideo.picker.activity.ImagePickerActivity;
import com.mintosoft.hidephotovideo.picker.activity.VideoPicker;
import com.mintosoft.hidephotovideo.picker.model.Image;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mintosoft.hidephotovideo.adapter.ImageAdapter;
import com.mintosoft.hidephotovideo.utils.FileUtils;
import com.mintosoft.hidephotovideo.utils.NewFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class VaultActivity extends AppCompatActivity {

    Toolbar toolbar;
    String mActivityTitle = "";
    FloatingActionButton fab;
    GridView gridView;
    ImageAdapter mAdapter;
    File[] files;
    File directory;
    String CurrentDir, IntentType, CurrentMainDir;
    int mCurrentPosition = 0;
    ProgressDialog pDialog;

    private ArrayList<Image> images = new ArrayList<>();

    public static final int REQUEST_CODE_PICKER = 2000;
    public static final int REQUEST_CODE_PICKER_FILE = 2001;

    boolean isKitKat = false;

    RelativeLayout lout_no_files;

    ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);

        isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        final AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_id)).build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
            }
        });

        if (this.getIntent().hasExtra("Type")) {
            mActivityTitle = this.getIntent().getStringExtra("Type");
        }

        if (mActivityTitle.equalsIgnoreCase("Image")) {
            CurrentDir = getString(R.string.image_directory);
            CurrentMainDir = getString(R.string.image_directory);
            IntentType = "image";
        } else if (mActivityTitle.equalsIgnoreCase("Video")) {
            CurrentDir = getString(R.string.video_directory);
            CurrentMainDir = getString(R.string.video_directory);
            IntentType = "video";
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mActivityTitle);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        gridView = (GridView) findViewById(R.id.gridview);
        lout_no_files = (RelativeLayout) findViewById(R.id.lout_no_files);

        directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
        files = directory.listFiles();
        mAdapter = new ImageAdapter(this, files, IntentType);
        gridView.setAdapter(mAdapter);
        //gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemSelect(position);
                return true;
            }
        });

        if (files.length <= 0) {
            lout_no_files.setVisibility(View.VISIBLE);
        } else {
            lout_no_files.setVisibility(View.GONE);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mActionMode == null) {
                    mCurrentPosition = i;
                    if (IntentType.equalsIgnoreCase("image")) {
                        Intent viewImage = new Intent(VaultActivity.this, ImageViewActivity.class);
                        viewImage.putExtra("CurrentPosition", mCurrentPosition);
                        startActivity(viewImage);
                    } else {
                        new BottomSheet.Builder(VaultActivity.this).title("Option").sheet(R.menu.action_menu).listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case R.id.menu_open:

                                        Uri mUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", files[mCurrentPosition]);
                                        Intent intent = new Intent();
                                        intent.setAction(android.content.Intent.ACTION_VIEW);
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                            intent.setDataAndType(mUri, getContentResolver().getType(mUri));
                                        } else {
                                            intent.setDataAndType(mUri, getContentResolver().getType(mUri));
                                        }
                                        try {
                                            startActivity(intent);
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(VaultActivity.this, "No application found to open this file.", Toast.LENGTH_LONG).show();
                                        }
                                        break;
                                    case R.id.menu_share:
                                        Uri mUriShare = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", files[mCurrentPosition]);
                                        Intent mIntentShare = new Intent();
                                        mIntentShare.setAction(Intent.ACTION_SEND);
                                        mIntentShare.setType(getContentResolver().getType(mUriShare));
                                        mIntentShare.putExtra(Intent.EXTRA_STREAM, mUriShare);
                                        try {
                                            startActivity(mIntentShare);
                                        } catch (ActivityNotFoundException e) {
                                            Toast.makeText(VaultActivity.this, "No application found to open this file.", Toast.LENGTH_LONG).show();
                                        }
                                        break;
                                    case R.id.menu_delete:
                                        files[mCurrentPosition].delete();
                                        directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
                                        files = directory.listFiles();
                                        mAdapter = new ImageAdapter(VaultActivity.this, files, IntentType);
                                        gridView.setAdapter(mAdapter);
                                        if (files.length <= 0) {
                                            lout_no_files.setVisibility(View.VISIBLE);
                                        } else {
                                            lout_no_files.setVisibility(View.GONE);
                                        }
                                        break;
                                    case R.id.menu_unhide:
                                        new MoveUnhideFile().execute(files[mCurrentPosition].getAbsolutePath());
                                        break;
                                }
                            }
                        }).show();
                    }
                } else {
                    onListItemSelect(i);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IntentType.equalsIgnoreCase("image")) {
                    start();
                } else if (IntentType.equalsIgnoreCase("video")) {
                    startvideo();
                }
            }
        });
    }

    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.action_menu_image, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {

                case R.id.menu_delete:
                    SparseBooleanArray mSelectedDelete = mAdapter.getSelectedIds();
                    for (int i = (mSelectedDelete.size() - 1); i >= 0; i--) {
                        files[mSelectedDelete.keyAt(i)].delete();
                    }
                    directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
                    files = directory.listFiles();
                    mAdapter = new ImageAdapter(VaultActivity.this, files, IntentType);
                    gridView.setAdapter(mAdapter);
                    if (files.length <= 0) {
                        lout_no_files.setVisibility(View.VISIBLE);
                    } else {
                        lout_no_files.setVisibility(View.GONE);
                    }
                    mode.finish();
                    setNullToActionMode();
                    return true;
                case R.id.menu_unhide:
                    SparseBooleanArray mSelectedUnhide = mAdapter.getSelectedIds();
                    new MoveUnhideFileMultiple(mSelectedUnhide).execute();
                    mode.finish();
                    setNullToActionMode();
                    return true;
                case R.id.menu_share:
                    ArrayList<Uri> fileUris = new ArrayList<Uri>();
                    SparseBooleanArray mSelectedShare = mAdapter.getSelectedIds();
                    for (int i = (mSelectedShare.size() - 1); i >= 0; i--) {
                        fileUris.add(FileProvider.getUriForFile(getApplicationContext(),
                                getApplicationContext().getPackageName() + ".provider", files[mSelectedShare.keyAt(i)]));
                    }
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Shared from " + getString(R.string.app_name) + " android app.");

                    if (IntentType.equalsIgnoreCase("image")) {
                        intent.setType("image/*");
                    } else if (IntentType.equalsIgnoreCase("video")) {
                        intent.setType("video/*");
                    } else if (IntentType.equalsIgnoreCase("audio")) {
                        intent.setType("audio/*");
                    } else {
                        intent.setType("*/*");
                    }
                    intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
                    startActivity(intent);

                    mode.finish();
                    setNullToActionMode();
                    return true;
                default:
                    setNullToActionMode();
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.removeSelection();
            setNullToActionMode();
        }
    };

    private void onListItemSelect(int position) {
        mAdapter.toggleSelection(position);
        boolean hasCheckedItems = mAdapter.getSelectedCount() > 0;
        if (hasCheckedItems && mActionMode == null)
            mActionMode = startSupportActionMode(modeCallBack);
        else if (!hasCheckedItems && mActionMode != null)
            mActionMode.finish();
        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(mAdapter.getSelectedCount()) + " selected");
    }

    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
        files = directory.listFiles();
        mAdapter = new ImageAdapter(VaultActivity.this, files, IntentType);
        gridView.setAdapter(mAdapter);
        if (files.length <= 0) {
            lout_no_files.setVisibility(View.VISIBLE);
        } else {
            lout_no_files.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PICKER_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        grantUriPermission(getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        new MoveHideFile_kitkat().execute(uri);
                    } else {
                        String SourcePath = getRealPathFromURI(uri);
                        new MoveHideFile().execute(SourcePath);
                    }
                }
                break;
            case REQUEST_CODE_PICKER:
                if (resultCode == Activity.RESULT_OK && data != null) {

                    images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                    new MoveHidePhoto(images).execute();

                }
                break;
        }
    }

    public String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = null;
        String mPath;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            cursor = getContentResolver().query(contentURI, null, null, null, null, null);
        } else {
            cursor = getContentResolver().query(contentURI, null, null, null, null);
        }
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        } else {
            return contentURI.getPath();
        }
    }


    private class MoveUnhideFile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VaultActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.setTitle("Unhide " + mActivityTitle);
            pDialog.setMessage("Unhiding " + mActivityTitle + ". Please wait...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            InputStream in = null;
            OutputStream out = null;
            String inputPath = strings[0];
            String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentMainDir + File.separator;
            String inputFile = inputPath.substring(inputPath.lastIndexOf("/") + 1);
            inputPath = inputPath.substring(0, inputPath.lastIndexOf("/") + 1);
            int read;
            try {

                File dir = new File(outputPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                long lenghtOfFile = new File(strings[0]).length();

                in = new FileInputStream(inputPath + inputFile);
                out = new FileOutputStream(outputPath + inputFile);

                byte[] buffer = new byte[1024];
                long total = 0;
                while ((read = in.read(buffer)) != -1) {
                    total += read;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    out.write(buffer, 0, read);
                }
                out.flush();
                out.close();
                in.close();
                new File(inputPath + inputFile).delete();
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(outputPath + inputFile))));
            } catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
            files = directory.listFiles();
            mAdapter = new ImageAdapter(VaultActivity.this, files, IntentType);
            gridView.setAdapter(mAdapter);
            if (files.length <= 0) {
                lout_no_files.setVisibility(View.VISIBLE);
            } else {
                lout_no_files.setVisibility(View.GONE);
            }
        }
    }

    private class MoveUnhideFileMultiple extends AsyncTask<String, String, String> {

        SparseBooleanArray mSelectedItems;

        public MoveUnhideFileMultiple(SparseBooleanArray mSelectedItemsIds) {
            this.mSelectedItems = mSelectedItemsIds;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VaultActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.setTitle("Unhide " + mActivityTitle);
            pDialog.setMessage("Unhiding " + mActivityTitle + ". Please wait...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            for (int i = (mSelectedItems.size() - 1); i >= 0; i--) {
                InputStream in = null;
                OutputStream out = null;
                String inputPath = files[mSelectedItems.keyAt(i)].getAbsolutePath();
                String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentMainDir + File.separator;
                String inputFile = inputPath.substring(inputPath.lastIndexOf("/") + 1);
                inputPath = inputPath.substring(0, inputPath.lastIndexOf("/") + 1);
                int read;
                try {
                    File dir = new File(outputPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    long lenghtOfFile = files[mSelectedItems.keyAt(i)].length();

                    in = new FileInputStream(inputPath + inputFile);
                    out = new FileOutputStream(outputPath + inputFile);

                    byte[] buffer = new byte[1024];
                    long total = 0;
                    while ((read = in.read(buffer)) != -1) {
                        total += read;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                    out.close();
                    in.close();
                    new File(inputPath + inputFile).delete();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(outputPath + inputFile))));
                } catch (FileNotFoundException fnfe1) {
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
            files = directory.listFiles();
            mAdapter = new ImageAdapter(VaultActivity.this, files, IntentType);
            gridView.setAdapter(mAdapter);
            if (files.length <= 0) {
                lout_no_files.setVisibility(View.VISIBLE);
            } else {
                lout_no_files.setVisibility(View.GONE);
            }
        }
    }

    private class MoveHidePhoto extends AsyncTask<String, String, String> {

        private ArrayList<Image> _images = new ArrayList<>();

        public MoveHidePhoto(ArrayList<Image> images) {
            this._images = images;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VaultActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.setTitle("Hide " + mActivityTitle);
            pDialog.setMessage("Hiding " + mActivityTitle + ". Please wait...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            for (int i = 0, l = _images.size(); i < l; i++) {
                InputStream in = null;
                OutputStream out = null;
                String inputPath = _images.get(i).getPath();
                String outputPath = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir + File.separator;
                String inputFile = inputPath.substring(inputPath.lastIndexOf("/") + 1);
                inputPath = inputPath.substring(0, inputPath.lastIndexOf("/") + 1);
                int read;
                try {

                    //create output directory if it doesn't exist
                    File dir = new File(outputPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    long lenghtOfFile = new File(_images.get(i).getPath()).length();

                    in = new FileInputStream(inputPath + inputFile);
                    out = new FileOutputStream(outputPath + inputFile);

                    byte[] buffer = new byte[1024];
                    long total = 0;
                    while ((read = in.read(buffer)) != -1) {
                        total += read;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                    out.close();
                    in.close();
                    NewFileUtils.deleteFile(getApplicationContext(), Uri.fromFile(new File(inputPath + inputFile)));
                } catch (FileNotFoundException fnfe1) {
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            this._images.clear();
            images.clear();
            directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
            files = directory.listFiles();
            mAdapter = new ImageAdapter(VaultActivity.this, files, IntentType);
            gridView.setAdapter(mAdapter);
            if (files.length <= 0) {
                lout_no_files.setVisibility(View.VISIBLE);
            } else {
                lout_no_files.setVisibility(View.GONE);
            }
        }
    }

    private class MoveHideFile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VaultActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.setTitle("Hide " + mActivityTitle);
            pDialog.setMessage("Hiding " + mActivityTitle + ". Please wait...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            InputStream in = null;
            OutputStream out = null;
            String inputPath = strings[0];
            String outputPath = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir + File.separator;
            String inputFile = inputPath.substring(inputPath.lastIndexOf("/") + 1);
            inputPath = inputPath.substring(0, inputPath.lastIndexOf("/") + 1);
            int read;
            try {
                File dir = new File(outputPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                long lenghtOfFile = new File(strings[0]).length();

                in = new FileInputStream(inputPath + inputFile);
                out = new FileOutputStream(outputPath + inputFile);

                byte[] buffer = new byte[1024];
                long total = 0;
                while ((read = in.read(buffer)) != -1) {
                    total += read;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    out.write(buffer, 0, read);
                }
                out.flush();
                out.close();
                in.close();
                NewFileUtils.deleteFile(getApplicationContext(), Uri.fromFile(new File(inputPath + inputFile)));

            } catch (FileNotFoundException fnfe1) {
                Log.e("tag", fnfe1.getMessage());
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
            files = directory.listFiles();
            mAdapter = new ImageAdapter(VaultActivity.this, files, IntentType);
            gridView.setAdapter(mAdapter);
            if (files.length <= 0) {
                lout_no_files.setVisibility(View.VISIBLE);
            } else {
                lout_no_files.setVisibility(View.GONE);
            }
            //Toast.makeText(VaultActivity.this, "Success", Toast.LENGTH_SHORT).show();
        }
    }

    private class MoveHideFile_kitkat extends AsyncTask<Uri, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(VaultActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.setTitle("Hide File");
            pDialog.setMessage("Hiding Please wait...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(Uri... uris) {

            Uri mFileUri = uris[0];
            InputStream mInputStream = null;
            try {
                mInputStream = getContentResolver().openInputStream(mFileUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String outputPath = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir + File.separator;
            String inputFile = null;
            OutputStream out = null;
            long lenghtOfFile = 0;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Cursor cursor = getContentResolver().query(mFileUri, null, null, null, null, null);
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        inputFile = FileUtils.getPath(getApplicationContext(), mFileUri);
                        if (inputFile != null) {
                            inputFile = inputFile.substring(inputFile.lastIndexOf("/") + 1);
                        }
                        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                        if (!cursor.isNull(sizeIndex)) {
                            lenghtOfFile = Long.parseLong(cursor.getString(sizeIndex));
                        } else {
                            lenghtOfFile = 0;
                        }
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                if (inputFile != null) {
                    File dir = new File(outputPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    int read;
                    try {
                        out = new FileOutputStream(outputPath + inputFile);
                        byte[] buffer = new byte[1024];
                        long total = 0;
                        while ((read = mInputStream.read(buffer)) != -1) {
                            total += read;
                            publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                            out.write(buffer, 0, read);
                        }
                        out.flush();
                        out.close();
                        mInputStream.close();
                        NewFileUtils.deleteFile(getApplicationContext(), mFileUri);

                    } catch (FileNotFoundException fnfe1) {
                        Log.e("tag", "" + fnfe1.getMessage());
                    } catch (Exception e) {
                        Log.e("tag", "" + e.getMessage());
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
            files = directory.listFiles();
            mAdapter = new ImageAdapter(VaultActivity.this, files, IntentType);
            gridView.setAdapter(mAdapter);
            if (files.length <= 0) {
                lout_no_files.setVisibility(View.VISIBLE);
            } else {
                lout_no_files.setVisibility(View.GONE);
            }
        }
    }

    public void start() {
        ImagePicker.create(this)
                .folderMode(true) // set folder mode (false by default)
                .folderTitle("Folder") // folder selection title
                .imageTitle("Tap to select") // image selection title
                .multi() // multi mode (default mode)
                .limit(20) // max images can be selected (999 by default)
                .showCamera(true) // show camera or not (true by default)
                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
                .origin(images) // original selected images, used in multi mode
                .start(REQUEST_CODE_PICKER); // start image picker activity with request code
    }

    public void startvideo() {
        VideoPicker.create(this)
                .folderMode(true) // set folder mode (false by default)
                .folderTitle("Folder") // folder selection title
                .imageTitle("Tap to select") // image selection title
                .multi() // multi mode (default mode)
                .showCamera(false)
                .limit(20) // max images can be selected (999 by default)
                .origin(images) // original selected images, used in multi mode
                .start(REQUEST_CODE_PICKER); // start image picker activity with request code
    }
}
