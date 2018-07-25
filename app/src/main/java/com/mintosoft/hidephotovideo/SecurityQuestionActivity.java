package com.mintosoft.hidephotovideo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.mintosoft.hidephotovideo.utils.Preferences;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

public class SecurityQuestionActivity extends AppCompatActivity {

    Toolbar toolbar;
    List<String> questions;
    Button btn_security_question;
    MaterialEditText txtbx_security_question;
    boolean mFromSettings = false;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_question);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.activity_security_question);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (this.getIntent().hasExtra("FromSetting")) {
            mFromSettings = true;
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        }

        questions = new ArrayList<>();
        questions.add("Who was your childhood hero?");
        questions.add("Which is your favorite movie?");
        questions.add("What is the name of first pet?");
        questions.add("What is your city of birth?");
        questions.add("What is your favorite book?");
        questions.add("What is your favorite song?");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, questions);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner = (Spinner) findViewById(R.id.security_question_spinner);
        btn_security_question = (Button) findViewById(R.id.btn_security_question);
        txtbx_security_question = (MaterialEditText) findViewById(R.id.txtbx_security_question);
        spinner.setAdapter(dataAdapter);

        if (this.getIntent().hasExtra("FromSetting")) {
            txtbx_security_question.setText(Preferences.getsecurityanswer(getApplicationContext()));
            txtbx_security_question.setText(Preferences.getsecurityanswer(getApplicationContext()));
        }

        spinner.setSelection(Preferences.getsecurityquestionNumber(getApplicationContext()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Preferences.savesecurityquestionNumber(getApplicationContext(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Preferences.savesecurityquestionNumber(getApplicationContext(), null);
            }
        });

        txtbx_security_question.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (txtbx_security_question.getText().toString().equalsIgnoreCase("") || txtbx_security_question.getText() == null) {
                        txtbx_security_question.setError("Please enter answer");
                        return false;
                    } /*else if (Preferences.getsecurityquestionNumber(getApplicationContext()) == null) {
                        Toast.makeText(getApplicationContext(), "Please select question", Toast.LENGTH_SHORT).show();
                    }*/ else {
                        Preferences.savesecurityanswer(getApplicationContext(), txtbx_security_question.getText().toString());
                        if (mFromSettings) {
                            finish();
                        } else {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        btn_security_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtbx_security_question.getText().toString().equalsIgnoreCase("") || txtbx_security_question.getText() == null) {
                    txtbx_security_question.setError("Please enter answer");
                } /*else if (Preferences.getsecurityquestion(getApplicationContext()) == null) {
                    Toast.makeText(getApplicationContext(), "Please select question", Toast.LENGTH_SHORT).show();
                }*/ else {
                    Preferences.savesecurityanswer(getApplicationContext(), txtbx_security_question.getText().toString());
                    if (mFromSettings) {
                        finish();
                    } else {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
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
