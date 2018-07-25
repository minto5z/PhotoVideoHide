package com.mintosoft.hidephotovideo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mintosoft.hidephotovideo.adapter.AdepterForMainScreenItem;
import com.mintosoft.hidephotovideo.utils.Preferences;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    Toolbar toolbar;
    GridView gridView;
    AdepterForMainScreenItem adepter;
    Intent i;
    private FirebaseAnalytics mFirebaseAnalytics;
    InterstitialAd mInterstitialAd;
    File root, dirimage, dirvideo, file, maindirimage, maindirvideo;
    List<String> permissionsNeeded = new ArrayList<>();
    List<String> permissionsList = new ArrayList<>();
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    boolean mAlertDialog = true;

    private String[] image_name = {
            "Hide Photo",
            "Hide Video",
            "Settings",
            "Rate us",
            "Share App",
            "FeedBack"
    };

    private Integer[] image_orignal = {
            R.drawable.ic_photo,
            R.drawable.ic_video,
            R.drawable.ic_setting,
            R.drawable.ic_rate_us,
            R.drawable.ic_share,
            R.drawable.ic_feedback,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (Preferences.getsecurityanswer(getApplicationContext()) == null) {
            Intent i = new Intent(getApplicationContext(), SecurityQuestionActivity.class);
            startActivity(i);
            finish();
        }

        setuppermission();

        makedir();

        FirebaseApp.initializeApp(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mAlertDialog = Preferences.getAlertDialog(this);


        final NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.NativeAdContainer);

        adView.loadAd(new AdRequest.Builder().addTestDevice(getResources().getString(R.string.test_device_id)).build());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }
        });

        requestNewInterstitial();

        final IProfile profile = new ProfileDrawerItem().withName(getString(R.string.app_name)).withEmail(getString(R.string.developer_email)).withIcon(R.drawable.icon).withIdentifier(100);


        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.color.colorAccent)
                .withAlternativeProfileHeaderSwitching(false)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(profile).withSavedInstance(savedInstanceState)
                .build();

        new DrawerBuilder()
                .withActivity(this)
                .withHasStableIds(true)
                .withSelectedItem(-1)
                .addDrawerItems(

                        new PrimaryDrawerItem().withName(R.string.navigation_setting).withIcon(R.drawable.ic_settings_black_24dp).withIdentifier(1).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.navigation_help).withIcon(R.drawable.ic_help_black_24dp).withIdentifier(3).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.navigation_rate).withIcon(R.drawable.ic_star_black_24dp).withIdentifier(4).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.navigation_recommend).withIcon(R.drawable.ic_share_black_24dp).withIdentifier(5).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.navigation_about).withIcon(R.drawable.ic_info_black_24dp).withIdentifier(2).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.photo_editing).withIcon(R.drawable.edit).withIdentifier(6).withSelectable(false)
                )

                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == 1) {
                                Intent mIntentSettings = new Intent(getApplicationContext(), LockscreenSettingsActivity.class);
                                startActivity(mIntentSettings);
                            } else if (drawerItem.getIdentifier() == 2) {
                                Intent mIntentSettings = new Intent(getApplicationContext(), AboutActivity.class);
                                startActivity(mIntentSettings);
                            } else if (drawerItem.getIdentifier() == 3) {
                                PackageManager manager = getApplicationContext().getPackageManager();
                                PackageInfo info = null;
                                try {
                                    info = manager.getPackageInfo(getPackageName(), 0);
                                } catch (PackageManager.NameNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                String version = info.versionName;
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("message/rfc822");
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email)});
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Help " + version);
                                intent.putExtra(Intent.EXTRA_TEXT, "Application Version : " + version + "\nDevice : " + getDeviceName() + "\nSystemVersion : " + Build.VERSION.SDK_INT + "\n\nHow can we help you?\n\n");
                                startActivity(Intent.createChooser(intent, "Send Email"));
                            } else if (drawerItem.getIdentifier() == 4) {
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                                }
                            } else if (drawerItem.getIdentifier() == 5) {
                                Intent intent1 = new Intent();
                                intent1.setAction(Intent.ACTION_SEND);
                                intent1.setType("text/plain");
                                final String text = "Check out "
                                        + getResources().getString(R.string.app_name)
                                        + ", the free app for hide you photo,video and files. https://play.google.com/store/apps/details?id="
                                        + getPackageName();
                                intent1.putExtra(Intent.EXTRA_TEXT, text);
                                Intent sender = Intent.createChooser(intent1, "Share " + getResources().getString(R.string.app_name));
                                startActivity(sender);
                            }
                            else if (drawerItem.getIdentifier() == 6) {
                                Intent intent1 = new Intent(getApplicationContext(),PhotoEdit.class);
                                startActivity(intent1);
                            }

                        }
                        return false;
                    }
                })
                .withAccountHeader(headerResult)
                .withToolbar(toolbar).build();


        gridView = (GridView) findViewById(R.id.gridViewHome);

        adepter = new AdepterForMainScreenItem(getApplicationContext(), image_name, image_orignal);
        gridView.setAdapter(adepter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {

                    case 0:
                        i = new Intent(getApplicationContext(), VaultActivity.class);
                        i.putExtra("Type", "Image");
                        startActivity(i);
                        if (mInterstitialAd != null) {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                requestNewInterstitial();
                            }
                        }

                        break;

                    case 1:
                        i = new Intent(getApplicationContext(), VaultActivity.class);
                        i.putExtra("Type", "Video");
                        startActivity(i);
                        if (mInterstitialAd != null) {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                requestNewInterstitial();
                            }
                        }

                        break;
                    case 2:
                        i = new Intent(getApplicationContext(), LockscreenSettingsActivity.class);
                        startActivity(i);

                        break;
                    case 3:

                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                        }

                        break;
                    case 4:

                        Intent intent1 = new Intent();
                        intent1.setAction(Intent.ACTION_SEND);
                        intent1.setType("text/plain");
                        final String text = "Check out "
                                + getResources().getString(R.string.app_name)
                                + ", the free app for hide you photo and video. https://play.google.com/store/apps/details?id="
                                + getPackageName();
                        intent1.putExtra(Intent.EXTRA_TEXT, text);
                        Intent sender = Intent.createChooser(intent1, "Share " + getResources().getString(R.string.app_name));
                        startActivity(sender);
                        break;

                    case 5:

                        PackageManager manager = getApplicationContext().getPackageManager();
                        PackageInfo info = null;
                        try {
                            info = manager.getPackageInfo(getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        String version = info.versionName;
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email)});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Help " + version);
                        intent.putExtra(Intent.EXTRA_TEXT, "Application Version : " + version + "\nDevice : " + getDeviceName() + "\nSystemVersion : " + Build.VERSION.SDK_INT + "\n\nHow can we help you?\n\n");
                        startActivity(Intent.createChooser(intent, "Send Email"));

                        break;


                }
            }
        });
    }

    private void makedir() {

        root = new File(getFilesDir(), getString(R.string.root_directory));
        if (!root.exists()) root.mkdir();

        dirimage = new File(root, getString(R.string.image_directory));
        if (!dirimage.exists()) dirimage.mkdir();

        dirvideo = new File(root, getString(R.string.video_directory));
        if (!dirvideo.exists()) dirvideo.mkdir();

        file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), getString(R.string.root_directory));
        if (!file.exists()) file.mkdir();

        maindirimage = new File(file, getString(R.string.image_directory));
        if (!maindirimage.exists()) maindirimage.mkdir();

        maindirvideo = new File(file, getString(R.string.video_directory));
        if (!maindirvideo.exists()) maindirvideo.mkdir();
    }

    @Override
    protected void onResume() {
        super.onResume();
        makedir();
        requestNewInterstitial();
    }


    private void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.int_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_id)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    private void setuppermission() {
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Write Storage");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
            ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
        }
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    if (mAlertDialog) {
                        new MaterialStyledDialog.Builder(this)
                                .setTitle("Instruction")
                                .setCancelable(false)
                                .setDescription("Your files are locked and stored inside application internal memory. Before uninstall application unhide all files after uninstall application will erase all hide files and will lost forever. \n\n Do not factory reset your phone, It will remove application and its data forever.")
                                .setPositiveText("OK")
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .setHeaderColor(R.color.colorAccent)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Preferences.setAlertDialog(getApplicationContext(), false);
                                        dialog.dismiss();
                                    }
                                }).show();
                    }

                } else {
                    if (mAlertDialog) {
                        new MaterialStyledDialog.Builder(this)
                                .setTitle("Instruction")
                                .setCancelable(false)
                                .setDescription("Your files are locked and stored inside application internal memory. Before uninstall application unhide all files after uninstall application will erase all hide files and will lost forever. \n\n Do not factory reset your phone, It will remove application and its data forever.")
                                .setPositiveText("OK")
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .setHeaderColor(R.color.colorAccent)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Preferences.setAlertDialog(getApplicationContext(), false);
                                        dialog.dismiss();
                                    }
                                }).show();
                    }

                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
