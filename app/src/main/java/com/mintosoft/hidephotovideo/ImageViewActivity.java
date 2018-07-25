package com.mintosoft.hidephotovideo;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mintosoft.hidephotovideo.adapter.ImageViewAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageViewActivity extends AppCompatActivity {

    int mCurrentPosition = 0;
    ViewPager mViewPager;
    ImageViewAdapter adapter;
    File directory;
    File[] files;
    Toolbar toolbar;
    ProgressDialog pDialog;
    String CurrentDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        CurrentDir = getString(R.string.image_directory);
        ;

        mCurrentPosition = this.getIntent().getIntExtra("CurrentPosition", 0);

        directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
        files = directory.listFiles();

        getSupportActionBar().setTitle((mCurrentPosition + 1) + "/" + files.length);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ImageViewAdapter(this, files);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mCurrentPosition);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                toolbar.setTitle((mCurrentPosition + 1) + "/" + files.length);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu_image, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_delete:
                files[mCurrentPosition].delete();
                directory = new File(getFilesDir().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + CurrentDir);
                files = directory.listFiles();
                if (files.length > 0) {
                    toolbar.setTitle((mCurrentPosition + 1) + "/" + files.length);
                    mViewPager.removeAllViews();
                    adapter = new ImageViewAdapter(this, files);
                    mViewPager.setAdapter(adapter);
                    mViewPager.setCurrentItem(mCurrentPosition);
                } else {
                    finish();
                }
                return true;
            case R.id.menu_share:
                Uri mUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", files[mCurrentPosition]);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType(getContentResolver().getType(mUri));
                intent.putExtra(Intent.EXTRA_STREAM, mUri);
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ImageViewActivity.this, "No application found to open this file.", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_unhide:
                new MoveUnhideFile().execute(files[mCurrentPosition].getAbsolutePath());
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private class MoveUnhideFile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ImageViewActivity.this);
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(false);
            pDialog.setTitle("Unhide image");
            pDialog.setMessage("Unhiding image. Please wait...");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            InputStream in;
            OutputStream out;
            String inputPath = strings[0];
            String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getString(R.string.root_directory) + File.separator + getString(R.string.image_directory) + File.separator;
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
                in.close();
                out.flush();
                out.close();
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
            if (files.length > 0) {
                toolbar.setTitle((mCurrentPosition + 1) + "/" + files.length);
                mViewPager.removeAllViews();
                adapter = new ImageViewAdapter(ImageViewActivity.this, files);
                mViewPager.setAdapter(adapter);
                mViewPager.setCurrentItem(mCurrentPosition);
            } else {
                finish();
            }
        }
    }
}
