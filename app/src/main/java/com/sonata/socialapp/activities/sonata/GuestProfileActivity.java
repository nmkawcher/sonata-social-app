package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.BlockedPersonAdapter;
import com.sonata.socialapp.utils.adapters.GuestGridProfilAdapter;
import com.sonata.socialapp.utils.adapters.SafPostAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;
import com.vincan.medialoader.DownloadManager;
import com.vincan.medialoader.MediaLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.jzvd.Jzvd;


public class GuestProfileActivity extends AppCompatActivity implements RecyclerViewClick {

    private void refreshSetting(){
        list.clear();
        adapter.notifyDataSetChanged();
        if(postAdapter!=null){
            postAdapter.notifyDataSetChanged();
        }
    }

    RoundKornerRelativeLayout rr;

    ProgressDialog progressDialog;
    TextView username,username1;
    SonataUser user;
    RelativeLayout followButton,suggestLayout;
    TextView followButtonText;
    RoundKornerRelativeLayout followLayout;
    TextView name,bio,followingtext,followingnumber,followertext,followernumber;




    private long mLastClickTime = 0;

    private SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    ImageView profilephoto;
    RecyclerView recyclerView,postRecyclerView,suggestRecyclerView;
    Date date;
    List<ListObject> list;

    private boolean postson=false;
    private boolean loading=true;
    RecyclerView.OnScrollListener onScrollListener,postOnScrollListener;
    SafPostAdapter postAdapter;
    GuestGridProfilAdapter adapter;
    String usernamestring;
    RelativeLayout loadingLayout,listPostLayout;
    CoordinatorLayout mainLayout;
    RelativeLayout back,back1,back2,options;

    BlockedPersonAdapter suggestAdapter;
    List<ListObject> suggestList;
    LinearLayoutManager suggestLayoutManager;


    boolean actionIntent;


    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_profile);

        if(Objects.equals(getIntent().getAction(), Intent.ACTION_VIEW)){
            actionIntent = true;
            String data = getIntent().getData().toString();
            if(ParseUser.getCurrentUser()==null){
                startActivity(new Intent(this,LoginActivity.class).putExtra("deplinkintent",data));
                finish();
                return;
            }
            String newS = data.substring(data.indexOf(GenelUtil.appUrl)+GenelUtil.appUrl.length());
            if(newS.startsWith("/")){
                newS = newS.substring(1);
            }
            if(newS.startsWith("user/")){
                newS = newS.replace("user/","");
                setUpOnCreate(newS);

            }
        }
        else{
            if(getIntent().getStringExtra("to")!=null){
                String to = getIntent().getStringExtra("to");
                if(ParseUser.getCurrentUser().getObjectId().equals(to)){
                    setUpOnCreate(null);
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
                                    GenelUtil.ToastLong(GuestProfileActivity.this,text);

                                    setUpOnCreate(null);
                                }
                                else{
                                    if(e.getCode() == ParseException.INVALID_SESSION_TOKEN){
                                        GenelUtil.removeUserFromCache(to, GuestProfileActivity.this);
                                    }
                                    GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.invalidsessiontoken));
                                    startActivity(new Intent(GuestProfileActivity.this, StartActivity.class));
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
                setUpOnCreate(null);
            }
        }

    }

    private void setUpOnCreate(String usernamestr){
        loadingLayout=findViewById(R.id.guestprofileloading);

        mainLayout=findViewById(R.id.guestmainlayout);
        back2 = findViewById(R.id.backbuttonbutton2);
        postRecyclerView = findViewById(R.id.profile_posts_recyclerview);
        listPostLayout = findViewById(R.id.profile_post_layout);
        swipeRefreshLayout = findViewById(R.id.gpSwipeRefresh);

        progressDialog = new ProgressDialog(GuestProfileActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.block));

        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!loading){
                    loading = true;
                    date=null;
                    if(user!=null){
                        getUser(user.getObjectId(),null,true);
                    }
                    else{
                        if(getIntent().getStringExtra("username")!=null){
                            getUser(null,getIntent().getStringExtra("username"),true);
                        }
                        else{
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }


                }
                else{
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);



        options = findViewById(R.id.appoptionsripple);
        usernamestring=getIntent().getStringExtra("username");
        if(usernamestr != null) {
            usernamestring = usernamestr;
        }
        back1=findViewById(R.id.backbuttonbutton1);
        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        username1=findViewById(R.id.profileusernametext1);
        username1.setText(usernamestring);


        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        username=findViewById(R.id.profileusernametext);
        if(usernamestring != null){
            username.setText(usernamestring);
        }

        profilephoto = findViewById(R.id.profilephotophoto);
        profilephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user.getHasPp()){
                    List<String> ulist = new ArrayList<>();
                    ulist.add("0");

                    List<HashMap> ulist2 = new ArrayList<>();
                    HashMap json = new HashMap();
                    try {
                        json.put("media",user.getBigPp());
                        json.put("thumbnail",user.getAdapterPp());
                        json.put("width",2000);
                        json.put("height",2000);
                        ulist2.add(json);

                        GenelUtil.showImage(ulist,ulist2
                                ,profilephoto,0,null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        name=findViewById(R.id.profilename);

        bio=findViewById(R.id.profilebio);

        followernumber=findViewById(R.id.followersnumbers);

        followingnumber=findViewById(R.id.followingnumbers);

        followertext=findViewById(R.id.followerstext);
        followingtext=findViewById(R.id.followingtext);

        suggestRecyclerView = findViewById(R.id.guestProfileSggestionsRecyclerview);
        suggestLayout = findViewById(R.id.suggestLayout);




        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int llm = ((GridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                for(int a = llm; a < Math.min(llm+15,list.size()); a++){
                    try{
                        Post post = list.get(a).getPost();

                        if(post != null){

                            if(post.getType().equals("video")){
                                HashMap<String,Object> mediaObject = post.getMediaList().get(0);

                                ParseFile thumb = (ParseFile) mediaObject.get("thumbnail");
                                String thumburl = thumb.getUrl();
                                Glide.with(GuestProfileActivity.this).load(thumburl).preload();
                            }
                            else{
                                HashMap<String,Object> mediaObject = post.getMediaList().get(0);
                                ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                String url = parseFile.getUrl();
                                Glide.with(GuestProfileActivity.this).load(url).preload();
                            }
                        }
                    } catch (Exception ignored){}

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(((GridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition()>(list.size()-4)){
                    if(!loading&&!postson){
                        loading=true;
                        getPosts(date,false);
                    }
                }



            }
        };




        followButton = findViewById(R.id.followButtonRipple);
        followButtonText = findViewById(R.id.followButtonText);
        followButtonText.setText(getString(R.string.loading));
        followLayout = findViewById(R.id.followButtonLayout);



        recyclerView = findViewById(R.id.profilerecyclerview);


        list=new ArrayList<>();






        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        ((GridLayoutManager)recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                String type = list.get(position).getType();
                if(type.equals("load")||type.equals("boş")||type.equals("private")){
                    return 3;
                }
                return 1;
            }
        });


        adapter = new GuestGridProfilAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(onScrollListener);
        adapter.setContext(list,Glide.with(this),this);
        ListObject object2 = new ListObject();
        object2.setType("load");
        list.add(object2);
        adapter.notifyDataSetChanged();

        if(getIntent().getParcelableExtra("user")!=null){
            user=getIntent().getParcelableExtra("user");
            loadingLayout.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
            setProfile1(user);
            adapter.setUser(user);

        }




        if(!this.isFinishing()&&!this.isDestroyed()){
            //get
            if(usernamestr!=null){
                getUser(null,usernamestr.toLowerCase(),false);
            }
            else{
                if(user!=null){
                    getUser(user.getObjectId(),null,false);
                }
                else{
                    if(getIntent().getStringExtra("username")!=null){
                        getUser(null,getIntent().getStringExtra("username").toLowerCase(),false);
                    }
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        if(listPostLayout!=null){
            if(listPostLayout.getVisibility()==View.VISIBLE){
                Jzvd.releaseAllVideos();
                mainLayout.setVisibility(View.VISIBLE);
                listPostLayout.setVisibility(View.INVISIBLE);
            }
            else{
                if(this.isTaskRoot() || actionIntent){
                    startActivity(new Intent(this,MainActivity.class));
                    finish();
                }
                else{
                    super.onBackPressed();
                }
            }
        }
        else{
            if(this.isTaskRoot() || actionIntent){
                startActivity(new Intent(this,MainActivity.class));
                finish();
            }
            else{
                super.onBackPressed();
            }
        }


    }

    private void setProfile(SonataUser user){

        if(user.getBlock()){
            followButtonText.setText(getString(R.string.unblock));
            followButtonText.setTextColor(getResources().getColor(R.color.white));
            followLayout.setBackground(getResources().getDrawable(R.drawable.button_background_engel));
        }
        else{
            if(user.getFollow()){
                followButtonText.setText(getString(R.string.unfollow));
                followButtonText.setTextColor(getResources().getColor(R.color.white));
                followLayout.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
            }
            else{
                if(user.getFollowRequest()){
                    followButtonText.setText(getString(R.string.requestsent));
                    followButtonText.setTextColor(getResources().getColor(R.color.white));
                    followLayout.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
                }
                else{
                    followButtonText.setText(getString(R.string.follow));
                    followButtonText.setTextColor(getResources().getColor(R.color.blue));
                    followLayout.setBackground(getResources().getDrawable(R.drawable.button_background));
                }

            }
        }

        username.setText(user.getUsername());

        if(user.getHasPp()){
            Glide.with(this)
                    .load(user.getPPbig())
                    .thumbnail(
                            Glide.with(this)
                                    .load(user.getPPAdapter()))
                    .into(profilephoto);
        }
        else{
            Glide.with(profilephoto.getContext()).load(getResources().getDrawable(R.drawable.emptypp)).into(profilephoto);
        }

        followingnumber.setText(GenelUtil.ConvertNumber((int)user.getFollowing(),this));
        followernumber.setText(GenelUtil.ConvertNumber((int)user.getFollower(),this));

        followernumber.setOnClickListener(null);
        followertext.setOnClickListener(null);

        followingnumber.setOnClickListener(null);
        followingtext.setOnClickListener(null);


        if(!user.getPrivate()){
            followingtext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GuestProfileActivity.this, FollowingsActivity.class).putExtra("id",user.getObjectId()));
                }
            });
            followingnumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GuestProfileActivity.this, FollowingsActivity.class).putExtra("id",user.getObjectId()));

                }
            });
            followertext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GuestProfileActivity.this, FollowersActivity.class).putExtra("id",user.getObjectId()));

                }
            });
            followernumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(GuestProfileActivity.this, FollowersActivity.class).putExtra("id",user.getObjectId()));
                }
            });
        }
        else{
            if(user.getFollow()){
                followingtext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(GuestProfileActivity.this, FollowingsActivity.class).putExtra("id",user.getObjectId()));
                    }
                });
                followingnumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(GuestProfileActivity.this, FollowingsActivity.class).putExtra("id",user.getObjectId()));

                    }
                });
                followertext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(GuestProfileActivity.this, FollowersActivity.class).putExtra("id",user.getObjectId()));

                    }
                });
                followernumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(GuestProfileActivity.this, FollowersActivity.class).putExtra("id",user.getObjectId()));
                    }
                });
            }
        }


        name.setText(user.getName());
        if(user.getBio()==null||user.getBio().length()==0){
            bio.setVisibility(View.GONE);
        }
        else{
            bio.setVisibility(View.VISIBLE);
            bio.setText(user.getBio());
        }

    }

    private void setProfile1(SonataUser user){



        username.setText(user.getUsername());

        if(user.getHasPp()){
            Glide.with(this)
                    .load(user.getPPbig())
                    .thumbnail(
                            Glide.with(this)
                                    .load(user.getPPAdapter()))
                    .into(profilephoto);
        }
        else{
            Glide.with(profilephoto.getContext()).load(getResources().getDrawable(R.drawable.emptypp)).into(profilephoto);
        }

        followingnumber.setText(GenelUtil.ConvertNumber((int)user.getFollowing(),this));
        followernumber.setText(GenelUtil.ConvertNumber((int)user.getFollower(),this));




        name.setText(user.getName());
        if(user.getBio()==null||user.getBio().length()==0){
            bio.setVisibility(View.GONE);
        }
        else{
            bio.setVisibility(View.VISIBLE);
            bio.setText(user.getBio());
        }

    }

    private void handleSuggest(){
        if(suggestAdapter!=null){
            if(suggestList != null){
                if(list.size()==0){
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("userID",user.getObjectId());
                    params.put("lang", GenelUtil.getCurrentCountryCode(this));
                    ParseCloud.callFunctionInBackground("getSuggestionProfiles", params, new FunctionCallback<HashMap>() {
                        @Override
                        public void done(HashMap  objects, ParseException e) {
                            Log.e("done","done");
                            if(GenelUtil.isAlive(GuestProfileActivity.this)){
                                if(e==null){
                                    if(objects!= null){
                                        if(((List<SonataUser>)objects.get("profiles")).size()<1){
                                            suggestLayout.setVisibility(View.GONE);
                                            return;
                                        }
                                        suggestLayout.setVisibility(View.VISIBLE);
                                        for(int i=0;i<((List<SonataUser>)objects.get("profiles")).size();i++){

                                            ListObject post = new ListObject();
                                            post.setType("user");
                                            post.setUser(((List<SonataUser>)objects.get("profiles")).get(i));
                                            suggestList.add(post);

                                        }

                                        suggestAdapter.notifyDataSetChanged();
                                    }
                                    else{
                                        suggestLayout.setVisibility(View.GONE);
                                    }

                                }
                                else{
                                    suggestLayout.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }

        }
        else{
            suggestList=new ArrayList<>();


            suggestLayoutManager = new LinearLayoutManager(GuestProfileActivity.this);
            suggestLayoutManager.setOrientation(RecyclerView.VERTICAL);

            suggestRecyclerView.setLayoutManager(suggestLayoutManager);
            suggestAdapter=new BlockedPersonAdapter();
            suggestAdapter.setContext(suggestList, Glide.with(GuestProfileActivity.this), new BlockedAdapterClick() {
                @Override
                public void goToProfileClick(int position) {
                    SonataUser user = suggestList.get(position).getUser();
                    if(GenelUtil.clickable(700)){
                        if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                            startActivity(new Intent(GuestProfileActivity.this, GuestProfileActivity.class).putExtra("user",user));
                        }
                    }
                }

                @Override
                public void buttonClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay) {
                    SonataUser user = suggestList.get(position).getUser();
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
                                            GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
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
                                            GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
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
                                            GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
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
                                            GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
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
                                        GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
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
                                        GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
                                    }


                                }
                            });

                        }
                    }

                }
            });
            suggestRecyclerView.setAdapter(suggestAdapter);
            suggestAdapter.notifyDataSetChanged();

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("userID",user.getObjectId());
            ParseCloud.callFunctionInBackground("getSuggestionProfiles", params, new FunctionCallback<HashMap>() {
                @Override
                public void done(HashMap  objects, ParseException e) {
                    Log.e("done","done");
                    if(GenelUtil.isAlive(GuestProfileActivity.this)){
                        if(e==null){
                            if(objects!= null){
                                if(((List<SonataUser>)objects.get("profiles")).size()<1){
                                    suggestLayout.setVisibility(View.GONE);
                                    return;
                                }
                                suggestLayout.setVisibility(View.VISIBLE);
                                for(int i=0;i<((List<SonataUser>)objects.get("profiles")).size();i++){

                                    ListObject post = new ListObject();
                                    post.setType("user");
                                    post.setUser(((List<SonataUser>)objects.get("profiles")).get(i));
                                    suggestList.add(post);

                                }

                                suggestAdapter.notifyDataSetChanged();
                            }
                            else{
                                suggestLayout.setVisibility(View.GONE);
                            }

                        }
                        else{
                            suggestLayout.setVisibility(View.GONE);
                        }
                    }
                }
            });

        }
    }



    private void setFollowClick(SonataUser user) {
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(followButtonText.getText().toString().equals(getString(R.string.follow))){
                    if(user.getPrivate()){
                        //Takip İsteği gönder
                        followButtonText.setText(getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("sendFollowRequest", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(GenelUtil.isAlive(GuestProfileActivity.this)){
                                    if(e==null){
                                        followLayout.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
                                        user.setFollowRequest(true);
                                        followButtonText.setText(getString(R.string.requestsent));
                                        followButtonText.setTextColor(Color.WHITE);
                                    }
                                    else{
                                        followButtonText.setText(getString(R.string.follow));
                                        GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
                                    }
                                }

                            }
                        });
                    }
                    else{
                        //takip et
                        followButtonText.setText(getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("follow", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(GenelUtil.isAlive(GuestProfileActivity.this)){
                                    if(e==null){
                                        user.setFollow(true);
                                        followButtonText.setTextColor(getResources().getColor(R.color.white));

                                        followLayout.setBackground(getResources().getDrawable(R.drawable.button_background_dolu));
                                        followButtonText.setText(getString(R.string.unfollow));
                                    }
                                    else{
                                        followButtonText.setText(getString(R.string.follow));
                                        GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
                                    }
                                }

                            }
                        });

                    }
                    handleSuggest();

                }
                if(followButtonText.getText().toString().equals(getString(R.string.unfollow))){
                    if(user.getPrivate()){
                        //takipten çık ve profili gizle
                        followButtonText.setText(getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("unfollow", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(GenelUtil.isAlive(GuestProfileActivity.this)){
                                    if(e==null){
                                        user.setFollow(false);
                                        followButtonText.setText(getString(R.string.follow));
                                        followButtonText.setTextColor(getResources().getColor(R.color.blue));
                                        followLayout.setBackground(getResources().getDrawable(R.drawable.button_background));

                                        refreshSetting();
                                        ListObject load = new ListObject();
                                        load.setType("private");
                                        list.add(load);
                                        adapter.notifyDataSetChanged();
                                    }
                                    else{
                                        followButtonText.setText(getString(R.string.unfollow));
                                        GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
                                    }
                                }

                            }
                        });
                    }
                    else{
                        //takipten çık
                        followButtonText.setText(getString(R.string.loading));
                        HashMap<String,String> params = new HashMap<>();
                        params.put("userID",user.getObjectId());
                        ParseCloud.callFunctionInBackground("unfollow", params, new FunctionCallback<Object>() {
                            @Override
                            public void done(Object object, ParseException e) {
                                if(GenelUtil.isAlive(GuestProfileActivity.this)){
                                    if(e==null){
                                        user.setFollow(false);
                                        followButtonText.setText(getString(R.string.follow));
                                        followButtonText.setTextColor(getResources().getColor(R.color.blue));
                                        followLayout.setBackground(getResources().getDrawable(R.drawable.button_background));

                                    }
                                    else{
                                        followButtonText.setText(getString(R.string.unfollow));
                                        GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
                                    }
                                }

                            }
                        });
                    }
                }
                if(followButtonText.getText().toString().equals(getString(R.string.unblock))){
                    //engeli kaldır
                    progressDialog.setMessage(getString(R.string.unblock));
                    progressDialog.show();
                    HashMap<String,String> params = new HashMap<>();
                    params.put("userID",user.getObjectId());
                    ParseCloud.callFunctionInBackground("unblock", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if(GenelUtil.isAlive(GuestProfileActivity.this)){
                                if(e==null){
                                    user.setBlock(false);
                                    progressDialog.dismiss();
                                    followButtonText.setText(getString(R.string.follow));
                                    followButtonText.setTextColor(getResources().getColor(R.color.blue));
                                    followLayout.setBackground(getResources().getDrawable(R.drawable.button_background));

                                }
                                else{
                                    GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
                                }
                            }

                        }
                    });

                }
                if(followButtonText.getText().toString().equals(getString(R.string.requestsent))){
                    //isteği geri çek
                    followButtonText.setText(getString(R.string.loading));
                    HashMap<String,String> params = new HashMap<>();
                    params.put("userID",user.getObjectId());
                    ParseCloud.callFunctionInBackground("removeFollowRequest", params, new FunctionCallback<Object>() {
                        @Override
                        public void done(Object object, ParseException e) {
                            if(GenelUtil.isAlive(GuestProfileActivity.this)){
                                if(e==null){
                                    user.setFollowRequest(false);
                                    followButtonText.setText(getString(R.string.follow));
                                    followButtonText.setTextColor(getResources().getColor(R.color.blue));
                                    followLayout.setBackground(getResources().getDrawable(R.drawable.button_background));
                                }
                                else{
                                    followButtonText.setText(getString(R.string.requestsent));
                                    GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
                                }
                            }

                        }
                    });

                }

            }
        });



    }

    private void getPosts(Date date,boolean isRefresh){
        HashMap<String, Object> params = new HashMap<String, Object>();
        if(date!=null){
            params.put("date", date);
        }
        params.put("userID",user.getObjectId());
        ParseCloud.callFunctionInBackground("getGuestPosts", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap  objects, ParseException e) {
                Log.e("done","done");
                if(GenelUtil.isAlive(GuestProfileActivity.this)){
                    if(e==null){

                        if(objects!= null){
                            if(isRefresh){
                                list.clear();
                                adapter.notifyDataSetChanged();
                                if(postAdapter!=null){
                                    postAdapter.notifyDataSetChanged();
                                }
                            }
                            initList((List<Post>) objects.get("posts")
                                    ,(boolean) objects.get("hasmore")
                                    ,(Date) objects.get("date"));

                        }


                    }
                    else{
                        Log.e("error code",""+e.getCode());

                        getPosts(date,isRefresh);
                    }
                }
            }
        });
    }





    private void initList(List<Post> objects,boolean hasmore,Date date) {
        if(GenelUtil.isAlive(GuestProfileActivity.this)){
            postson = !hasmore;
            this.date = date;
            if(objects.size()==0){
                loading =false;
                if(list!=null){
                    if(list.size()==0){
                        ListObject post = new ListObject();
                        post.setType("boş");
                        list.add(post);
                        adapter.notifyItemInserted(0);
                        if(postAdapter!=null){
                            postAdapter.notifyItemInserted(0);
                        }
                    }
                    else{
                        if(list.get(list.size()-1).getType().equals("load")){
                            int in = list.size()-1;
                            list.remove(in);
                            adapter.notifyItemRemoved(in);
                            if(postAdapter!=null){
                                postAdapter.notifyItemRemoved(in);
                            }
                        }
                        if(list.size()==0){
                            ListObject post = new ListObject();
                            post.setType("boş");
                            list.add(post);
                            adapter.notifyItemInserted(0);
                            if(postAdapter!=null){
                                postAdapter.notifyItemInserted(0);
                            }
                        }


                    }
                }
                swipeRefreshLayout.setRefreshing(false);
                Log.e("done","adapterNotified");


            }

            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        int in = list.size()-1;
                        list.remove(in);
                        adapter.notifyItemRemoved(in);
                        if(postAdapter!=null){
                            postAdapter.notifyItemRemoved(in);
                        }
                    }
                }
                int an = list.size();
                for(int i=0;i<objects.size();i++){
                    ListObject post = new ListObject();
                    post.setType(objects.get(i).getType());
                    Post p2 = objects.get(i);
                    p2.setLikenumber(p2.getLikenumber2());
                    p2.setCommentnumber(p2.getCommentnumber2());
                    p2.setSaved(p2.getSaved2());
                    p2.setCommentable(p2.getCommentable2());
                    p2.setLiked(p2.getLiked2());
                    post.setPost(p2);
                    list.add(post);
                }

                if(hasmore){
                    ListObject load = new ListObject();
                    load.setType("load");
                    list.add(load);
                }
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyItemRangeInserted(an, list.size()-an);
                if(postAdapter!=null){
                    postAdapter.notifyItemRangeInserted(an, list.size()-an);
                }
                loading = false;



            }
        }

    }






    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
        if(postAdapter!=null){
            postAdapter.notifyDataSetChanged();
        }
        if(suggestAdapter!=null){
            suggestAdapter.notifyDataSetChanged();
        }
        if(user!=null){
            setProfile(user);
        }
    }

    private void getUser(@Nullable String userID, @Nullable String username,boolean isRefresh){
        HashMap<String, Object> params = new HashMap<String, Object>();
        if(userID!=null){
            params.put("userID", userID);
        }
        if(username!=null){
            params.put("username",username);
        }
        ParseCloud.callFunctionInBackground("getGuestProfile", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap  objects, ParseException e) {
                if(GenelUtil.isAlive(GuestProfileActivity.this)){
                    Log.e("userload done","done");
                    if(e==null){
                        if(objects!=null){

                            user=(SonataUser) objects.get("user");
                            user.setFollow(user.getFollow2());
                            user.setFollowRequest(user.getFollowRequest2());
                            user.setBlock(user.getBlock2());
                            setProfile(user);
                            setOptionsClick(user);
                            setFollowClick(user);


                            if(user.getPrivate()){
                                if(user.getFollow()){
                                    adapter.setUser(user);

                                    //getPosts(date,isRefresh);
                                    HashMap ps = (HashMap) objects.get("posts");
                                    if(ps!= null){
                                        if(isRefresh){
                                            list.clear();
                                            adapter.notifyDataSetChanged();
                                            if(postAdapter!=null){
                                                postAdapter.notifyDataSetChanged();
                                            }
                                        }
                                        initList((List<Post>) ps.get("posts")
                                                ,(boolean) ps.get("hasmore")
                                                ,(Date) ps.get("date"));

                                    }

                                }
                                else{
                                    if(isRefresh){
                                        refreshSetting();
                                    }
                                    if(list.size()>0){
                                        if(list.get(list.size()-1).getType().equals("load")){
                                            list.remove(list.get(list.size()-1));
                                        }
                                    }

                                    ListObject load = new ListObject();
                                    load.setType("private");
                                    list.add(load);
                                    swipeRefreshLayout.setRefreshing(false);
                                    loading = false;
                                    adapter.notifyDataSetChanged();
                                }
                            }
                            else{
                                adapter.setUser(user);
                                //getPosts(date,isRefresh);
                                HashMap ps = (HashMap) objects.get("posts");
                                if(ps!= null){
                                    if(isRefresh){
                                        list.clear();
                                        adapter.notifyDataSetChanged();
                                        if(postAdapter!=null){
                                            postAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    initList((List<Post>) ps.get("posts")
                                            ,(boolean) ps.get("hasmore")
                                            ,(Date) ps.get("date"));

                                }
                            }



                            loadingLayout.setVisibility(View.GONE);
                            mainLayout.setVisibility(View.VISIBLE);



                        }
                    }
                    else{
                        if(e.getCode()==ParseException.CONNECTION_FAILED){
                            getUser(userID,username,isRefresh);
                        }

                    }

                }

            }
        });
    }


    private void setOptionsClick (SonataUser user){
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> other = new ArrayList<>();
                if (user.getBlock()) {
                    other.add(getString(R.string.unblock));
                } else {
                    other.add(getString(R.string.block));
                    other.add(getString(R.string.sendmessage));
                }
                other.add(getString(R.string.copylink));
                other.add(getString(R.string.share));

                String[] popupMenu = other.toArray(new String[other.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(GuestProfileActivity.this);
                builder.setCancelable(true);

                builder.setItems(popupMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String select = popupMenu[which];
                        if (select.equals(getString(R.string.block))) {
                            progressDialog.setMessage(getString(R.string.block));
                            progressDialog.show();
                            HashMap<String,String> params = new HashMap<>();
                            params.put("userID",user.getObjectId());
                            ParseCloud.callFunctionInBackground("block", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {
                                    if(GenelUtil.isAlive(GuestProfileActivity.this)){
                                        if(e==null){
                                            user.setBlock(true);
                                            progressDialog.dismiss();
                                            followButtonText.setText(getString(R.string.unblock));
                                            followButtonText.setTextColor(getResources().getColor(R.color.white));
                                            followLayout.setBackground(getResources().getDrawable(R.drawable.button_background_engel));

                                        }
                                        else{
                                            GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
                                        }
                                    }

                                }
                            });

                        }
                        else if (select.equals(getString(R.string.unblock))) {
                            progressDialog.setMessage(getString(R.string.unblock));
                            progressDialog.show();
                            HashMap<String,String> params = new HashMap<>();
                            params.put("userID",user.getObjectId());
                            ParseCloud.callFunctionInBackground("unblock", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object object, ParseException e) {
                                    if(GenelUtil.isAlive(GuestProfileActivity.this)){
                                        if(e==null){
                                            user.setBlock(false);
                                            progressDialog.dismiss();
                                            followButtonText.setText(getString(R.string.follow));
                                            followButtonText.setTextColor(getResources().getColor(R.color.blue));
                                            followLayout.setBackground(getResources().getDrawable(R.drawable.button_background));

                                        }
                                        else{
                                            GenelUtil.ToastLong(GuestProfileActivity.this,getString(R.string.error));
                                        }
                                    }

                                }
                            });

                        }
                        else if (select.equals(getString(R.string.sendmessage))) {
                            startActivity(new Intent(GuestProfileActivity.this, DirectMessageActivity.class).putExtra("user",user));
                        }
                        else if(select.equals(getString(R.string.share))){
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, GenelUtil.getUrlOfObject(user));
                            sendIntent.setType("text/plain");

                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            startActivity(shareIntent);
                        }
                        else if(select.equals(getString(R.string.copylink))){
                            GenelUtil.copyText(GenelUtil.getUrlOfObject(user),GuestProfileActivity.this);
                        }
                    }
                });
                builder.show();


            }
        });

    }


    @Override
    public void onOptionsClick(int position, TextView commentNumber) {
        GenelUtil.handlePostOptionsClick(this,position,list,adapter,commentNumber);
    }

    @Override
    public void onSocialClick(int position, int clickType, String text) {
        GenelUtil.handleLinkClicks(this,text,clickType);
    }

    @Override
    public void onLikeClick(int position, ImageView likeImage, TextView likeNumber) {
        Post post = list.get(position).getPost();
        if(!post.getLiked()){
            post.setLiked(true);

            likeImage.setImageDrawable(GuestProfileActivity.this.getDrawable(R.drawable.ic_like_red));
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("postID", post.getObjectId());
            ParseCloud.callFunctionInBackground("likePost", params);


            post.increment("likenumber");
            likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),GuestProfileActivity.this));


        }
        else{
            post.setLiked(false);
            likeImage.setImageDrawable(GuestProfileActivity.this.getDrawable(R.drawable.ic_like));
            if(post.getLikenumber()>0){

                post.increment("likenumber",-1);
                likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),GuestProfileActivity.this));                                                    }
            else{
                post.setLikenumber(0);
                likeNumber.setText("0");
            }
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("postID", post.getObjectId());
            ParseCloud.callFunctionInBackground("unlikePost", params);

        }
    }

    @Override
    public void onGoToProfileClick(int position) {
        Post post = list.get(position).getPost();
        if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            startActivity(new Intent(GuestProfileActivity.this, GuestProfileActivity.class).putExtra("user",post.getUser()));
        }
    }

    @Override
    public void onOpenComments(int position) {
        if(listPostLayout.getVisibility()==View.INVISIBLE){
            if(postAdapter == null){
                postAdapter = new SafPostAdapter();
                postAdapter.setContext(list,Glide.with(GuestProfileActivity.this),GuestProfileActivity.this,user);

                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                postRecyclerView.setLayoutManager(linearLayoutManager);
                postRecyclerView.setAdapter(postAdapter);

                postOnScrollListener = new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            if(postRecyclerView!=null&&linearLayoutManager!=null){
                                AutoPlayUtils.onScrollPlayVideo(recyclerView, R.id.masterExoPlayer, linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition());
                                if(!recyclerView.canScrollVertically(-1)){
                                    Jzvd.releaseAllVideos();
                                }
                            }
                        }
                        int llm = linearLayoutManager.findLastVisibleItemPosition();
                        for(int a = llm; a < Math.min(llm+5,list.size()); a++){
                            try{
                                Post post = list.get(a).getPost();

                                if(post != null ){
                                    SonataUser user = post.getUser();
                                    if(user != null){
                                        String url = user.getPPAdapter();
                                        Glide.with(GuestProfileActivity.this).load(url).preload();
                                    }
                                    if(post.getType().equals("video")){
                                        HashMap<String,Object> mediaObject = post.getMediaList().get(0);
                                        ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                        String url = parseFile.getUrl();
                                        DownloadManager.getInstance(GuestProfileActivity.this)
                                                .enqueue(new DownloadManager.Request(MediaLoader.getInstance(GuestProfileActivity.this).getProxyUrl(url)));
                                        ParseFile thumb = (ParseFile) mediaObject.get("thumbnail");
                                        String thumburl = thumb.getUrl();
                                        Glide.with(GuestProfileActivity.this).load(thumburl).preload();
                                    }
                                    else{
                                        for (int im = 0; im < post.getImageCount(); im++){
                                            HashMap<String,Object> mediaObject = post.getMediaList().get(im);
                                            ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                            String url = parseFile.getUrl();
                                            Glide.with(GuestProfileActivity.this).load(url).preload();
                                        }
                                    }
                                }
                            } catch (Exception ignored){}

                        }
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if(((LinearLayoutManager)postRecyclerView.getLayoutManager()).findFirstVisibleItemPosition()>(list.size()-4)&&!loading&&!postson){
                            loading=true;
                            getPosts(date,false);
                        }
                    }
                };
                postRecyclerView.addOnScrollListener(postOnScrollListener);
                postAdapter.notifyDataSetChanged();

                postRecyclerView.scrollToPosition(position);
                back2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Jzvd.releaseAllVideos();
                        listPostLayout.setVisibility(View.INVISIBLE);
                        mainLayout.setVisibility(View.VISIBLE);
                    }
                });
                mainLayout.setVisibility(View.INVISIBLE);
                listPostLayout.setVisibility(View.VISIBLE);
                postRecyclerView.smoothScrollBy(0,1);

            }
            else{

                postRecyclerView.scrollToPosition(position);
                mainLayout.setVisibility(View.INVISIBLE);
                listPostLayout.setVisibility(View.VISIBLE);
                postRecyclerView.smoothScrollBy(0,1);
            }
        }
        else{
            Post post = list.get(position).getPost();
            if(post.getCommentable()){
                startActivity(new Intent(this,CommentActivity.class)
                        .putExtra("post",post));
            }
        }
    }

    @Override
    public void onImageClick(int position,ImageView imageView,int pos) {
        Post post = list.get(position).getPost();
        List<String> ulist = new ArrayList<>();

        for(int i = 0; i < post.getImageCount(); i++){
            ulist.add(String.valueOf(i));
        }
        GenelUtil.showImage(ulist,post.getMediaList(),imageView,pos,adapter);
    }

}
