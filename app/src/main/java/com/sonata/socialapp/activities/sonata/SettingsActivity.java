package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.classes.SonataUser;

public class SettingsActivity extends AppCompatActivity {

    RelativeLayout back,opensource,logout,blockedAccounts,pendingRequests
            ,likedPosts,yourComments,savedPosts,savedComments, advancedSettings;

    SwitchCompat nightMode;


    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Util.getNightMode()){
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

        advancedSettings = findViewById(R.id.advancedsettings);
        advancedSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,AdvancedSettingsActivity.class));
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
        nightMode.setChecked(Util.getNightMode());
        nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Util.saveNightMode(isChecked);
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
                        Util.removeUserFromCache(id,SettingsActivity.this);
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
