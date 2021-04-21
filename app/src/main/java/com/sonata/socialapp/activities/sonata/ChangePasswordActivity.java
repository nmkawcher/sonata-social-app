package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.Util;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText currentpass,newpass,newpassagain;
    RelativeLayout save, back;
    ProgressDialog progressDialog;
    long mLastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Util.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.changepassword));

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        currentpass = findViewById(R.id.currentpasswordedittext);
        newpass = findViewById(R.id.newpasswordedittext);
        newpassagain = findViewById(R.id.newpasswordedittexttekrar);
        save = findViewById(R.id.savetextripple);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(currentpass.getText().toString().trim().length()>0){
                    if(newpass.getText().toString().trim().length()>6){
                        if(newpass.getText().toString().trim().equals(newpassagain.getText().toString().trim())){
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                            builder.setMessage(R.string.changepasstext);
                            builder.setCancelable(true);
                            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                    progressDialog.show();
                                    HashMap<String,Object> params = new HashMap<>();
                                    params.put("currentpass",currentpass.getText().toString().trim());
                                    params.put("newpass",newpass.getText().toString().trim());
                                    ParseCloud.callFunctionInBackground("changePassword", params, new FunctionCallback<HashMap>() {
                                        @Override
                                        public void done(HashMap object, ParseException e) {
                                            if(e==null){
                                                ParseUser.logOut();
                                                Util.ToastLong(ChangePasswordActivity.this,getString(R.string.passwordchanged));
                                                progressDialog.dismiss();
                                                startActivity(new Intent(ChangePasswordActivity.this,StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                                ChangePasswordActivity.this.finish();
                                            }
                                            else{
                                                Util.ToastLong(ChangePasswordActivity.this,getString(R.string.error));
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });


                                }
                            });
                            builder.show();

                        }
                        else{
                            Util.ToastLong(ChangePasswordActivity.this,getString(R.string.passwordsdoesntmatch));
                        }
                    }
                    else{
                        Util.ToastLong(ChangePasswordActivity.this,getString(R.string.shortpass));
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
