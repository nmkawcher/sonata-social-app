package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseDecoder;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.fragments.GroupFragment;
import com.sonata.socialapp.fragments.DiscoverFragment;
import com.sonata.socialapp.fragments.HomeFragment;
import com.sonata.socialapp.fragments.NotifFragment;
import com.sonata.socialapp.fragments.ProfilFragment;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


import cn.jzvd.Jzvd;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemReselectedListener,BottomNavigationView.OnNavigationItemSelectedListener {


    FrameLayout homeframe,backyardframe,notifframe,profileframe,discoverframe;

    BottomNavigationViewEx bottombar;
    QBadgeView badgeView = null;
    private int code=1547;




    private int activitySettings=2658;



    @Override
    protected void onDestroy() {
        badgeView=null;
        bottombar.setOnNavigationItemSelectedListener(null);
        bottombar=null;
        super.onDestroy();
    }

    public void homeBackPress(){
        if(getSupportFragmentManager().findFragmentById(R.id.homeframehome)!=null){
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.homeframehome)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.homeframeprofile)!=null){
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.homeframeprofile)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.homeframenotif)!=null){
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.homeframenotif)).commit();
        }
        if(getSupportFragmentManager().findFragmentById(R.id.homeframebackyard)!=null){
            getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.homeframebackyard)).commit();
        }
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(SonataUser.getCurrentUser()==null){
            finish();
            return;
        }
        if(bottombar!=null){
            if(bottombar.getCurrentItem()==0){
                if(getSupportFragmentManager().findFragmentById(R.id.homeframehome)!=null){
                    ((HomeFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.homeframehome))).notifyAdapter();
                }
            }
            else if(bottombar.getCurrentItem()==2){
                if(getSupportFragmentManager().findFragmentById(R.id.homeframenotif)!=null){
                    NotifFragment homeFragment = (NotifFragment) getSupportFragmentManager().findFragmentById(R.id.homeframenotif);
                    assert homeFragment != null;
                    homeFragment.notifyAdapter();
                }
            }
            else if(bottombar.getCurrentItem()==3){
                if(getSupportFragmentManager().findFragmentById(R.id.homeframeprofile)!=null){
                    ((ProfilFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.homeframeprofile))).notifyAdapter();
                }
            }

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyApp.ses=false;
        //GenelUtil.ToastLong(this,Build.MODEL);
        if(getIntent() != null && getIntent().getBooleanExtra("newregister",false)){
            startActivity(new Intent(this,EditProfileActivity.class));
        }
        HashMap<String,String> params = new HashMap<>();
        ParseCloud.callFunctionInBackground("refreshOwnProfile", params, new FunctionCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if(e==null){
                    if(Objects.requireNonNull(GenelUtil.getCurrentUser()).getNotifCount()>0){
                        addBadgeAt(2,(int)GenelUtil.getCurrentUser().getNotifCount());
                    }
                    if(getSupportFragmentManager().findFragmentById(R.id.homeframeprofile)!=null){
                        ProfilFragment homeFragment = (ProfilFragment) getSupportFragmentManager().findFragmentById(R.id.homeframeprofile);
                        assert homeFragment != null;
                        homeFragment.setProfile((SonataUser) object);
                    }
                }

            }
        });


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        HashMap<String,String> hash = new HashMap<>();
                        hash.put("token",token);
                        hash.put("lang", Locale.getDefault().getLanguage());
                        ParseCloud.callFunctionInBackground("saveUserDeviceToken",hash);

                    }
                });




        bottombar = findViewById(R.id.bottomnav);
        bottombar.enableAnimation(false);
        bottombar.enableItemShiftingMode(false);
        bottombar.enableShiftingMode(false);
        bottombar.setIconSize(28,28);





        homeframe=findViewById(R.id.homeframehome);
        discoverframe = findViewById(R.id.homeframediscover);
        backyardframe=findViewById(R.id.homeframebackyard);
        notifframe=findViewById(R.id.homeframenotif);
        profileframe=findViewById(R.id.homeframeprofile);


        if(getSupportFragmentManager().findFragmentById(R.id.homeframehome)==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.homeframehome,new HomeFragment()).commit();
        }

        bottombar.setOnNavigationItemSelectedListener(this);

        bottombar.setOnNavigationItemReselectedListener(this);

        int pos = bottombar.getCurrentItem();

        if(pos==0){

            homeframe.setVisibility(View.VISIBLE);
            backyardframe.setVisibility(View.INVISIBLE);
            notifframe.setVisibility(View.INVISIBLE);
            discoverframe.setVisibility(View.INVISIBLE);
            profileframe.setVisibility(View.INVISIBLE);

        }
        /*if(pos==1){
            homeframe.setVisibility(View.INVISIBLE);
            backyardframe.setVisibility(View.VISIBLE);
            notifframe.setVisibility(View.INVISIBLE);
            discoverframe.setVisibility(View.INVISIBLE);
            profileframe.setVisibility(View.INVISIBLE);
        }*/
        if(pos==1){
            homeframe.setVisibility(View.INVISIBLE);
            backyardframe.setVisibility(View.INVISIBLE);
            notifframe.setVisibility(View.INVISIBLE);
            discoverframe.setVisibility(View.VISIBLE);
            profileframe.setVisibility(View.INVISIBLE);
        }
        if(pos==2){
            homeframe.setVisibility(View.INVISIBLE);
            backyardframe.setVisibility(View.INVISIBLE);
            notifframe.setVisibility(View.VISIBLE);
            discoverframe.setVisibility(View.INVISIBLE);
            profileframe.setVisibility(View.INVISIBLE);

        }
        if(pos==3){
            homeframe.setVisibility(View.INVISIBLE);
            backyardframe.setVisibility(View.INVISIBLE);
            notifframe.setVisibility(View.INVISIBLE);
            discoverframe.setVisibility(View.INVISIBLE);
            profileframe.setVisibility(View.VISIBLE);
        }

        bottombar.getBottomNavigationItemView(0).performClick();






    }



    private Badge addBadgeAt(int position, int number) {
        // add badge
        if (badgeView == null){
            badgeView = new QBadgeView(this);
        }

        badgeView.setBadgeNumber(number)
                .setGravityOffset(15, 2, true)
                .bindTarget(bottombar.getBottomNavigationItemView(position))
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {


                    }
                });

        return badgeView;
    }

    public void startActivityResult(){
        startActivityForResult(new Intent(MainActivity.this,UploadActivity.class),code);
    }

    public void homeFragmentComment(Post post){
        startActivity(new Intent(this,CommentActivity.class)
                .putExtra("post",post));
    }

    public void profileFragmentComment(Post post ){
        startActivity(new Intent(this,CommentActivity.class)
                        .putExtra("post",post));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==code){
                if(data!=null){
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage(getString(R.string.uploaddonewaitfor))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.okk), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }

                            })
                            .show();
                    /*Snackbar snackbar = Snackbar.make(bottombar,getString(R.string.uploadisdone),Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(getResources().getColor(R.color.blue));
                    //2131231300
                    snackbar.setAction(getString(R.string.show), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this,CommentActivity.class)
                                            .putExtra("post",(Post)data.getParcelableExtra("post")));
                        }
                    });
                    snackbar.show();*/
                }

            }
            if(requestCode==activitySettings){
                //do stuff
                if(data!=null){
                    if(data.getBooleanExtra("restart",false)){
                        startActivity(new Intent(this,StartActivity.class));
                        if(getSupportFragmentManager().findFragmentById(R.id.homeframehome)!=null){
                            ((HomeFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.homeframehome))).backPress();
                        }
                        else{
                            finish();
                        }
                    }
                }
            }



        }

    }

    public void startSettingsActivity(){
        startActivityForResult(new Intent(this, SettingsActivity.class),activitySettings);
    }



    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    public void backPressProfile(){
        if(bottombar!=null){
            bottombar.setCurrentItem(0);
        }
    }




    @Override
    public void onBackPressed() {

        if(bottombar!=null){
            if(bottombar.getCurrentItem()==3){
                if(getSupportFragmentManager().findFragmentById(R.id.homeframeprofile)!=null){
                    ProfilFragment homeFragment = (ProfilFragment) getSupportFragmentManager().findFragmentById(R.id.homeframeprofile);
                    assert homeFragment != null;
                    homeFragment.backPressed();
                }
            }
            else if(bottombar.getCurrentItem()==2){
                bottombar.setCurrentItem(0);
            }
            else if(bottombar.getCurrentItem()==1){
                bottombar.setCurrentItem(0);
            }
            else if(bottombar.getCurrentItem()==0){
                ((HomeFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.homeframehome))).backPress();
            }


        }
        else{
            super.onBackPressed();
        }
    }



    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        Jzvd.releaseAllVideos();
        if(item.getItemId()==R.id.tab_home){
            if(getSupportFragmentManager().findFragmentById(R.id.homeframehome)!=null){
                HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.homeframehome);
                assert homeFragment != null;
                homeFragment.Refresh();
            }


        }
        if(item.getItemId()==R.id.tab_back_yard){
            if(getSupportFragmentManager().findFragmentById(R.id.homeframebackyard)!=null){
                GroupFragment groupFragment = (GroupFragment) getSupportFragmentManager().findFragmentById(R.id.homeframebackyard);
                //backyardFragment.Refresh();
            }
        }
        if(item.getItemId()==R.id.tab_discover){
            if(getSupportFragmentManager().findFragmentById(R.id.homeframediscover)!=null){
                DiscoverFragment backyardFragment = (DiscoverFragment) getSupportFragmentManager().findFragmentById(R.id.homeframediscover);
                assert backyardFragment != null;
                //backyardFragment.Refresh();
            }
        }
        if(item.getItemId()==R.id.tab_notif){
            if(getSupportFragmentManager().findFragmentById(R.id.homeframenotif)!=null){
                NotifFragment homeFragment = (NotifFragment) getSupportFragmentManager().findFragmentById(R.id.homeframenotif);
                assert homeFragment != null;
                homeFragment.Refresh();
            }
        }
        if(item.getItemId()==R.id.tab_profile){
            if(getSupportFragmentManager().findFragmentById(R.id.homeframeprofile)!=null){
                ProfilFragment homeFragment = (ProfilFragment) getSupportFragmentManager().findFragmentById(R.id.homeframeprofile);
                assert homeFragment != null;
                homeFragment.Refresh();
            }

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Jzvd.releaseAllVideos();
        if(item.getItemId()==R.id.tab_home){


            if(getSupportFragmentManager().findFragmentById(R.id.homeframehome)==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.homeframehome,new HomeFragment()).commit();

            }
            else{
                ((HomeFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.homeframehome))).notifyAdapter();

            }


            homeframe.setVisibility(View.VISIBLE);
            backyardframe.setVisibility(View.INVISIBLE);
            discoverframe.setVisibility(View.INVISIBLE);
            notifframe.setVisibility(View.INVISIBLE);
            profileframe.setVisibility(View.INVISIBLE);

        }
        if(item.getItemId()==R.id.tab_back_yard){
            if(getSupportFragmentManager().findFragmentById(R.id.homeframebackyard)==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.homeframebackyard,new GroupFragment()).commit();
            }



            homeframe.setVisibility(View.INVISIBLE);
            backyardframe.setVisibility(View.VISIBLE);
            discoverframe.setVisibility(View.INVISIBLE);
            notifframe.setVisibility(View.INVISIBLE);
            profileframe.setVisibility(View.INVISIBLE);
        }
        if(item.getItemId()==R.id.tab_discover){


            if(getSupportFragmentManager().findFragmentById(R.id.homeframediscover)==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.homeframediscover,new DiscoverFragment()).commit();

            }
            else{
                //((DiscoverFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.homeframediscover))).notifyAdapter();

            }


            homeframe.setVisibility(View.INVISIBLE);
            backyardframe.setVisibility(View.INVISIBLE);
            discoverframe.setVisibility(View.VISIBLE);
            notifframe.setVisibility(View.INVISIBLE);
            profileframe.setVisibility(View.INVISIBLE);

        }
        if(item.getItemId()==R.id.tab_notif){
            if(getSupportFragmentManager().findFragmentById(R.id.homeframenotif)==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.homeframenotif,new NotifFragment()).commit();
            }
            else{
                ((NotifFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.homeframenotif))).notifyAdapter();
            }



            homeframe.setVisibility(View.INVISIBLE);
            backyardframe.setVisibility(View.INVISIBLE);
            discoverframe.setVisibility(View.INVISIBLE);
            notifframe.setVisibility(View.VISIBLE);
            profileframe.setVisibility(View.INVISIBLE);
            if(badgeView!=null){
                badgeView.hide(true);
                badgeView=null;

                ParseCloud.callFunctionInBackground("notifReset",new HashMap<>());
            }

        }
        if(item.getItemId()==R.id.tab_profile){


            if(getSupportFragmentManager().findFragmentById(R.id.homeframeprofile)==null){
                getSupportFragmentManager().beginTransaction().replace(R.id.homeframeprofile,new ProfilFragment()).commit();


            }
            else{

                ((ProfilFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.homeframeprofile))).notifyAdapter();

            }


            homeframe.setVisibility(View.INVISIBLE);
            backyardframe.setVisibility(View.INVISIBLE);
            discoverframe.setVisibility(View.INVISIBLE);
            notifframe.setVisibility(View.INVISIBLE);
            profileframe.setVisibility(View.VISIBLE);
        }
        return true;
    }
}
