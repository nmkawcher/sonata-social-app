package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.google.android.exoplayer2.util.Log;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.SonataUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AdvancedSettingsActivity extends AppCompatActivity {

    RelativeLayout prvAccountLayout,changeUsername, changeEmail, changePass, back;
    Switch privateAccount;
    Spinner spinner;
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    ProgressDialog progressDialog;

    AdapterView.OnItemSelectedListener onItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_settings);

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SonataUser user = (SonataUser) ParseUser.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        prvAccountLayout = findViewById(R.id.praclayout);
        spinner = findViewById(R.id.settingsaccountypespinner);
        List<String> list = new ArrayList<>();

        list.add(getString(R.string.personal));
        list.add(getString(R.string.contentcreator));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,list);
        spinner.setAdapter(adapter);


        if(user.getAccountType()== SonataUser.ACCOUNT_TYPE_CONTENT_CREATOR){
            spinner.setSelection(1);
            prvAccountLayout.setVisibility(View.GONE);
        }
        else{
            spinner.setSelection(0);
            privateAccount = findViewById(R.id.settingprivateaccountswitch);
            privateAccount.setChecked(Objects.requireNonNull(GenelUtil.getCurrentUser()).getPrivate());
            onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        ProgressDialog progressDialog = new ProgressDialog(AdvancedSettingsActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.privaccount));
                        progressDialog.show();
                        HashMap<String,String> params = new HashMap<>();
                        ParseCloud.callFunctionInBackground("makeProfilePrivate", params, new FunctionCallback<HashMap>() {
                            @Override
                            public void done(HashMap object, ParseException e) {
                                if(!AdvancedSettingsActivity.this.isDestroyed()&&!AdvancedSettingsActivity.this.isFinishing()){
                                    if(e==null){
                                        progressDialog.dismiss();
                                    }
                                    else{
                                        GenelUtil.ToastLong(AdvancedSettingsActivity.this,getString(R.string.error));
                                        buttonView.setOnCheckedChangeListener(null);
                                        buttonView.setChecked(false);
                                        buttonView.setOnCheckedChangeListener(onCheckedChangeListener);
                                        progressDialog.dismiss();
                                    }
                                }
                            }
                        });
                    }
                    else{
                        ProgressDialog progressDialog = new ProgressDialog(AdvancedSettingsActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.privaccount));
                        progressDialog.show();
                        HashMap<String,String> params = new HashMap<>();
                        ParseCloud.callFunctionInBackground("makeProfileUnprivate", params, new FunctionCallback<HashMap>() {
                            @Override
                            public void done(HashMap object, ParseException e) {
                                if(!AdvancedSettingsActivity.this.isDestroyed()&&!AdvancedSettingsActivity.this.isFinishing()){
                                    if(e==null){
                                        progressDialog.dismiss();
                                    }
                                    else{
                                        GenelUtil.ToastLong(AdvancedSettingsActivity.this,getString(R.string.error));
                                        buttonView.setOnCheckedChangeListener(null);
                                        buttonView.setChecked(true);
                                        buttonView.setOnCheckedChangeListener(onCheckedChangeListener);
                                        progressDialog.dismiss();
                                    }
                                }
                            }
                        });

                    }
                }
            };
            privateAccount.setOnCheckedChangeListener(onCheckedChangeListener);
        }


        onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("Spinner Message :",list.get(position));
                if(position==1){
                    if(user.getAccountType()!=SonataUser.ACCOUNT_TYPE_CONTENT_CREATOR){
                        //Do something
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdvancedSettingsActivity.this);
                        builder.setTitle(R.string.swtocr);
                        builder.setMessage(R.string.swtocrdesc);
                        builder.setCancelable(true);
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spinner.setOnItemSelectedListener(null);
                                spinner.setSelection(0);
                                spinner.setOnItemSelectedListener(onItemSelectedListener);
                                dialog.dismiss();
                            }
                        });
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                spinner.setOnItemSelectedListener(null);
                                spinner.setSelection(0);
                                spinner.setOnItemSelectedListener(onItemSelectedListener);
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                                progressDialog.setMessage(getString(R.string.loading));
                                progressDialog.show();
                                HashMap<String, Object> params = new HashMap<String, Object>();
                                params.put("type",SonataUser.ACCOUNT_TYPE_CONTENT_CREATOR);
                                ParseCloud.callFunctionInBackground("switchAccountType", params, new FunctionCallback<HashMap>() {
                                    @Override
                                    public void done(HashMap object, ParseException e) {
                                        if(GenelUtil.isAlive(AdvancedSettingsActivity.this)){
                                            if(e==null){
                                                prvAccountLayout.setVisibility(View.GONE);
                                                progressDialog.dismiss();
                                                GenelUtil.ToastLong(AdvancedSettingsActivity.this,getString(R.string.atchanged));

                                            }
                                            else{
                                                spinner.setOnItemSelectedListener(null);
                                                spinner.setSelection(0);
                                                spinner.setOnItemSelectedListener(onItemSelectedListener);
                                                progressDialog.dismiss();
                                                GenelUtil.ToastLong(AdvancedSettingsActivity.this,getString(R.string.error));
                                            }
                                        }

                                    }
                                });


                            }
                        });
                        builder.show();
                    }
                }
                else{
                    if(user.getAccountType()==SonataUser.ACCOUNT_TYPE_CONTENT_CREATOR){
                        //Do something
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdvancedSettingsActivity.this);
                        builder.setTitle(R.string.swtopa);
                        builder.setMessage(R.string.swtopadesc);
                        builder.setCancelable(true);
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spinner.setOnItemSelectedListener(null);
                                spinner.setSelection(1);
                                spinner.setOnItemSelectedListener(onItemSelectedListener);
                                dialog.dismiss();
                            }
                        });

                        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();

                                progressDialog.setMessage(getString(R.string.loading));
                                progressDialog.show();
                                HashMap<String, Object> params = new HashMap<String, Object>();
                                params.put("type",SonataUser.ACCOUNT_TYPE_PERSONAL);
                                ParseCloud.callFunctionInBackground("switchAccountType", params, new FunctionCallback<HashMap>() {
                                    @Override
                                    public void done(HashMap object, ParseException e) {
                                        if(GenelUtil.isAlive(AdvancedSettingsActivity.this)){
                                            if(e==null){
                                                privateAccount = findViewById(R.id.settingprivateaccountswitch);
                                                prvAccountLayout.setVisibility(View.VISIBLE);
                                                privateAccount.setChecked(Objects.requireNonNull(GenelUtil.getCurrentUser()).getPrivate());
                                                onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if(isChecked){
                                                            ProgressDialog progressDialog = new ProgressDialog(AdvancedSettingsActivity.this);
                                                            progressDialog.setCancelable(false);
                                                            progressDialog.setMessage(getString(R.string.privaccount));
                                                            progressDialog.show();
                                                            HashMap<String,String> params = new HashMap<>();
                                                            ParseCloud.callFunctionInBackground("makeProfilePrivate", params, new FunctionCallback<HashMap>() {
                                                                @Override
                                                                public void done(HashMap object, ParseException e) {
                                                                    if(!AdvancedSettingsActivity.this.isDestroyed()&&!AdvancedSettingsActivity.this.isFinishing()){
                                                                        if(e==null){
                                                                            progressDialog.dismiss();
                                                                        }
                                                                        else{
                                                                            GenelUtil.ToastLong(AdvancedSettingsActivity.this,getString(R.string.error));
                                                                            buttonView.setOnCheckedChangeListener(null);
                                                                            buttonView.setChecked(false);
                                                                            buttonView.setOnCheckedChangeListener(onCheckedChangeListener);
                                                                            progressDialog.dismiss();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        else{
                                                            ProgressDialog progressDialog = new ProgressDialog(AdvancedSettingsActivity.this);
                                                            progressDialog.setCancelable(false);
                                                            progressDialog.setMessage(getString(R.string.privaccount));
                                                            progressDialog.show();
                                                            HashMap<String,String> params = new HashMap<>();
                                                            ParseCloud.callFunctionInBackground("makeProfileUnprivate", params, new FunctionCallback<HashMap>() {
                                                                @Override
                                                                public void done(HashMap object, ParseException e) {
                                                                    if(!AdvancedSettingsActivity.this.isDestroyed()&&!AdvancedSettingsActivity.this.isFinishing()){
                                                                        if(e==null){
                                                                            progressDialog.dismiss();
                                                                        }
                                                                        else{
                                                                            GenelUtil.ToastLong(AdvancedSettingsActivity.this,getString(R.string.error));
                                                                            buttonView.setOnCheckedChangeListener(null);
                                                                            buttonView.setChecked(true);
                                                                            buttonView.setOnCheckedChangeListener(onCheckedChangeListener);
                                                                            progressDialog.dismiss();
                                                                        }
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                };
                                                privateAccount.setOnCheckedChangeListener(onCheckedChangeListener);
                                                progressDialog.dismiss();
                                                GenelUtil.ToastLong(AdvancedSettingsActivity.this,getString(R.string.atchanged));

                                            }
                                            else{
                                                spinner.setOnItemSelectedListener(null);
                                                spinner.setSelection(1);
                                                spinner.setOnItemSelectedListener(onItemSelectedListener);
                                                progressDialog.dismiss();
                                                GenelUtil.ToastLong(AdvancedSettingsActivity.this,getString(R.string.error));
                                            }
                                        }

                                    }
                                });


                            }
                        });

                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                spinner.setOnItemSelectedListener(null);
                                spinner.setSelection(1);
                                spinner.setOnItemSelectedListener(onItemSelectedListener);
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner.setOnItemSelectedListener(onItemSelectedListener);

        changeEmail = findViewById(R.id.changeemail);
        changeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdvancedSettingsActivity.this,ChangeEmailActivity.class));
            }
        });

        changeUsername = findViewById(R.id.changeusername);
        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdvancedSettingsActivity.this,ChangeUsernameActivity.class));
            }
        });

        changePass = findViewById(R.id.changepass);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdvancedSettingsActivity.this,ChangePasswordActivity.class));
            }
        });
    }
}
