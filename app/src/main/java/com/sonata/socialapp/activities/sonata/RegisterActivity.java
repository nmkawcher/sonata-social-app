package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.SonataUser;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username,name,email,password;
    Button register;
    ProgressDialog progressDialog;
    TextView userAgreement, privacyPolicy;
    private long mLastClickTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemeDay);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.register));
        username=findViewById(R.id.registerusernameedittext);
        username.setFilters(new InputFilter[] {
                new InputFilter.AllCaps() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        return String.valueOf(source).toLowerCase();
                    }
                }
        });
        name=findViewById(R.id.registernamesurnameedittext);
        email=findViewById(R.id.registeremailedittext);
        password=findViewById(R.id.registerpasswordedittext);
        register=findViewById(R.id.registerregisterbutton);
        userAgreement = findViewById(R.id.useragreement);
        userAgreement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                String url = "https://cdn-sn-1-i.sonatasocialapp.com/user-agreement.html";

                if(GenelUtil.getNightMode()){
                    builder.setToolbarColor(Color.parseColor("#303030"));
                }
                else{
                    builder.setToolbarColor(Color.parseColor("#ffffff"));
                }
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(RegisterActivity.this, Uri.parse(url));
            }
        });

        privacyPolicy = findViewById(R.id.privacypolicy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                String url = "https://cdn-sn-1-i.sonatasocialapp.com/b0de9d742124c27da0a82cd2a59fb3d4_privacy-policy.html";

                if(GenelUtil.getNightMode()){
                    builder.setToolbarColor(Color.parseColor("#303030"));
                }
                else{
                    builder.setToolbarColor(Color.parseColor("#ffffff"));
                }
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(RegisterActivity.this, Uri.parse(url));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }


                mLastClickTime = SystemClock.elapsedRealtime();

                if(name.getText().toString().trim().length()>0){
                    if(username.getText().toString().trim().length()>0){
                        if(username.getText().toString().trim().matches("([a-z0-9\\_\\.]+)")){
                            if(email.getText().toString().trim().length()>0){
                                if(password.getText().toString().length()>=6){
                                    if(isEmailValid(email.getText().toString())){
                                        progressDialog.show();
                                        SonataUser user = new SonataUser();
                                        user.setEmail(email.getText().toString().trim());
                                        user.setPassword(password.getText().toString());
                                        user.setUsername(username.getText().toString().trim());
                                        user.put("namesurname",name.getText().toString().trim().replaceAll("\\s+", " "));



                                        HashMap<String, Object> params = new HashMap<String, Object>();
                                        params.put("name", name.getText().toString().trim());
                                        params.put("username", username.getText().toString());

                                        params.put("password", password.getText().toString());

                                        params.put("email", email.getText().toString().trim());

                                        ParseCloud.callFunctionInBackground("register", params, new FunctionCallback<String>() {
                                            @Override
                                            public void done(String  objects, ParseException e) {
                                                if(e==null){
                                                    if(objects.equals("signUpSuccess")){
                                                        SonataUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                                                            @Override
                                                            public void done(ParseUser user, ParseException e) {
                                                                if(user!=null){
                                                                    if(e==null){
                                                                        GenelUtil.saveNewUser(GenelUtil.convertUserToJson((SonataUser) ParseUser.getCurrentUser()),RegisterActivity.this);
                                                                        startActivity(new Intent(RegisterActivity.this,ChooseCategoryActivity.class));
                                                                        finish();
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                                else{
                                                    if(e.getCode()==ParseException.USERNAME_TAKEN){
                                                        username.setError(getString(R.string.usernametaken));
                                                        new CountDownTimer(2750, 2750) {

                                                            public void onTick(long millisUntilFinished) {

                                                            }

                                                            public void onFinish() {
                                                                username.setError(null);
                                                            }

                                                        }.start();
                                                    }
                                                    if(e.getCode()==203){
                                                        email.setError(getString(R.string.emailtaken));
                                                        new CountDownTimer(2750, 2750) {

                                                            public void onTick(long millisUntilFinished) {

                                                            }

                                                            public void onFinish() {
                                                                email.setError(null);
                                                            }

                                                        }.start();
                                                    }
                                                    progressDialog.hide();
                                                }
                                            }
                                        });


                                    }
                                    else{
                                        email.setError(getString(R.string.invalidemail));
                                        new CountDownTimer(2750, 2750) {
                                            public void onTick(long millisUntilFinished) {

                                            }
                                            public void onFinish() {
                                                email.setError(null);
                                            }

                                        }.start();
                                    }
                                }
                                else{
                                    password.setError(getString(R.string.shortpass));
                                    new CountDownTimer(2750, 2750) {

                                        public void onTick(long millisUntilFinished) {

                                        }

                                        public void onFinish() {
                                            password.setError(null);
                                        }

                                    }.start();
                                }
                            }
                            else{
                                email.setError(getString(R.string.emailfieldcantbeempty));
                                new CountDownTimer(2750, 2750) {

                                    public void onTick(long millisUntilFinished) {

                                    }

                                    public void onFinish() {
                                        email.setError(null);
                                    }

                                }.start();
                            }
                        }
                        else {
                            username.setError(getString(R.string.invalidchars));
                            new CountDownTimer(2750, 2750) {

                                public void onTick(long millisUntilFinished) {

                                }

                                public void onFinish() {
                                    username.setError(null);
                                }

                            }.start();
                        }

                    }
                    else{
                        username.setError(getString(R.string.usernamecantbeempty));
                        new CountDownTimer(2750, 2750) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                username.setError(null);
                            }

                        }.start();
                    }

                }
                else{
                    name.setError(getString(R.string.namecantbeempty));
                    new CountDownTimer(2750, 2750) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            name.setError(null);
                        }

                    }.start();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        finish();
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
