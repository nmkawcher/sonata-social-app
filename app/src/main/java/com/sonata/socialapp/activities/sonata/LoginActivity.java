package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.SonataUser;

public class LoginActivity extends AppCompatActivity {

    Button login,register;
    TextView forgottenpassword;
    EditText username,password;
    ProgressDialog progressDialog;
    private long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.ThemeDay);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.login));
        login=findViewById(R.id.loginloginbutton);
        register=findViewById(R.id.loginregisterbutton);

        username=findViewById(R.id.loginusernameedittext);
        password=findViewById(R.id.loginpasswordedittext);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                progressDialog.dismiss();
                finish();
            }
        });

        forgottenpassword=findViewById(R.id.loginforgottenpassword);
        forgottenpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                final EditText input = new EditText(LoginActivity.this);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                input.setMaxLines(1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    input.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO);
                }
                input.setTextColor(Color.parseColor("#808283"));
                input.setHint(getString(R.string.email));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(10,0,10,0);
                input.setLayoutParams(lp);

                new AlertDialog.Builder(LoginActivity.this)
                        .setMessage(getString(R.string.enteremail))
                        .setView(input)
                        .setPositiveButton(getString(R.string.sendPasswordResetEmail), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage(getString(R.string.loading));
                                progressDialog.show();
                                String text = input.getText().toString();
                                if(text.trim().length()<1){
                                    GenelUtil.ToastLong(LoginActivity.this,getString(R.string.error));
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                    return;
                                }
                                ParseUser.requestPasswordResetInBackground(text.trim(), new RequestPasswordResetCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null){
                                            GenelUtil.ToastLong(LoginActivity.this,getString(R.string.emailsent));
                                            dialog.dismiss();
                                            progressDialog.dismiss();
                                        }
                                        else{
                                            GenelUtil.ToastLong(LoginActivity.this,getString(R.string.error));
                                            dialog.dismiss();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }

                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(username.getText().toString().length()>0&&password.getText().toString().length()>0){
                    progressDialog.show();
                    /*HashMap<String,String> params = new HashMap<>();
                    params.put("username",username.getText().toString().trim());
                    params.put("password",password.getText().toString());
                    ParseCloud.callFunctionInBackground("login", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String user, ParseException e) {
                            if(e==null){
                                become(user);

                            }
                            else{

                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),getString(R.string.invalidemailorusername),Toast.LENGTH_LONG).show();
                            }
                        }
                    });*/
                    ParseUser.logInInBackground(username.getText().toString().trim(), password.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(e==null){
                                if(user!=null){
                                    GenelUtil.saveNewUser(GenelUtil.convertUserToJson((SonataUser) ParseUser.getCurrentUser()),LoginActivity.this);
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                    progressDialog.dismiss();
                                    finish();
                                }
                                else{
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(),getString(R.string.invalidemailorusername),Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(),getString(R.string.invalidemailorusername),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void become(String token){
        SonataUser.becomeInBackground(token, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e==null){
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    progressDialog.dismiss();

                    finish();
                }
                else{
                    if(e.getCode()==ParseException.CONNECTION_FAILED){
                        become(token);
                    }
                }
            }
        });
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
