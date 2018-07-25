package com.mintosoft.hidephotovideo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mintosoft.hidephotovideo.utils.Preferences;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

public class ForgetPasscodeActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt_security_question;
    MaterialEditText txtbx_security_answer;
    Button btn_security_answer;
    List<String> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passcode);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_forget);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        questions = new ArrayList<>();
        questions.add("Who was your childhood hero?");
        questions.add("Which is your favorite movie?");
        questions.add("What is the name of first pet?");
        questions.add("What is your city of birth?");
        questions.add("What is your favorite book?");
        questions.add("What is your favorite song?");

        txt_security_question = (TextView) findViewById(R.id.txt_security_question);
        txtbx_security_answer = (MaterialEditText) findViewById(R.id.txtbx_security_answer);
        btn_security_answer = (Button) findViewById(R.id.btn_security_answer);

        txt_security_question.setText(questions.get(Preferences.getsecurityquestionNumber(getApplicationContext())));

        txtbx_security_answer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (txtbx_security_answer.getText().toString().equalsIgnoreCase("") || txtbx_security_answer.getText() == null) {
                        txtbx_security_answer.setError("Please enter answer");
                        return false;
                    } else {
                        if (txtbx_security_answer.getText().toString().equalsIgnoreCase(Preferences.getsecurityanswer(getApplicationContext()))) {
                            new MaterialStyledDialog.Builder(ForgetPasscodeActivity.this)
                                    .setTitle("PASSCODE")
                                    .setCancelable(false)
                                    .setDescription("Your passcode is :" + Preferences.getpasscode(getApplicationContext()))
                                    .setPositiveText("OK")
                                    .setStyle(Style.HEADER_WITH_TITLE)
                                    .setHeaderColor(R.color.colorAccent)
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            finish();
                                        }
                                    }).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        btn_security_answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtbx_security_answer.getText().toString().equalsIgnoreCase("") || txtbx_security_answer.getText() == null) {
                    txtbx_security_answer.setError("Please enter answer");
                } else {
                    if (txtbx_security_answer.getText().toString().equalsIgnoreCase(Preferences.getsecurityanswer(getApplicationContext()))) {
                        new MaterialStyledDialog.Builder(ForgetPasscodeActivity.this)
                                .setTitle("PASSCODE")
                                .setCancelable(false)
                                .setDescription("Your passcode is :" + Preferences.getpasscode(getApplicationContext()))
                                .setPositiveText("OK")
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .setHeaderColor(R.color.colorAccent)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        finish();
                                    }
                                }).show();
                    }
                }

            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
