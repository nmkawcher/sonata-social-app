package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.SonataUser;

import java.util.HashMap;

public class ChangeUsernameActivity extends AppCompatActivity {

    RelativeLayout save,back;
    ProgressDialog progressDialog;
    EditText editText;
    TextWatcher textWatcher;
    ProgressBar progressBar;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        save.setOnClickListener(null);
        back.setOnClickListener(null);
        save=null;
        back=null;
        progressDialog.dismiss();
        progressDialog=null;
        editText.addTextChangedListener(null);
        editText=null;
        textWatcher=null;
        progressBar = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.usernameCheckProgress);
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()>0){
                    if(!s.toString().equals(GenelUtil.getCurrentUser().getUsername())){
                        if(save!=null){
                            save.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        if(save!=null){
                            save.setVisibility(View.INVISIBLE);
                        }
                    }
                    if(editText!=null){
                        editText.setError(null);
                    }
                }
                else{
                    if(editText!=null){
                        editText.setError(null);
                    }
                    if(save!=null){
                        save.setVisibility(View.INVISIBLE);
                    }
                    if(progressBar!=null){
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals(GenelUtil.getCurrentUser().getUsername())&&s.toString().length()>0){
                    if(progressBar!=null){
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    final String t = s.toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Do something after 5s = 5000ms
                            if(GenelUtil.isAlive(ChangeUsernameActivity.this)){
                                if(t.equals(editText.getText().toString())){
                                    HashMap<String,String> params =new HashMap<>();
                                    params.put("username",editText.getText().toString().trim());
                                    ParseCloud.callFunctionInBackground("checkUsername", params, new FunctionCallback<String>() {
                                        @Override
                                        public void done(String object, ParseException e) {
                                            if(GenelUtil.isAlive(ChangeUsernameActivity.this)){
                                                if(e==null){
                                                    if(object!=null){
                                                        if(object.equals("canTake")){
                                                            if(progressBar!=null){
                                                                progressBar.setVisibility(View.INVISIBLE);
                                                            }
                                                        }
                                                    }
                                                }
                                                else{
                                                    if(progressBar!=null){
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                    }
                                                    if(e.getMessage()!=null){
                                                        if(e.getMessage().equals("denied")){
                                                            editText.setError(getString(R.string.usernamecantbeempty));
                                                        }
                                                        else if(e.getMessage().equals("usernamemustbeshorterthan25")){
                                                            editText.setError(getString(R.string.usernametoolong));
                                                        }
                                                        else if(e.getMessage().equals("usernamemustcontainletter")){
                                                            editText.setError(getString(R.string.usernamemustcontainletter));
                                                        }
                                                        else if(e.getMessage().equals("usernameMustBeAlphaNumeric")){
                                                            editText.setError(getString(R.string.invalidchars));
                                                        }
                                                        else if(e.getMessage().equals("taken")){
                                                            editText.setError(getString(R.string.usernametaken));
                                                        }
                                                    }


                                                }
                                            }
                                        }
                                    });
                                }
                            }

                        }
                    }, 1000);

                }
                else{
                    if(editText!=null){
                        editText.setError(null);
                    }
                    if(save!=null){
                        save.setVisibility(View.INVISIBLE);
                    }
                    if(progressBar!=null){
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }

            }
        };

        editText = findViewById(R.id.changeusernameedittext);
        editText.setText(GenelUtil.getCurrentUser().getUsername());
        editText.addTextChangedListener(textWatcher);
        editText.setFilters(new InputFilter[] {
                new InputFilter.AllCaps() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        return String.valueOf(source).toLowerCase();
                    }
                },new InputFilter.LengthFilter(25)
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.savechanges));
        progressDialog.setCancelable(false);
        save = findViewById(R.id.savetextripple);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().length()>0){
                    progressDialog.show();
                    HashMap<String,String> params =new HashMap<>();
                    params.put("username",editText.getText().toString().trim());
                    ParseCloud.callFunctionInBackground("updateUsername", params, new FunctionCallback<HashMap>() {
                        @Override
                        public void done(HashMap object, ParseException e) {
                            if(GenelUtil.isAlive(ChangeUsernameActivity.this)){
                                if(e==null){
                                    GenelUtil.saveNewUser(GenelUtil.convertUserToJson((SonataUser) object.get("user")),ChangeUsernameActivity.this);
                                    progressDialog.dismiss();
                                    onBackPressed();
                                }
                                else{
                                    progressDialog.dismiss();
                                    if(e.getCode()==ParseException.USERNAME_TAKEN){
                                        editText.setError(getString(R.string.usernametaken));
                                    }
                                    else{
                                        if(e.getMessage()!=null){
                                            if(e.getMessage().equals("denied")){
                                                editText.setError(getString(R.string.usernamecantbeempty));
                                            }
                                            else if(e.getMessage().equals("usernamemustbeshorterthan25")){
                                                editText.setError(getString(R.string.usernametoolong));
                                            }
                                            else if(e.getMessage().equals("usernamemustcontainletter")){
                                                editText.setError(getString(R.string.usernamemustcontainletter));
                                            }
                                            else if(e.getMessage().equals("usernameMustBeAlphaNumeric")){
                                                editText.setError(getString(R.string.invalidchars));
                                            }
                                        }
                                    }


                                }
                            }
                        }
                    });

                }

            }
        });


    }
}
