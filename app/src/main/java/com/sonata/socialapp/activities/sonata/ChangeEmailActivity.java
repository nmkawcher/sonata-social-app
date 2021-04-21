package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.Util;

import java.util.HashMap;

public class ChangeEmailActivity extends AppCompatActivity {

    EditText pass,email;
    RelativeLayout save,back;
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
        setContentView(R.layout.activity_change_email);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.changeemail));

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        pass = findViewById(R.id.passwordedittext);
        email = findViewById(R.id.newemailedittext);
        save = findViewById(R.id.savetextripple);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(pass.getText().toString().trim().length()>0
                        &&email.getText().toString().trim().length()>0
                        &&android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()){
                    progressDialog.show();
                    HashMap<String,Object> params = new HashMap<>();
                    params.put("pass",pass.getText().toString().trim());
                    params.put("email",email.getText().toString().trim());
                    ParseCloud.callFunctionInBackground("changeEmail", params, new FunctionCallback<HashMap>() {
                        @Override
                        public void done(HashMap object, ParseException e) {
                            if(e==null){
                                Util.ToastLong(ChangeEmailActivity.this,getString(R.string.emailchanged));
                                progressDialog.dismiss();
                                ChangeEmailActivity.this.finish();
                            }
                            else{
                                Util.ToastLong(ChangeEmailActivity.this,getString(R.string.error));
                                progressDialog.dismiss();
                            }
                        }
                    });
                }

            }
        });
    }
}
