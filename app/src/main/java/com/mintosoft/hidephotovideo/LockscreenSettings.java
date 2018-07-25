package com.mintosoft.hidephotovideo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;

import com.github.ajalt.reprint.core.Reprint;

public class LockscreenSettings extends PreferenceFragmentCompat {

    public static final String FRAGMENT_TAG = "my_lockscreen_preference_fragment";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.lockscreen_settings, rootKey);

        Preference myPref = findPreference("changepasscode");
        myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent mIntentSettings = new Intent(getActivity(), PasscodeActivity.class);
                mIntentSettings.putExtra("ChangePasscode", true);
                startActivity(mIntentSettings);
                return true;
            }
        });


        Preference aboutus = findPreference("aboutus");
        aboutus.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent mIntentSettings = new Intent(getActivity(), AboutActivity.class);
                startActivity(mIntentSettings);
                return true;
            }
        });


        Preference securityquestion = findPreference("securityquestion");
        securityquestion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent mIntentSettings = new Intent(getActivity(), SecurityQuestionActivity.class);
                mIntentSettings.putExtra("FromSetting", true);
                startActivity(mIntentSettings);
                return true;
            }
        });


        Preference share = findPreference("share");
        share.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Intent intent1 = new Intent();
                intent1.setAction(Intent.ACTION_SEND);
                intent1.setType("text/plain");
                final String text = "Check out "
                        + getResources().getString(R.string.app_name)
                        + ", the free app for hide you photo and video. https://play.google.com/store/apps/details?id="
                        + getActivity().getPackageName();
                intent1.putExtra(Intent.EXTRA_TEXT, text);
                Intent sender = Intent.createChooser(intent1, "Share " + getResources().getString(R.string.app_name));
                startActivity(sender);
                return true;
            }
        });

        Preference feedback = findPreference("feedback");
        feedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                PackageManager manager = getActivity().getApplicationContext()
                        .getPackageManager();
                PackageInfo info = null;
                try {
                    info = manager.getPackageInfo(getActivity().getPackageName(), 0);
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
                return true;
            }
        });


        Preference rateus = findPreference("rateus");
        rateus.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                }
                return true;
            }
        });


        Reprint.initialize(getActivity());

        Preference myPrefusefinger = findPreference("usefingerprint");
        myPrefusefinger.setVisible(false);

        if (Reprint.isHardwarePresent()) {
            myPrefusefinger.setVisible(true);
            if (Reprint.hasFingerprintRegistered()) {
                myPrefusefinger.setEnabled(true);
            } else {
                myPrefusefinger.setEnabled(false);
                myPrefusefinger.setSummary(R.string.pref_use_fingerprint_desc1);
            }
        } else {
            myPrefusefinger.setVisible(false);
        }
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
}
