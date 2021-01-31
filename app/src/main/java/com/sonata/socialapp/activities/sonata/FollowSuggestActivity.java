package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.adapters.BlockedPersonAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FollowSuggestActivity extends AppCompatActivity implements BlockedAdapterClick {

    List<ListObject> list;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    BlockedPersonAdapter adapter;
    RelativeLayout next;

    ArrayList<String> suggestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_suggest);

        if(getIntent() == null) return;
        suggestList = getIntent().getStringArrayListExtra("list");


        next = findViewById(R.id.nexttextripple);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FollowSuggestActivity.this,MainActivity.class).putExtra("newregister",true));
                finish();
            }
        });

        recyclerView = findViewById(R.id.folreqrecyclerview);



        list=new ArrayList<>();
        ListObject object2 = new ListObject();
        object2.setType("load");
        list.add(object2);
        linearLayoutManager=new LinearLayoutManager(FollowSuggestActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new BlockedPersonAdapter();
        adapter.setContext(list, Glide.with(FollowSuggestActivity.this),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        if(GenelUtil.isAlive(this)){
            getReqs();
        }

    }

    private void getReqs(){
        if(GenelUtil.isAlive(this)){
            HashMap<String, Object> params = new HashMap<String, Object>();
            if(suggestList != null){
                params.put("list",suggestList);
            }
            ParseCloud.callFunctionInBackground("getSuggestsFromList", params, new FunctionCallback<HashMap>() {
                @Override
                public void done(HashMap  objects, ParseException e) {
                    Log.e("done","done");
                    if(GenelUtil.isAlive(FollowSuggestActivity.this)){
                        if(e==null){

                            if(objects!= null){
                                Collections.shuffle((List<SonataUser>)objects.get("profiles"));
                                initList((List<SonataUser>)objects.get("profiles"));
                            }
                            else{
                                initList(new ArrayList<>());
                            }


                        }
                        else{
                            initList(new ArrayList<>());


                        }
                    }
                }
            });
        }
    }

    private void initList(List<SonataUser> objects) {
        if(GenelUtil.isAlive(this)){
            if(objects.size()==0){
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


            }
            else{

                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.get(list.size()-1));
                    }
                }

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
        if(GenelUtil.clickable(700)){
            if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                startActivity(new Intent(FollowSuggestActivity.this, GuestProfileActivity.class).putExtra("user",user));
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
                                GenelUtil.ToastLong(FollowSuggestActivity.this,getString(R.string.error));
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
                                GenelUtil.ToastLong(FollowSuggestActivity.this,getString(R.string.error));
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
                                GenelUtil.ToastLong(FollowSuggestActivity.this,getString(R.string.error));
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
                                GenelUtil.ToastLong(FollowSuggestActivity.this,getString(R.string.error));
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
                            GenelUtil.ToastLong(FollowSuggestActivity.this,getString(R.string.error));
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
                            GenelUtil.ToastLong(FollowSuggestActivity.this,getString(R.string.error));
                        }


                    }
                });

            }
        }


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
