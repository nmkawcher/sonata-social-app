package com.sonata.socialapp.activities.groups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.CommentActivity;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;
import com.sonata.socialapp.activities.sonata.HashtagActivity;
import com.sonata.socialapp.activities.sonata.MainActivity;
import com.sonata.socialapp.activities.sonata.SavedPostsActivity;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.GroupSafPostAdapter;
import com.sonata.socialapp.utils.adapters.HomeAdapter;
import com.sonata.socialapp.utils.adapters.SafPostAdapter;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.classes.Group;
import com.sonata.socialapp.utils.classes.GroupPost;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.interfaces.GroupRecyclerViewClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class GroupActivity extends AppCompatActivity implements GroupRecyclerViewClick {

    Group group;

    TextView groupName, groupNameBottom, groupDescription, groupMemberCount,buttonText;
    ImageView groupBanner;

    RelativeLayout adminPanel, adminPanelRipple, privateLayout, joinButton, back, joinButtonRipple;

    FloatingActionButton upload;



    private final int code = 8745;

    private final long SHARE_TYPE_ADMIN_APPROVAL = 1;

    List<ListObject> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    GroupSafPostAdapter adapter;
    private boolean loading=true;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> listreklam;

    Date date;
    RecyclerView.OnScrollListener onScrollListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        group = getIntent().getParcelableExtra("group");

        groupName = findViewById(R.id.profileusernametext);
        groupBanner = findViewById(R.id.group_banner);
        groupNameBottom = findViewById(R.id.group_name_bottom);

        adminPanel = findViewById(R.id.admin_panel);
        adminPanelRipple = findViewById(R.id.adminpanelripple);


        groupDescription = findViewById(R.id.group_description);
        privateLayout = findViewById(R.id.group_private_layout);
        groupMemberCount = findViewById(R.id.group_member_number);
        joinButton = findViewById(R.id.join_group_button);
        joinButtonRipple = findViewById(R.id.join_group_button_ripple);
        buttonText = findViewById(R.id.followButtonText);

        upload = findViewById(R.id.uploadbutton);






        setUpGroup(group);

        refreshGroup(group.getId());

        recyclerView = findViewById(R.id.grouprecyclerview);

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(recyclerView!=null&&linearLayoutManager!=null){
                        AutoPlayUtils.onScrollPlayVideo(recyclerView, R.id.masterExoPlayer, linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition());
                        if(!recyclerView.canScrollVertically(-1)){
                            Jzvd.releaseAllVideos();
                        }
                    }
                }


            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){
                    if(dy>5){
                        upload.hide();
                    }

                    if(linearLayoutManager.findLastVisibleItemPosition()>(list.size()-7)&&!loading&&!postson){
                        loading=true;
                        getReqs(date,false);
                    }

                }

                if(dy<-5){
                    upload.show();

                }
            }
        };


        list=new ArrayList<>();
        listreklam = new ArrayList<>();
        ListObject object2 = new ListObject();
        object2.setType("load");
        list.add(object2);
        linearLayoutManager=new LinearLayoutManager(GroupActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new GroupSafPostAdapter();
        adapter.setContext(list, Glide.with(GroupActivity.this),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();





        recyclerView.addOnScrollListener(onScrollListener);

        if(GenelUtil.isAlive(this)){
            getReqs(null,false);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }




    private void getReqs(Date date,boolean isRefresh){
        if(GenelUtil.isAlive(this)){
            HashMap<String, Object> params = new HashMap<>();
            if(date!=null){
                params.put("date",date);
            }
            params.put("groupid",group.getId());
            ParseCloud.callFunctionInBackground("getGroupPosts", params, (FunctionCallback<List<GroupPost>>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(GroupActivity.this)){
                    if(e==null){

                        if(objects!= null){
                            if(isRefresh){
                                listreklam.clear();
                                listreklam = null;
                                listreklam=new ArrayList<>();
                            }
                            getAds(objects,isRefresh);
                            //initList(objects);
                        }



                    }
                    else{
                        getReqs(date,isRefresh);


                    }
                }
            });
        }
    }

    private void initList(List<GroupPost> objects, List<UnifiedNativeAd> listreklam) {
        Log.e("done","InitList");

        if(GenelUtil.isAlive(GroupActivity.this)){
            Log.e("done","InitListActive");

            if(objects.size()==0){
                postson =true;
                loading =false;
                if(list!=null){
                    if(list.size()==0){
                        ListObject post = new ListObject();
                        post.setType("boş");
                        list.add(post);
                    }
                    else{
                        if(list.get(list.size()-1).getType().equals("load")){
                            list.remove(list.get(list.size()-1));
                        }
                        ListObject post = new ListObject();
                        post.setType("boş");
                        list.add(post);
                    }
                }

                adapter.notifyDataSetChanged();
                Log.e("done","adapterNotified");


            }
            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.get(list.size()-1));
                    }
                }

                date=objects.get(objects.size()-1).getDate("date");
                if(objects.size()<40){
                    postson =true;
                    loading =false;
                    for(int i=0;i<objects.size();i++){

                        if(list.size()<3){
                            if((list.size()+1)%3==0&&list.size()!=0){
                                if(listreklam.size()>0){
                                    ListObject reklam = new ListObject();
                                    reklam.setType("reklam");
                                    reklam.setAd(listreklam.get(0));
                                    listreklam.remove(0);
                                    list.add(reklam);
                                    i--;
                                }
                                else{
                                    ListObject post = new ListObject();
                                    post.setType(objects.get(i).getType());
                                    GroupPost p2 = objects.get(i);
                                    p2.setVote(p2.getVote2());
                                    p2.setCommentnumber(p2.getCommentnumber2());
                                    p2.setUpvote(p2.getUpvote2());
                                    p2.setCommentable(p2.getCommentable2());
                                    p2.setDownvote(p2.getDownvote2());
                                    p2.setSaved(p2.getSaved2());
                                    post.setGroupPost(p2);
                                    list.add(post);
                                }

                            }
                            else{
                                ListObject post = new ListObject();
                                post.setType(objects.get(i).getType());
                                GroupPost p2 = objects.get(i);
                                p2.setVote(p2.getVote2());
                                p2.setCommentnumber(p2.getCommentnumber2());
                                p2.setUpvote(p2.getUpvote2());
                                p2.setCommentable(p2.getCommentable2());
                                p2.setDownvote(p2.getDownvote2());
                                p2.setSaved(p2.getSaved2());
                                post.setGroupPost(p2);
                                list.add(post);
                            }
                        }
                        else{
                            if((list.size()+1)%10==0&&list.size()!=0){
                                if(listreklam.size()>0){
                                    if(listreklam.size()>0){
                                        ListObject reklam = new ListObject();
                                        reklam.setType("reklam");
                                        reklam.setAd(listreklam.get(0));
                                        listreklam.remove(0);
                                        list.add(reklam);
                                        i--;
                                    }
                                    else{
                                        ListObject post = new ListObject();
                                        post.setType(objects.get(i).getType());
                                        GroupPost p2 = objects.get(i);
                                        p2.setVote(p2.getVote2());
                                        p2.setCommentnumber(p2.getCommentnumber2());
                                        p2.setUpvote(p2.getUpvote2());
                                        p2.setCommentable(p2.getCommentable2());
                                        p2.setDownvote(p2.getDownvote2());
                                        p2.setSaved(p2.getSaved2());
                                        post.setGroupPost(p2);
                                        list.add(post);
                                    }
                                }
                                else{
                                    ListObject post = new ListObject();
                                    post.setType(objects.get(i).getType());
                                    GroupPost p2 = objects.get(i);
                                    p2.setVote(p2.getVote2());
                                    p2.setCommentnumber(p2.getCommentnumber2());
                                    p2.setUpvote(p2.getUpvote2());
                                    p2.setCommentable(p2.getCommentable2());
                                    p2.setDownvote(p2.getDownvote2());
                                    p2.setSaved(p2.getSaved2());
                                    post.setGroupPost(p2);
                                    list.add(post);
                                }
                            }
                            else{
                                ListObject post = new ListObject();
                                post.setType(objects.get(i).getType());
                                GroupPost p2 = objects.get(i);
                                p2.setVote(p2.getVote2());
                                p2.setCommentnumber(p2.getCommentnumber2());
                                p2.setUpvote(p2.getUpvote2());
                                p2.setCommentable(p2.getCommentable2());
                                p2.setDownvote(p2.getDownvote2());
                                p2.setSaved(p2.getSaved2());
                                post.setGroupPost(p2);
                                list.add(post);

                            }
                        }

                    }

                    adapter.notifyDataSetChanged();
                    Log.e("done","adapterNotified");

                    loading =false;


                }
                else{
                    for(int i=0;i<objects.size();i++){
                        if(list.size()<3){
                            if((list.size()+1)%3==0&&list.size()!=0){
                                if(listreklam.size()>0){
                                    ListObject reklam = new ListObject();
                                    reklam.setType("reklam");
                                    reklam.setAd(listreklam.get(0));
                                    listreklam.remove(0);
                                    list.add(reklam);
                                    i--;
                                }
                                else{
                                    ListObject post = new ListObject();
                                    post.setType(objects.get(i).getType());
                                    GroupPost p2 = objects.get(i);
                                    p2.setVote(p2.getVote2());
                                    p2.setCommentnumber(p2.getCommentnumber2());
                                    p2.setUpvote(p2.getUpvote2());
                                    p2.setCommentable(p2.getCommentable2());
                                    p2.setDownvote(p2.getDownvote2());
                                    p2.setSaved(p2.getSaved2());
                                    post.setGroupPost(p2);
                                    list.add(post);
                                }

                            }
                            else{
                                ListObject post = new ListObject();
                                post.setType(objects.get(i).getType());
                                GroupPost p2 = objects.get(i);
                                p2.setVote(p2.getVote2());
                                p2.setCommentnumber(p2.getCommentnumber2());
                                p2.setUpvote(p2.getUpvote2());
                                p2.setCommentable(p2.getCommentable2());
                                p2.setDownvote(p2.getDownvote2());
                                p2.setSaved(p2.getSaved2());
                                post.setGroupPost(p2);
                                list.add(post);
                            }
                        }
                        else{
                            if((list.size()+1)%10==0&&list.size()!=0){
                                if(listreklam.size()>0){
                                    if(listreklam.size()>0){
                                        ListObject reklam = new ListObject();
                                        reklam.setType("reklam");
                                        reklam.setAd(listreklam.get(0));
                                        listreklam.remove(0);
                                        list.add(reklam);
                                        i--;
                                    }
                                    else{
                                        ListObject post = new ListObject();
                                        post.setType(objects.get(i).getType());
                                        GroupPost p2 = objects.get(i);
                                        p2.setVote(p2.getVote2());
                                        p2.setCommentnumber(p2.getCommentnumber2());
                                        p2.setUpvote(p2.getUpvote2());
                                        p2.setCommentable(p2.getCommentable2());
                                        p2.setDownvote(p2.getDownvote2());
                                        p2.setSaved(p2.getSaved2());
                                        post.setGroupPost(p2);
                                        list.add(post);
                                    }
                                }
                                else{
                                    ListObject post = new ListObject();
                                    post.setType(objects.get(i).getType());
                                    GroupPost p2 = objects.get(i);
                                    p2.setVote(p2.getVote2());
                                    p2.setCommentnumber(p2.getCommentnumber2());
                                    p2.setUpvote(p2.getUpvote2());
                                    p2.setCommentable(p2.getCommentable2());
                                    p2.setDownvote(p2.getDownvote2());
                                    p2.setSaved(p2.getSaved2());
                                    post.setGroupPost(p2);
                                    list.add(post);
                                }
                            }
                            else{
                                ListObject post = new ListObject();
                                post.setType(objects.get(i).getType());
                                GroupPost p2 = objects.get(i);
                                p2.setVote(p2.getVote2());
                                p2.setCommentnumber(p2.getCommentnumber2());
                                p2.setUpvote(p2.getUpvote2());
                                p2.setCommentable(p2.getCommentable2());
                                p2.setDownvote(p2.getDownvote2());
                                p2.setSaved(p2.getSaved2());
                                post.setGroupPost(p2);
                                list.add(post);

                            }
                        }


                    }



                    ListObject load = new ListObject();
                    load.setType("load");
                    list.add(load);

                    adapter.notifyDataSetChanged();
                    Log.e("done","adapterNotified");


                    loading =false;
                }

            }
        }
        else{
            Log.e("done","InitListNotActive");

        }

    }
    int loadCheck = 0;
    private void getAds(List<GroupPost> objects,boolean isRefresh){
        Log.e("done","doneGetAds");

        if(GenelUtil.isAlive(GroupActivity.this)){
            Log.e("done","doneGetAdsActive");



            int l = objects.size();
            int c = 0;
            if(l>2&&l<10){
                c=1;
            }
            if(l>=10&&l<20){
                c=2;
            }
            if(l>=20&&l<30){
                c=3;
            }
            if(l>=30&&l<40){
                c=4;
            }
            if(l>=40){
                c=5;
            }
            Log.e("Ad Count",""+c);
            if(c<=0){
                if(isRefresh){
                    //refreshSetting();
                    list.clear();
                }
                initList(objects,new ArrayList<>());
            }
            else{
                int finalC = c;
                final boolean[] isfinish = {false};
                List<UnifiedNativeAd> tempList = new ArrayList<>();
                AdLoader adLoader = new AdLoader.Builder(GroupActivity.this, getString(R.string.adId))
                        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                loadCheck++;
                                if(GenelUtil.isAlive(GroupActivity.this)){
                                    Log.e("done","AdLoadDoneActive");

                                    Log.e("Ad Load Done #","True");
                                    listreklam.add(unifiedNativeAd);
                                    tempList.add(unifiedNativeAd);

                                }
                                else{
                                    unifiedNativeAd.destroy();
                                }
                                if(loadCheck == finalC){
                                    isfinish[0] = true;
                                    Log.e("Ad Load Done #","False");
                                    if(GenelUtil.isAlive(GroupActivity.this)){
                                        if(isRefresh){
                                            //refreshSetting();
                                            list.clear();

                                        }
                                        loadCheck=0;
                                        initList(objects,tempList);
                                    }

                                }
                            }
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                loadCheck++;
                                Log.e("adError: ",""+adError.getCode());
                                Log.e("adError: ",""+adError.getCause());


                                if(loadCheck==finalC){
                                    if(!isfinish[0]){
                                        isfinish[0] = true;
                                        Log.e("adError !isLoading: ",""+adError.getCode());
                                        if(isRefresh){
                                            //refreshSetting();
                                            list.clear();

                                        }
                                        loadCheck=0;
                                        initList(objects,tempList);
                                    }

                                }
                            }
                        }).build();
                Log.e("Delay Öncesi zaman : ",System.currentTimeMillis()+"");
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!isfinish[0]){
                            isfinish[0] = true;
                            Log.e("Delay Öncesi zaman : ",System.currentTimeMillis()+"");

                            if(GenelUtil.isAlive(GroupActivity.this)){
                                if(isRefresh){
                                    //refreshSetting();
                                    list.clear();

                                }
                                loadCheck=0;
                                initList(objects,new ArrayList<>());
                            }


                        }
                    }
                }, Math.max(finalC * 4000, 7000));

                adLoader.loadAds(new AdRequest.Builder().build(), finalC);
            }






        }
        else{
            Log.e("done","doneGetNotActive");

        }

    }



    private void setUpGroup(Group group){

        if(!group.getAdmins().contains(ParseUser.getCurrentUser().getObjectId())){
            adminPanel.setVisibility(View.INVISIBLE);
        }
        Glide.with(this).load(group.getImageUrl()).into(groupBanner);
        groupName.setText(group.getName());
        groupNameBottom.setText(group.getName());
        groupDescription.setText(group.getDescription());
        if(!group.getPrivate()){
            privateLayout.setVisibility(View.INVISIBLE);
        }
        groupMemberCount.setText(GenelUtil.ConvertNumber((int)group.getMemberCount(),this));


        if(group.getJoin()){
            buttonText.setText(getString(R.string.leavegroup));
            buttonText.setTextColor(getResources().getColor(R.color.white));
            joinButton.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
        }
        else{
            if(group.getJoinRequest()){
                buttonText.setText(getString(R.string.requestsent));
                buttonText.setTextColor(getResources().getColor(R.color.white));
                joinButton.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
            }
            else{
                buttonText.setText(getString(R.string.joingroup));
                buttonText.setTextColor(getResources().getColor(R.color.blue));
                joinButton.setBackground(getResources().getDrawable(R.drawable.button_background));
            }

        }



    }

    private void setUpButtonClicks(Group group){
        if(group.getAdmins().contains(ParseUser.getCurrentUser().getObjectId())){
            adminPanelRipple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(GenelUtil.clickable(500)){
                        if(group.getAdmins().contains(ParseUser.getCurrentUser().getObjectId())){

                        }
                    }
                }
            });
        }


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GenelUtil.clickable(500)){
                    if(group.getJoin()){
                        startActivityForResult(new Intent(GroupActivity.this,GroupUploadActivity.class).putExtra("group",group),code);
                    }
                }
            }
        });





        joinButtonRipple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GenelUtil.clickable(500)){
                    if(buttonText.getText().toString().equals(getString(R.string.joingroup))){
                        if(group.getPrivate()){
                            //Takip İsteği gönder
                            buttonText.setText(getString(R.string.loading));
                            HashMap<String,String> params = new HashMap<>();
                            params.put("id",group.getId());
                            ParseCloud.callFunctionInBackground("sendJoinRequestToGroup", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {
                                    if(GenelUtil.isAlive(GroupActivity.this)){
                                        if(e==null){
                                            joinButton.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
                                            group.setJoinRequest(true);
                                            buttonText.setText(getString(R.string.requestsent));
                                            buttonText.setTextColor(Color.WHITE);
                                        }
                                        else{
                                            buttonText.setText(getString(R.string.joingroup));
                                            GenelUtil.ToastLong(GroupActivity.this,getString(R.string.error));
                                        }
                                    }

                                }
                            });
                        }
                        else{
                            //takip et
                            buttonText.setText(getString(R.string.loading));
                            HashMap<String,String> params = new HashMap<>();
                            params.put("id",group.getId());
                            ParseCloud.callFunctionInBackground("joinGroup", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {
                                    if(GenelUtil.isAlive(GroupActivity.this)){
                                        if(e==null){
                                            group.setJoin(true);
                                            buttonText.setTextColor(getResources().getColor(R.color.white));
                                            joinButton.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
                                            buttonText.setText(getString(R.string.leavegroup));
                                        }
                                        else{
                                            buttonText.setText(getString(R.string.joingroup));
                                            GenelUtil.ToastLong(GroupActivity.this,getString(R.string.error));
                                        }
                                    }

                                }
                            });

                        }
                    }
                    else if(buttonText.getText().toString().equals(getString(R.string.leavegroup))){
                        if(group.getOwner().equals(ParseUser.getCurrentUser().getObjectId())){
                            GenelUtil.ToastLong(GroupActivity.this,getString(R.string.youmusttransferownershipofgroup));
                            return;
                        }
                        if(group.getAdmins().contains(ParseUser.getCurrentUser().getObjectId())){
                            GenelUtil.ToastLong(GroupActivity.this,getString(R.string.youmustdropadminrights));
                            return;
                        }
                        buttonText.setText(getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("id",group.getId());
                        ParseCloud.callFunctionInBackground("leaveGroup", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(GenelUtil.isAlive(GroupActivity.this)){
                                    if(e==null){
                                        group.setJoin(true);
                                        buttonText.setTextColor(getResources().getColor(R.color.blue));
                                        joinButton.setBackground(getResources().getDrawable(R.drawable.button_background));
                                        buttonText.setText(getString(R.string.joingroup));
                                    }
                                    else{
                                        buttonText.setText(getString(R.string.leavegroup));
                                        GenelUtil.ToastLong(GroupActivity.this,getString(R.string.error));
                                    }
                                }

                            }
                        });
                    }
                    else if(buttonText.getText().toString().equals(getString(R.string.requestsent))){
                        buttonText.setText(getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("id",group.getId());
                        ParseCloud.callFunctionInBackground("removeJoinRequestToGroup", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(GenelUtil.isAlive(GroupActivity.this)){
                                    if(e==null){
                                        group.setJoin(true);
                                        buttonText.setTextColor(getResources().getColor(R.color.blue));
                                        joinButton.setBackground(getResources().getDrawable(R.drawable.button_background));
                                        buttonText.setText(getString(R.string.joingroup));
                                    }
                                    else{
                                        buttonText.setText(getString(R.string.requestsent));
                                        GenelUtil.ToastLong(GroupActivity.this,getString(R.string.error));
                                    }
                                }

                            }
                        });
                    }
                }
            }
        });


    }

    private void refreshGroup(String id){

        HashMap<String, Object> params = new HashMap<>();
        params.put("id",id);
        ParseCloud.callFunctionInBackground("refreshGroup", params, (FunctionCallback<Group>) (object, e) -> {
            Log.e("done","done");
            if(GenelUtil.isAlive(GroupActivity.this)){
                if(e==null){
                    if(object!= null){
                        object.setJoin(object.getJoin2());
                        object.setJoinRequest(object.getJoinRequest2());
                        group = object;
                        setUpGroup(group);

                        setUpButtonClicks(group);
                    }
                }
                else{
                    if(e.getCode()== ParseException.CONNECTION_FAILED){
                        refreshGroup(id);
                    }
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(group!=null){
            setUpGroup(group);
        }
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if (requestCode == code) {
                if (data != null) {
                    if(group.getShareType()==SHARE_TYPE_ADMIN_APPROVAL){
                        new AlertDialog.Builder(GroupActivity.this)
                                .setMessage(getString(R.string.uploaddonewaitforadmin))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okk), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }

                                })
                                .show();
                    }
                    else{
                        new AlertDialog.Builder(GroupActivity.this)
                                .setMessage(getString(R.string.uploaddonewaitfor))
                                .setCancelable(false)
                                .setPositiveButton(getString(R.string.okk), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                    }

                                })
                                .show();
                    }

                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(listreklam!=null){
            for(int i = 0;i<listreklam.size();i++){
                listreklam.get(i).destroy();
            }
        }

    }



    @Override
    public void onUpvoteClick(int position, ImageView upvoteImage,ImageView downvoteImage, TextView voteNumber) {
        GroupPost post = list.get(position).getGroupPost();


        if(post.getObjectId()!=null){
            if(post.getDownvote()){
                post.setDownvote(false);
                post.setUpvote(true);
                downvoteImage.setImageDrawable(getDrawable(R.drawable.ic_downvote));
                upvoteImage.setImageDrawable( getDrawable(R.drawable.ic_upvote_blue));
                voteNumber.setTextColor(Color.parseColor("#2d72bc"));
                post.increment("votenumber",2);
                voteNumber.setText(GenelUtil.ConvertNumber((int)post.getVote(),GroupActivity.this));


                //upvote
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("id", post.getObjectId());
                ParseCloud.callFunctionInBackground("upvoteGroupPost", params);

            }
            else{
                if(post.getUpvote()){
                    post.setUpvote(false);
                    upvoteImage.setImageDrawable(getDrawable(R.drawable.ic_upvote));
                    voteNumber.setTextColor(Color.parseColor("#999999"));
                    post.increment("votenumber",-1);
                    voteNumber.setText(GenelUtil.ConvertNumber((int)post.getVote(),GroupActivity.this));


                    //unupvote
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("id", post.getObjectId());
                    ParseCloud.callFunctionInBackground("unvoteGroupPost", params);
                }
                else{
                    post.setUpvote(true);
                    upvoteImage.setImageDrawable(getDrawable(R.drawable.ic_upvote_blue));
                    voteNumber.setTextColor(Color.parseColor("#2d72bc"));
                    post.increment("votenumber");
                    voteNumber.setText(GenelUtil.ConvertNumber((int)post.getVote(),GroupActivity.this));


                    //upvote
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("id", post.getObjectId());
                    ParseCloud.callFunctionInBackground("upvoteGroupPost", params);

                }
            }
        }
    }

    @Override
    public void onDownvoteClick(int position, ImageView upvoteImage,ImageView downvoteImage, TextView voteNumber) {
        GroupPost post = list.get(position).getGroupPost();
        if(post.getObjectId()!=null){

            if(post.getUpvote()){

                post.setDownvote(true);
                post.setUpvote(false);
                upvoteImage.setImageDrawable(getDrawable(R.drawable.ic_upvote));
                downvoteImage.setImageDrawable(getDrawable(R.drawable.ic_downvote_red));
                voteNumber.setTextColor(Color.parseColor("#a64942"));

                post.increment("votenumber",-2);
                voteNumber.setText(GenelUtil.ConvertNumber((int)post.getVote(),GroupActivity.this));



                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("id", post.getObjectId());
                ParseCloud.callFunctionInBackground("downvoteGroupPost", params);

            }
            else{
                if(post.getDownvote()){
                    post.setDownvote(false);
                    downvoteImage.setImageDrawable(getDrawable(R.drawable.ic_downvote));
                    voteNumber.setTextColor(Color.parseColor("#999999"));
                    post.increment("votenumber");
                    voteNumber.setText(GenelUtil.ConvertNumber((int)post.getVote(),GroupActivity.this));


                    //undownvote
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("id", post.getObjectId());
                    ParseCloud.callFunctionInBackground("unvoteGroupPost", params);
                }
                else{
                    post.setDownvote(true);
                    downvoteImage.setImageDrawable(getDrawable(R.drawable.ic_downvote_red));
                    voteNumber.setTextColor(Color.parseColor("#a64942"));
                    post.increment("votenumber",-1);
                    voteNumber.setText(GenelUtil.ConvertNumber((int)post.getVote(),GroupActivity.this));


                    //downvote
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("id", post.getObjectId());
                    ParseCloud.callFunctionInBackground("downvoteGroupPost", params);
                }
            }
        }
    }

    @Override
    public void onOptionsClick(int position, TextView commentNumber) {
        GroupPost post = list.get(position).getGroupPost();
        ProgressDialog progressDialog = new ProgressDialog(GroupActivity.this);
        progressDialog.setCancelable(false);
        ArrayList<String> other = new ArrayList<>();

        if(group.getAdmins().contains(ParseUser.getCurrentUser().getObjectId())){
            if(post.getCommentable()){
                other.add(getString(R.string.disablecomment));
            }
            if(!post.getCommentable()){
                other.add(getString(R.string.enablecomment));
            }
            other.add(getString(R.string.delete));
            if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())&&!group.getAdmins().contains(post.getUser().getObjectId())){
                other.add(getString(R.string.deleteandbanuser));
            }


        }
        else{
            if(post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                if(post.getCommentable()){
                    other.add(getString(R.string.disablecomment));
                }
                if(!post.getCommentable()){
                    other.add(getString(R.string.enablecomment));
                }
                other.add(getString(R.string.delete));
            }
            else{
                other.add(getString(R.string.reporttoadmin));
            }

        }




        String[] popupMenu = other.toArray(new String[other.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
        builder.setCancelable(true);

        builder.setItems(popupMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String select = popupMenu[which];
                if(select.equals(getString(R.string.disablecomment))){

                    progressDialog.setMessage(getString(R.string.disablecomment));
                    progressDialog.show();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("id", post.getObjectId());
                    ParseCloud.callFunctionInBackground("enableDisableCommentInGroup", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setCommentable(false);
                                progressDialog.dismiss();
                                commentNumber.setText(GroupActivity.this.getString(R.string.disabledcomment));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(GroupActivity.this,getString(R.string.error));
                            }
                        }
                    });

                }
                if(select.equals(getString(R.string.reporttoadmin))){

                    progressDialog.setMessage(getString(R.string.report));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("id", post.getObjectId());
                    ParseCloud.callFunctionInBackground("reportGroupPostToAdmin", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                GenelUtil.ToastLong(GroupActivity.this,getString(R.string.reportsucces));
                                progressDialog.dismiss();
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(GroupActivity.this,getString(R.string.error));
                            }
                        }
                    });


                }
                if(select.equals(getString(R.string.enablecomment))){

                    progressDialog.setMessage(getString(R.string.enablecomment));
                    progressDialog.show();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("id", post.getObjectId());
                    ParseCloud.callFunctionInBackground("enableDisableCommentInGroup", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setCommentable(true);
                                progressDialog.dismiss();

                                commentNumber.setText(GenelUtil.ConvertNumber((int)post.getCommentnumber(),GroupActivity.this));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(GroupActivity.this,getString(R.string.error));
                            }
                        }
                    });

                }
                if(select.equals(getString(R.string.delete))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
                    builder.setTitle(R.string.deletetitle);
                    builder.setCancelable(true);
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                            progressDialog.setMessage(getString(R.string.delete));
                            progressDialog.show();
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("id", post.getObjectId());
                            ParseCloud.callFunctionInBackground("deleteGroupPost", params, new FunctionCallback<String>() {
                                @Override
                                public void done(String object, ParseException e) {
                                    if(e==null&&object.equals("deleted")){
                                        list.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position,list.size());
                                        progressDialog.dismiss();

                                    }
                                    else{
                                        progressDialog.dismiss();
                                        GenelUtil.ToastLong(GroupActivity.this,getString(R.string.error));
                                    }
                                }
                            });


                        }
                    });
                    builder.show();

                }
                if(select.equals(getString(R.string.deleteandbanuser))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
                    builder.setTitle(R.string.deleteandbantitle);
                    builder.setCancelable(true);
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setPositiveButton(R.string.deletenban, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                            progressDialog.setMessage(getString(R.string.deleteandbanuser));
                            progressDialog.show();
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("id", post.getObjectId());
                            params.put("userid",post.getUser().getObjectId());
                            params.put("groupid",group.getId());
                            ParseCloud.callFunctionInBackground("deleteGroupPostAndBanUser", params, new FunctionCallback<String>() {
                                @Override
                                public void done(String object, ParseException e) {
                                    if(e==null&&object.equals("deleted")){
                                        list.remove(position);
                                        adapter.notifyItemRemoved(position);
                                        adapter.notifyItemRangeChanged(position,list.size());
                                        progressDialog.dismiss();

                                    }
                                    else{
                                        progressDialog.dismiss();
                                        GenelUtil.ToastLong(GroupActivity.this,getString(R.string.error));
                                    }
                                }
                            });


                        }
                    });
                    builder.show();

                }
            }
        });
        builder.show();
    }

    @Override
    public void onSocialClick(int position, int clickType, String text) {
        if(clickType== HomeAdapter.TYPE_HASHTAG){
            //hashtag
            startActivity(new Intent(GroupActivity.this, HashtagActivity.class).putExtra("hashtag",text.replace("#","")));

        }
        else if(clickType==HomeAdapter.TYPE_MENTION){
            //mention
            String username = text;

            username = username.replace("@","").trim();


            if(!username.equals(ParseUser.getCurrentUser().getUsername())){
                startActivity(new Intent(GroupActivity.this, GuestProfileActivity.class).putExtra("username",username));
            }

        }
        else if(clickType==HomeAdapter.TYPE_LINK){
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            String url = text;
            if(!url.startsWith("http")){
                url = "http://"+url;
            }
            if(GenelUtil.getNightMode()){
                builder.setToolbarColor(Color.parseColor("#303030"));
            }
            else{
                builder.setToolbarColor(Color.parseColor("#ffffff"));
            }
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(GroupActivity.this, Uri.parse(url));
        }
    }

    @Override
    public void onGoToProfileClick(int position) {
        GroupPost post = list.get(position).getGroupPost();
        if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            startActivity(new Intent(GroupActivity.this, GuestProfileActivity.class).putExtra("user",post.getUser()));
        }
    }

    @Override
    public void onOpenComments(int position) {
        GroupPost post = list.get(position).getGroupPost();
        if(post.getCommentable()){
            startActivity(new Intent(this, CommentActivity.class)
                    .putExtra("post",post));
        }
    }

    @Override
    public void onLinkClick(int position) {
        GroupPost post = list.get(position).getGroupPost();
        String url = post.getUrl();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        if(ParseUser.getCurrentUser().getBoolean("nightmode")){
            builder.setToolbarColor(Color.parseColor("#303030"));
        }
        else{
            builder.setToolbarColor(Color.parseColor("#ffffff"));
        }
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(GroupActivity.this, Uri.parse(url));
    }

    @Override
    public void onImageClick(int position,ImageView imageView,int pos) {
        GroupPost post = list.get(position).getGroupPost();
        ArrayList<String> ulist = new ArrayList<>();
        ArrayList<String> uList2 = new ArrayList<>();

        ulist.add(post.getMainMedia().getUrl());
        uList2.add(post.getThumbMedia().getUrl());
        if(post.getImageCount()>1){
            ulist.add(post.getMainMedia1().getUrl());
            uList2.add(post.getThumbMedia1().getUrl());
        }
        if(post.getImageCount()>2){
            ulist.add(post.getMainMedia2().getUrl());
            uList2.add(post.getThumbMedia2().getUrl());
        }
        if(post.getImageCount()>3){
            ulist.add(post.getMainMedia3().getUrl());
            uList2.add(post.getThumbMedia3().getUrl());
        }
        GenelUtil.showImage(ulist,uList2,imageView,pos,adapter);
    }

    @Override
    public void onReloadImageClick(int position, RoundKornerRelativeLayout reloadLayout, ProgressBar progressBar, ImageView imageView) {
        GroupPost post = list.get(position).getGroupPost();

        reloadLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if(post.getNsfw()){
            Glide.with(GroupActivity.this).load(post.getMainMedia().getUrl()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.INVISIBLE);
                    reloadLayout.setVisibility(View.VISIBLE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.INVISIBLE);
                    return false;
                }
            }).into(imageView);
        }
        else{
            if(post.getRatioH()>1280||post.getRatioW()>1280){
                int ih = 1280;
                int iw = 1280;
                if(post.getRatioH()>post.getRatioW()){
                    ih = 1280;
                    iw = 1280 * (post.getRatioW()/post.getRatioH());
                }
                else{
                    iw = 1280;
                    ih = 1280 * (post.getRatioH()/post.getRatioW());
                }
                Glide.with(GroupActivity.this).load(post.getMainMedia().getUrl()).override(iw,ih).thumbnail(Glide.with(GroupActivity.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        reloadLayout.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).into(imageView);

            }
            else{
                Glide.with(GroupActivity.this).load(post.getMainMedia().getUrl()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).thumbnail(Glide.with(GroupActivity.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        reloadLayout.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                }).into(imageView);

            }
        }



    }

}
