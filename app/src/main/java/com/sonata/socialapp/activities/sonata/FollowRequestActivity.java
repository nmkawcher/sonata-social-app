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
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.adapters.FollowReqAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.FollowRequestClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class FollowRequestActivity extends AppCompatActivity implements FollowRequestClick {

    List<ListObject> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    FollowReqAdapter adapter;
    private boolean loading=true;
    Date date;
    RecyclerView.OnScrollListener onScrollListener;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    RelativeLayout back;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        recyclerView.setAdapter(null);
        adapter = null;
        recyclerView=null;
        list.clear();
        list=null;
        linearLayoutManager=null;
        date=null;
        onScrollListener = null;
        swipeRefreshLayout.setOnRefreshListener(null);
        swipeRefreshLayout=null;
        onRefreshListener=null;
        back=null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_request);

        if(getIntent().getStringExtra("to")!=null){
            String to = getIntent().getStringExtra("to");
            if(ParseUser.getCurrentUser().getObjectId().equals(to)){
                setUpOnCreate();
            }
            else{
                List<Object> an = GenelUtil.isUserSaved(this,to);
                boolean isExist = (boolean) an.get(0);
                if(isExist){
                    String session = (String) an.get(2);
                    ParseUser.becomeInBackground(session, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                            if(e==null){
                                String text = String.format(getResources().getString(R.string.accsw), "@"+ParseUser.getCurrentUser().getUsername());
                                GenelUtil.ToastLong(FollowRequestActivity.this,text);

                                setUpOnCreate();
                            }
                            else{
                                if(e.getCode() == ParseException.INVALID_SESSION_TOKEN){
                                    GenelUtil.removeUserFromCache(to, FollowRequestActivity.this);
                                }
                                GenelUtil.ToastLong(FollowRequestActivity.this,getString(R.string.invalidsessiontoken));
                                startActivity(new Intent(FollowRequestActivity.this, StartActivity.class));
                                finish();
                            }
                        }
                    });
                }
                else{
                    startActivity(new Intent(this, StartActivity.class));
                    finish();
                }
            }
        }
        else{
            setUpOnCreate();
        }


    }

    private void setUpOnCreate(){
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
        linearLayoutManager=new LinearLayoutManager(FollowRequestActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter=new FollowReqAdapter();
        adapter.setContext(list, Glide.with(FollowRequestActivity.this),this);
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

        if(!this.isDestroyed()&&!this.isFinishing()){
            getReqs(null,false);
        }
    }

    @Override
    public void onBackPressed() {
        if(getIntent()!=null){
            if(getIntent().getBooleanExtra("notif",false)){
                if(this.isTaskRoot()){
                    startActivity(new Intent(this,MainActivity.class));
                    finish();
                }
                else{
                    super.onBackPressed();
                }
            }
            else{
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }
    }

    private void refreshSetting(){
        list.clear();
        date=null;
        postson=false;

    }

    private void getReqs(Date date,boolean isRefresh){
        if(!this.isDestroyed()&&!this.isFinishing()){
            HashMap<String, Object> params = new HashMap<String, Object>();
            if(date!=null){
                params.put("date",date);
            }
            ParseCloud.callFunctionInBackground("getToMeFollowReqs", params, new FunctionCallback<List<SonataUser>>() {
                @Override
                public void done(List<SonataUser>  objects, ParseException e) {
                    Log.e("done","done");
                    if(!FollowRequestActivity.this.isFinishing()&&!FollowRequestActivity.this.isDestroyed()){
                        if(e==null){

                            if(objects!= null){
                                if(isRefresh){
                                    refreshSetting();
                                }
                                initList(objects);
                            }



                        }

                    }
                }
            });
        }
    }

    private void initList(List<SonataUser> objects) {
        if(GenelUtil.isAlive(this)){
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

                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();


            }
            else{
                date=objects.get(0).getDate("date");
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.get(list.size()-1));
                    }
                }
                if(objects.size()<10){
                    postson =true;
                    for(int i=0;i<objects.size();i++){

                        ListObject post = new ListObject();
                        post.setType("user");

                        post.setUser(objects.get(i));
                        post.getUser().setToMeFollowRequest(post.getUser().getToMeFollowRequest2());
                        post.getUser().setFollowRequest(post.getUser().getFollowRequest2());
                        post.getUser().setBlock(post.getUser().getBlock2());
                        post.getUser().setFollow(post.getUser().getFollow2());

                        list.add(post);

                    }

                    adapter.notifyDataSetChanged();
                    loading =false;
                    swipeRefreshLayout.setRefreshing(false);


                }
                else{

                    for(int i=0;i<objects.size();i++){

                        ListObject post = new ListObject();
                        post.setType("user");

                        post.setUser(objects.get(i));
                        post.getUser().setToMeFollowRequest(post.getUser().getToMeFollowRequest2());
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
        if(GenelUtil.clickable(700)){
            if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                startActivity(new Intent(FollowRequestActivity.this, GuestProfileActivity.class).putExtra("user",user));
            }
        }
    }

    @Override
    public void buttonClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay,RoundKornerRelativeLayout rejectLayout) {
        Log.e("Click","Block button Click");

            SonataUser user = list.get(position).getUser();
            if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                if(buttonText.getText().toString().equals(buttonText.getContext().getString(R.string.accept))){
                    //isteği geri çek
                    rejectLayout.setVisibility(View.GONE);

                    buttonText.setText(buttonText.getContext().getString(R.string.loading));

                    HashMap<String,String> params = new HashMap<>();
                    params.put("id",user.getObjectId());
                    ParseCloud.callFunctionInBackground("acceptFollowRequest", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if(e==null){
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) buttonLay.getLayoutParams();
                                params.setMarginEnd((int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        20,
                                        buttonLay.getContext().getResources().getDisplayMetrics()));
                                buttonLay.setLayoutParams(params);
                                user.setToMeFollowRequest(false);
                                if(user.getFollow()){
                                    buttonText.setText(getString(R.string.unfollow));
                                    buttonText.setTextColor(getResources().getColor(R.color.white));
                                    buttonLay.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
                                }
                                else{
                                    if(user.getFollowRequest()){
                                        buttonText.setText(getString(R.string.requestsent));
                                        buttonText.setTextColor(getResources().getColor(R.color.white));
                                        buttonLay.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
                                    }
                                    else{
                                        buttonText.setText(getString(R.string.follow));
                                        buttonText.setTextColor(getResources().getColor(R.color.blue));
                                        buttonLay.setBackground(getResources().getDrawable(R.drawable.button_background));
                                    }

                                }


                            }
                            else{

                                rejectLayout.setVisibility(View.VISIBLE);
                                buttonText.setText(buttonText.getContext().getString(R.string.accept));
                                GenelUtil.ToastLong(buttonText.getContext(),buttonText.getContext().getString(R.string.error));
                            }
                        }
                    });

                }
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
                                    GenelUtil.ToastLong(FollowRequestActivity.this,getString(R.string.error));
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
                                    GenelUtil.ToastLong(FollowRequestActivity.this,getString(R.string.error));
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
                                    GenelUtil.ToastLong(FollowRequestActivity.this,getString(R.string.error));
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
                                    GenelUtil.ToastLong(FollowRequestActivity.this,getString(R.string.error));
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
                                GenelUtil.ToastLong(FollowRequestActivity.this,getString(R.string.error));
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
                                GenelUtil.ToastLong(FollowRequestActivity.this,getString(R.string.error));
                            }


                        }
                    });

                }
            }


    }

    @Override
    public void rejectClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay, RoundKornerRelativeLayout rejectLayout, ProgressBar progressBar, RelativeLayout reject) {
        if(GenelUtil.clickable(700)){
            SonataUser user = list.get(position).getUser();
            if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                buttonText.setText(getString(R.string.loading));
                reject.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                HashMap<String,String> params = new HashMap<>();
                params.put("id",user.getObjectId());
                ParseCloud.callFunctionInBackground("rejectFollowRequest", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String object, ParseException e) {
                        if(e==null){
                            user.setToMeFollowRequest(false);
                            list.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position,list.size());
                            //setVisibility(false);

                        }
                        else{
                            GenelUtil.ToastLong(reject.getContext(),reject.getContext().getString(R.string.error));
                            reject.setVisibility(View.VISIBLE);
                            buttonText.setText(getString(R.string.accept));
                            buttonText.setTextColor(getResources().getColor(R.color.blue));
                            buttonLay.setBackground(getResources().getDrawable(R.drawable.button_background));
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }

        }
    }

}
