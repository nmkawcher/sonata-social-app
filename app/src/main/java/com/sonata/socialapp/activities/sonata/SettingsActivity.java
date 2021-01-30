package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.socialview.Hashtag;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.classes.SonataUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    RelativeLayout back,opensource,logout,blockedAccounts,pendingRequests
            ,likedPosts,yourComments,savedPosts,savedComments,changeUsername,prvAccountLayout;

    Switch nightMode,privateAccount;

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
        setContentView(R.layout.activity_settings);
        SonataUser user = (SonataUser) ParseUser.getCurrentUser();
        back = findViewById(R.id.backbuttonbutton);
        progressDialog = new ProgressDialog(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        prvAccountLayout = findViewById(R.id.praclayout);
        spinner = findViewById(R.id.settingsaccountypespinner);
        List<String> list = new ArrayList<>();

        list.add(getString(R.string.personal));
        list.add(getString(R.string.contentcreator));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item,list);
        spinner.setAdapter(adapter);


        if(user.getAccountType()==SonataUser.ACCOUNT_TYPE_CONTENT_CREATOR){
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
                        ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.privaccount));
                        progressDialog.show();
                        HashMap<String,String> params = new HashMap<>();
                        ParseCloud.callFunctionInBackground("makeProfilePrivate", params, new FunctionCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser object, ParseException e) {
                                if(!SettingsActivity.this.isDestroyed()&&!SettingsActivity.this.isFinishing()){
                                    if(e==null){
                                        progressDialog.dismiss();
                                    }
                                    else{
                                        GenelUtil.ToastLong(SettingsActivity.this,getString(R.string.error));
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
                        ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage(getString(R.string.privaccount));
                        progressDialog.show();
                        HashMap<String,String> params = new HashMap<>();
                        ParseCloud.callFunctionInBackground("makeProfileUnprivate", params, new FunctionCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser object, ParseException e) {
                                if(!SettingsActivity.this.isDestroyed()&&!SettingsActivity.this.isFinishing()){
                                    if(e==null){
                                        progressDialog.dismiss();
                                    }
                                    else{
                                        GenelUtil.ToastLong(SettingsActivity.this,getString(R.string.error));
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
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
                                        if(GenelUtil.isAlive(SettingsActivity.this)){
                                            if(e==null){
                                                prvAccountLayout.setVisibility(View.GONE);
                                                progressDialog.dismiss();
                                                GenelUtil.ToastLong(SettingsActivity.this,getString(R.string.atchanged));

                                            }
                                            else{
                                                spinner.setOnItemSelectedListener(null);
                                                spinner.setSelection(0);
                                                spinner.setOnItemSelectedListener(onItemSelectedListener);
                                                progressDialog.dismiss();
                                                GenelUtil.ToastLong(SettingsActivity.this,getString(R.string.error));
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
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
                                        if(GenelUtil.isAlive(SettingsActivity.this)){
                                            if(e==null){
                                                privateAccount = findViewById(R.id.settingprivateaccountswitch);
                                                prvAccountLayout.setVisibility(View.VISIBLE);
                                                privateAccount.setChecked(Objects.requireNonNull(GenelUtil.getCurrentUser()).getPrivate());
                                                onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
                                                    @Override
                                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                        if(isChecked){
                                                            ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
                                                            progressDialog.setCancelable(false);
                                                            progressDialog.setMessage(getString(R.string.privaccount));
                                                            progressDialog.show();
                                                            HashMap<String,String> params = new HashMap<>();
                                                            ParseCloud.callFunctionInBackground("makeProfilePrivate", params, new FunctionCallback<HashMap>() {
                                                                @Override
                                                                public void done(HashMap object, ParseException e) {
                                                                    if(!SettingsActivity.this.isDestroyed()&&!SettingsActivity.this.isFinishing()){
                                                                        if(e==null){
                                                                            progressDialog.dismiss();
                                                                        }
                                                                        else{
                                                                            GenelUtil.ToastLong(SettingsActivity.this,getString(R.string.error));
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
                                                            ProgressDialog progressDialog = new ProgressDialog(SettingsActivity.this);
                                                            progressDialog.setCancelable(false);
                                                            progressDialog.setMessage(getString(R.string.privaccount));
                                                            progressDialog.show();
                                                            HashMap<String,String> params = new HashMap<>();
                                                            ParseCloud.callFunctionInBackground("makeProfileUnprivate", params, new FunctionCallback<HashMap>() {
                                                                @Override
                                                                public void done(HashMap object, ParseException e) {
                                                                    if(!SettingsActivity.this.isDestroyed()&&!SettingsActivity.this.isFinishing()){
                                                                        if(e==null){
                                                                            progressDialog.dismiss();
                                                                        }
                                                                        else{
                                                                            GenelUtil.ToastLong(SettingsActivity.this,getString(R.string.error));
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
                                                GenelUtil.ToastLong(SettingsActivity.this,getString(R.string.atchanged));

                                            }
                                            else{
                                                spinner.setOnItemSelectedListener(null);
                                                spinner.setSelection(1);
                                                spinner.setOnItemSelectedListener(onItemSelectedListener);
                                                progressDialog.dismiss();
                                                GenelUtil.ToastLong(SettingsActivity.this,getString(R.string.error));
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

        changeUsername = findViewById(R.id.changeusername);
        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,ChangeUsernameActivity.class));
            }
        });

        savedComments = findViewById(R.id.savedcomments);
        savedComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,SavedCommentActivity.class));
            }
        });

        savedPosts = findViewById(R.id.savedposts);
        savedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,SavedPostsActivity.class));
            }
        });

        yourComments = findViewById(R.id.yourcomments);
        yourComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,MyOwnComments.class));
            }
        });

        likedPosts = findViewById(R.id.likedposts);
        likedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,PostYouHaveLikedActivity.class));

            }
        });

        pendingRequests = findViewById(R.id.pendingfollowreqs);
        pendingRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,PendingFollowRequestsActivity.class));
            }
        });

        blockedAccounts = findViewById(R.id.blockedaccounts);
        blockedAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,BlockedAccountsActivity.class));
            }
        });

        opensource = findViewById(R.id.opensourcelicences);
        opensource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OssLicensesMenuActivity.setActivityTitle(getString(R.string.opensourcelicences));

                startActivity(new Intent(SettingsActivity.this, OssLicensesMenuActivity.class));
            }
        });

        nightMode = findViewById(R.id.settingnightmodeswitch);
        nightMode.setChecked(GenelUtil.getNightMode());
        nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GenelUtil.saveNightMode(isChecked);
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_YES);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_NO);
                }
                /*Intent data = new Intent();
                data.putExtra("restart",true);
                setResult(RESULT_OK, data);
                onBackPressed();*/
            }
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SettingsActivity.this);
                alertDialog.setPositiveButton(getString(R.string.logout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = ParseUser.getCurrentUser().getObjectId();
                        ParseUser.logOut();
                        GenelUtil.removeUserFromCache(id,SettingsActivity.this);
                        startActivity(new Intent(SettingsActivity.this, StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        dialog.dismiss();
                        SettingsActivity.this.onBackPressed();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(true).setTitle(getString(R.string.logout)).show();

            }
        });



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
