package com.mintosoft.hidephotovideo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mintosoft.hidephotovideo.utils.Preferences;
import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;

public class PasscodeActivity extends AppCompatActivity {

    TextView txt_answer, txt_passcode_text, txtForgetPassword;
    LinearLayout btn_calc_clear, btn_calc_done, btn_calc_7, btn_calc_8, btn_calc_9, btn_calc_4, btn_calc_5, btn_calc_6, btn_calc_1, btn_calc_2, btn_calc_3, btn_calc_0;
    String Passcode = "";
    String Passcode1 = null;
    Integer limit = 4;
    boolean ChangePasscode = false;
    boolean UseFingerpring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        setupview();

        UseFingerpring = Preferences.usefingerprint(this);
        Passcode = Preferences.getpasscode(this);
        txt_answer.setText("");

        if (this.getIntent().hasExtra("ChangePasscode")) {
            ChangePasscode = this.getIntent().getBooleanExtra("ChangePasscode", false);
        }

/*
        if (UseFingerpring) {
            Reprint.initialize(this);
            Reprint.authenticate(new AuthenticationListener() {
                @Override
                public void onSuccess(int moduleTag) {
                    Intent passintent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(passintent);
                    finish();
                }

                @Override
                public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {

                }
            });
        }
*/

        if (Passcode == null) {
            txt_passcode_text.setVisibility(View.VISIBLE);
            txt_passcode_text.setText("Create a 4-digit passcode");
            new MaterialStyledDialog.Builder(this)
                    .setTitle("Instruction")
                    .setCancelable(false)
                    .setDescription("Enter a 4-digit passcode, and press done button to continue. \n\n Once set up, you can enter the PIN code and press done button to unlock private space.")
                    .setPositiveText("OK")
                    .setStyle(Style.HEADER_WITH_TITLE)
                    .setHeaderColor(R.color.colorAccent)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        if (ChangePasscode) {
            Passcode = null;
            txt_passcode_text.setVisibility(View.VISIBLE);
            txt_passcode_text.setText("Create a new 4-digit passcode");

        }

        txt_answer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Passcode == null) {
                    if (s.length() == limit) {
                        btn_calc_done.setEnabled(true);

                    } else {
                        btn_calc_done.setEnabled(false);
                    }
                }
            }
        });


        btn_calc_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit)
                    txt_answer.setText("" + curval + "1");
            }
        });

        btn_calc_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit)
                    txt_answer.setText("" + curval + "2");
            }
        });

        btn_calc_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit)
                    txt_answer.setText("" + curval + "3");
            }
        });

        btn_calc_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit)
                    txt_answer.setText("" + curval + "4");
            }
        });

        btn_calc_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit)
                    txt_answer.setText("" + curval + "5");
            }
        });

        btn_calc_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit)
                    txt_answer.setText("" + curval + "6");
            }
        });

        btn_calc_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit)
                    txt_answer.setText("" + curval + "7");
            }
        });

        btn_calc_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit)
                    txt_answer.setText("" + curval + "8");
            }
        });

        btn_calc_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit)
                    txt_answer.setText("" + curval + "9");
            }
        });

        btn_calc_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (curval.length() < limit && !curval.equalsIgnoreCase("0")) {
                    txt_answer.setText("" + curval + "0");
                }
            }
        });

        btn_calc_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curval = txt_answer.getText().toString();
                if (Passcode == null) {
                    if (Passcode1 == null) {
                        Passcode1 = curval;
                        txt_passcode_text.setText("Confirm passcode");
                        txt_answer.setText("");
                    } else {
                        if (Passcode1.equalsIgnoreCase(curval)) {
                            if (ChangePasscode) {
                                Preferences.savepasscode(getApplicationContext(), Passcode1);
                                finish();
                            } else {
                                Preferences.savepasscode(getApplicationContext(), Passcode1);
                                if (Preferences.getsecurityanswer(getApplicationContext()) == null) {
                                    Intent passintent = new Intent(getApplicationContext(), SecurityQuestionActivity.class);
                                    startActivity(passintent);
                                    finish();
                                } else {
                                    Intent passintent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(passintent);
                                    finish();
                                }


                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Passcode dose not match.", Toast.LENGTH_SHORT).show();
                            txt_answer.setText("");
                        }
                    }
                } else if (!curval.equalsIgnoreCase("")) {
                    if (curval.equalsIgnoreCase(Passcode)) {
                        if (Preferences.getsecurityanswer(getApplicationContext()) == null) {
                            Intent passintent = new Intent(getApplicationContext(), SecurityQuestionActivity.class);
                            startActivity(passintent);
                            finish();
                        } else {
                            Intent passintent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(passintent);
                            finish();
                        }
                        finish();
                    } else {
                        txt_answer.setText("");
                        Toast.makeText(getApplicationContext(), "Please enter right password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        btn_calc_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_answer.setText("");
            }
        });

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passintent = new Intent(getApplicationContext(), ForgetPasscodeActivity.class);
                startActivity(passintent);
                finish();
            }
        });
    }

    private void setupview() {
        txt_answer = (TextView) findViewById(R.id.txt_answer);
        txt_passcode_text = (TextView) findViewById(R.id.txt_passcode_text);
        txtForgetPassword = (TextView) findViewById(R.id.txtForgetPassword);
        btn_calc_1 = (LinearLayout) findViewById(R.id.btn_calc_1);
        btn_calc_2 = (LinearLayout) findViewById(R.id.btn_calc_2);
        btn_calc_3 = (LinearLayout) findViewById(R.id.btn_calc_3);
        btn_calc_4 = (LinearLayout) findViewById(R.id.btn_calc_4);
        btn_calc_5 = (LinearLayout) findViewById(R.id.btn_calc_5);
        btn_calc_6 = (LinearLayout) findViewById(R.id.btn_calc_6);
        btn_calc_7 = (LinearLayout) findViewById(R.id.btn_calc_7);
        btn_calc_8 = (LinearLayout) findViewById(R.id.btn_calc_8);
        btn_calc_9 = (LinearLayout) findViewById(R.id.btn_calc_9);
        btn_calc_0 = (LinearLayout) findViewById(R.id.btn_calc_0);
        btn_calc_done = (LinearLayout) findViewById(R.id.btn_calc_done);
        btn_calc_clear = (LinearLayout) findViewById(R.id.btn_calc_clear);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (UseFingerpring) {
            Reprint.initialize(this);
            Reprint.authenticate(new AuthenticationListener() {
                @Override
                public void onSuccess(int moduleTag) {
                    Intent passintent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(passintent);
                    finish();
                }

                @Override
                public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (!ChangePasscode) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        } else {
            super.onBackPressed();
        }

    }

}
