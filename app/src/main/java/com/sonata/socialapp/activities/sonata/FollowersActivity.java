package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.adapters.BlockedPersonAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FollowersActivity extends AppCompatActivity implements BlockedAdapterClick {

    String userID = "";

    List<ListObject> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    BlockedPersonAdapter adapter;
    private boolean loading=true;
    Date date;
    RecyclerView.OnScrollListener onScrollListener;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Util.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        if(getIntent()!=null){
            userID = getIntent().getStringExtra("id");
        }

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.folreqrecyclerview);
        swipeRefreshLayout = findViewById(R.id.folreqSwipeRefreshLayout);

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0&&linearLayoutManager.findLastVisibleItemPosition()>(list.size()-7)&&!loading&&!postson){
                    loading=true;
                    getReqs(date,false);
                }
            }
        };

        list=new ArrayList<>();

        ListObject object2 = new ListObject();
        object2.setType("load");
        list.add(object2);
        linearLayoutManager=new LinearLayoutManager(FollowersActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new BlockedPersonAdapter();
        adapter.setContext(list, Glide.with(FollowersActivity.this),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!loading){
                    loading=true;

                    date=null;

                    getReqs(null,true);

                }
            }
        };
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);



        recyclerView.addOnScrollListener(onScrollListener);

        if(Util.isAlive(this)){
            getReqs(null,false);
        }

    }


    private void refreshSetting(){
        list.clear();
        date=null;
        postson=false;
    }


    private void getReqs(Date date,boolean isRefresh){
        if(Util.isAlive(this)){
            HashMap<String, Object> params = new HashMap<String, Object>();
            if(date!=null){
                params.put("date",date);
            }
            params.put("id",userID);
            ParseCloud.callFunctionInBackground("getFollowers", params, new FunctionCallback<HashMap>() {
                @Override
                public void done(HashMap  objects, ParseException e) {
                    Log.e("done","done");
                    if(Util.isAlive(FollowersActivity.this)){
                        if(e==null){

                            if(objects!= null){
                                if(isRefresh){
                                    refreshSetting();
                                }
                                initList((List<SonataUser>)objects.get("followers")
                                        ,(boolean) objects.get("hasmore")
                                        ,(Date)objects.get("date"));
                            }
                            else{
                                initList(new ArrayList<>(),false,new Date());
                            }


                        }
                        else{
                            initList(new ArrayList<>(),false,new Date());


                        }
                    }
                }
            });
        }
    }

    private void initList(List<SonataUser> objects,boolean hasmore,Date date) {
        if(Util.isAlive(this)){
            postson = !hasmore;
            this.date = date;
            if(objects.size()==0){

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
                        if(list.size()==0){
                            ListObject post = new ListObject();
                            post.setType("boş");
                            list.add(post);
                        }

                    }
                }

                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();


            }
            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.get(list.size()-1));
                    }
                }
                if(objects.size()<20){
                    for(int i=0;i<objects.size();i++){

                        ListObject post = new ListObject();
                        post.setType("user");

                        post.setUser(objects.get(i));
                        post.getUser().setFollowRequest(post.getUser().getFollowRequest2());
                        post.getUser().setBlock(post.getUser().getBlock2());
                        post.getUser().setFollow(post.getUser().getFollow2());

                        list.add(post);

                    }

                    adapter.notifyDataSetChanged();
                    loading =false;
                    recyclerView.addOnScrollListener(onScrollListener);
                    swipeRefreshLayout.setRefreshing(false);


                }
                else{

                    for(int i=0;i<objects.size();i++){

                        ListObject post = new ListObject();
                        post.setType("user");

                        post.setUser(objects.get(i));
                        post.getUser().setFollowRequest(post.getUser().getFollowRequest2());
                        post.getUser().setBlock(post.getUser().getBlock2());
                        post.getUser().setFollow(post.getUser().getFollow2());
                        list.add(post);


                    }



                    ListObject load = new ListObject();
                    load.setType("load");
                    list.add(load);

                    adapter.notifyDataSetChanged();
                    loading =false;
                    recyclerView.addOnScrollListener(onScrollListener);
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void goToProfileClick(int position) {
        SonataUser user = list.get(position).getUser();
        if(Util.clickable(700)){
            if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                startActivity(new Intent(FollowersActivity.this, GuestProfileActivity.class).putExtra("user",user));
            }
        }
    }

    @Override
    public void buttonClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay) {
        Log.e("Click","Block button Click");
            SonataUser user = list.get(position).getUser();
            if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                if(buttonText.getText().toString().equals(buttonText.getContext().getString(R.string.follow))){
                    if(user.getPrivate()){
                        //Takip İsteği gönder
                        buttonText.setText(getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("sendFollowRequest", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {

                                if(e==null){
                                    buttonLay.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
                                    user.setFollowRequest(true);
                                    buttonText.setText(getString(R.string.requestsent));
                                    buttonText.setTextColor(Color.WHITE);
                                }
                                else{
                                    buttonText.setText(getString(R.string.follow));
                                    Util.ToastLong(FollowersActivity.this,getString(R.string.error));
                                }


                            }
                        });
                    }
                    else{
                        //takip et
                        buttonText.setText(getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("follow", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(e==null){
                                    user.setFollow(true);
                                    buttonText.setTextColor(getResources().getColor(R.color.white));

                                    buttonLay.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
                                    buttonText.setText(getString(R.string.unfollow));
                                }
                                else{
                                    buttonText.setText(getString(R.string.follow));
                                    Util.ToastLong(FollowersActivity.this,getString(R.string.error));
                                }


                            }
                        });

                    }
                }
                if(buttonText.getText().toString().equals(buttonText.getContext().getString(R.string.unfollow))){
                    if(user.getPrivate()){
                        //takipten çık ve profili gizle
                        buttonText.setText(buttonText.getContext().getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("unfollow", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(e==null){
                                    user.setFollow(false);
                                    buttonText.setText(buttonText.getContext().getString(R.string.follow));
                                    buttonText.setTextColor(getResources().getColor(R.color.blue));
                                    buttonLay.setBackground(getResources().getDrawable(R.drawable.button_background));


                                }
                                else{
                                    buttonText.setText(buttonText.getContext().getString(R.string.unfollow));
                                    Util.ToastLong(FollowersActivity.this,getString(R.string.error));
                                }


                            }
                        });
                    }
                    else{
                        //takipten çık
                        buttonText.setText(buttonText.getContext().getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("unfollow", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(e==null){
                                    user.setFollow(false);
                                    buttonText.setText(buttonText.getContext().getString(R.string.follow));
                                    buttonText.setTextColor(getResources().getColor(R.color.blue));
                                    buttonLay.setBackground(getResources().getDrawable(R.drawable.button_background));

                                }
                                else{
                                    buttonText.setText(buttonText.getContext().getString(R.string.unfollow));
                                    Util.ToastLong(FollowersActivity.this,getString(R.string.error));
                                }


                            }
                        });
                    }
                }
                if(buttonText.getText().toString().equals(getString(R.string.unblock))){


                    buttonText.setText(getString(R.string.loading));

                    HashMap<String,String> params = new HashMap<>();
                    params.put("userID",user.getObjectId());
                    ParseCloud.callFunctionInBackground("unblock", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if(e==null){

                                user.setBlock(false);
                                buttonText.setText(buttonText.getContext().getString(R.string.follow));
                                buttonText.setTextColor(getResources().getColor(R.color.blue));
                                buttonLay.setBackground(buttonLay.getContext().getResources().getDrawable(R.drawable.button_background));

                            }
                            else{
                                buttonText.setText(getString(R.string.accept));
                                Util.ToastLong(FollowersActivity.this,getString(R.string.error));
                            }
                        }
                    });

                }
                if(buttonText.getText().toString().equals(buttonText.getContext().getString(R.string.requestsent))){
                    //isteği geri çek
                    buttonText.setText(buttonText.getContext().getString(R.string.loading));
                    HashMap<String,String> params = new HashMap<>();
                    params.put("userID",user.getObjectId());
                    ParseCloud.callFunctionInBackground("removeFollowRequest", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if(e==null){
                                user.setFollowRequest(false);
                                buttonText.setText(buttonText.getContext().getString(R.string.follow));
                                buttonText.setTextColor(getResources().getColor(R.color.blue));
                                buttonLay.setBackground(getResources().getDrawable(R.drawable.button_background));
                            }
                            else{
                                buttonText.setText(buttonText.getContext().getString(R.string.requestsent));
                                Util.ToastLong(FollowersActivity.this,getString(R.string.error));
                            }


                        }
                    });

                }
            }


    }
}
