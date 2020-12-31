package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.HomeAdapter;
import com.sonata.socialapp.utils.adapters.SafPostAdapter;
import com.sonata.socialapp.utils.adapters.SearchUserAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;
import jp.wasabeef.glide.transformations.BlurTransformation;


public class SearchActivity extends AppCompatActivity implements RecyclerViewClick {

    SearchView searchView;
    RelativeLayout back;
    RelativeLayout mainLay;
    String searchText = "";

    //Post Stuff
    List<ListObject> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    SafPostAdapter adapter;
    String searchString = "";
    private boolean loading=true;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> listreklam;

    Date date;
    RecyclerView.OnScrollListener onScrollListener;


    //User stuf
    List<ListObject> listuser;
    RecyclerView recyclerViewuser;
    LinearLayoutManager linearLayoutManagerUser;
    SearchUserAdapter adapterUser;


    @Override
    protected void onDestroy() {
        for(int i = 0;i<listreklam.size();i++){
            listreklam.get(i).destroy();
        }
        super.onDestroy();
        adapter.setFinish(true);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(null);
        adapter = null;
        recyclerView=null;
        list.clear();
        listreklam.clear();
        listreklam=null;
        list=null;
        linearLayoutManager=null;
        date=null;
        adLoader = null;
        onScrollListener = null;
        back=null;
        searchView = null;
        mainLay = null;

        listuser.clear();
        listuser=null;
        recyclerViewuser.setAdapter(null);
        linearLayoutManagerUser=null;
        adapterUser=null;
    }


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
        setContentView(R.layout.activity_search);

        back = findViewById(R.id.backbuttonbutton);


        mainLay = findViewById(R.id.mainLay);

        list=new ArrayList<>();
        listuser = new ArrayList<>();
        listreklam = new ArrayList<>();


        recyclerView = findViewById(R.id.searchPostRecycler);
        recyclerViewuser = findViewById(R.id.profileRecyclerView);
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
                if(dy>0&&linearLayoutManager.findLastVisibleItemPosition()>(list.size()-7)&&!loading&&!postson){
                    loading=true;
                    getReqsPost(date,searchText);
                }
            }
        };

        linearLayoutManagerUser=new LinearLayoutManager(SearchActivity.this);
        linearLayoutManagerUser.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewuser.setLayoutManager(linearLayoutManagerUser);
        adapterUser=new SearchUserAdapter();
        adapterUser.setContext(listuser, Glide.with(SearchActivity.this));
        adapterUser.setHasStableIds(true);
        recyclerViewuser.setAdapter(adapterUser);

        linearLayoutManager=new LinearLayoutManager(SearchActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new SafPostAdapter();
        adapter.setContext(list, Glide.with(SearchActivity.this),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.searchView);
        searchView.requestFocus();
        GenelUtil.showKeyboard(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                recyclerViewuser.setVisibility(View.VISIBLE);
                refreshSettingPost();
                refreshSettingUser();
                ListObject object2 = new ListObject();
                object2.setType("load");
                list.add(object2);
                listuser.add(object2);
                adapterUser.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                if(GenelUtil.isAlive(SearchActivity.this)){
                    getReqs(searchText);
                }
                searchView.setIconified(false);
                //The above line will expand it to fit the area as well as throw up the keyboard

                //To remove the keyboard, but make sure you keep the expanded version:
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(view -> onBackPressed());










        recyclerView.addOnScrollListener(onScrollListener);




    }

    private void refreshSettingPost(){
        list.clear();
        postson=false;

    }

    private void refreshSettingUser(){
        adapterUser=null;
        listuser.clear();
        recyclerViewuser.setAdapter(null);
        listuser=null;
        listuser= new ArrayList<>();
        adapterUser = new SearchUserAdapter();
        adapterUser.setContext(listuser,Glide.with(this));
        recyclerViewuser.setAdapter(adapterUser);

    }

    private void getReqsPost(@NonNull Date date,String searchStr){
        if(GenelUtil.isAlive(this)){
            HashMap<String, Object> params = new HashMap<>();

            params.put("text",searchStr);
            params.put("date",date);
            ParseCloud.callFunctionInBackground("searchPost", params, (FunctionCallback<List<Post>>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(SearchActivity.this)){
                    if(e==null){
                        if(searchStr.equals(searchText)){
                            if(objects!= null){

                                getAds(objects,searchStr);
                                //initList(objects);
                            }
                        }

                    }
                    else{
                        getReqsPost(date,searchStr);


                    }
                }
            });
        }
    }

    private void getReqs(String searchStr){
        if(GenelUtil.isAlive(this)){
            HashMap<String, Object> params = new HashMap<>();
            params.put("text",searchStr);
            ParseCloud.callFunctionInBackground("search", params, (FunctionCallback<List<List>>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(SearchActivity.this)){
                    if(e==null){
                        if(searchText.equals(searchStr)){
                            if(objects!= null){
                                List<SonataUser> userList = (List<SonataUser>) objects.get(0);
                                List<Post> postList = (List<Post>) objects.get(1);
                                initUser(userList);

                                getAds(postList,searchStr);
                                //initList(objects);
                            }
                        }
                    }
                    else{
                        if(e.getCode()== ParseException.CONNECTION_FAILED){
                            getReqs(searchStr);
                        }
                    }
                }
            });
        }
    }

    private void initUser(List<SonataUser> objects) {
        if(GenelUtil.isAlive(SearchActivity.this)){
            if(objects.size()==0){

                recyclerViewuser.setVisibility(View.GONE);
            }
            else{
                for(int i=0;i<objects.size();i++){

                    ListObject post = new ListObject();
                    post.setType("user");
                    SonataUser p2 = objects.get(i);

                    post.setUser(p2);
                    listuser.add(post);

                }

                adapterUser.notifyDataSetChanged();

            }
        }

    }


    private void initList(List<Post> objects,List<UnifiedNativeAd> listreklam,boolean isrefresh) {
        Log.e("done","InitList");

        if(GenelUtil.isAlive(this)){
            Log.e("done","InitListActive");

            if(objects.size()==0){
                postson =true;
                loading =false;
                if(list!=null){
                    if(list.size()==0){
                        ListObject post = new ListObject();
                        post.setType("boş");
                        list.add(post);
                        adapter.notifyItemInserted(0);
                    }
                    else{
                        if(list.get(list.size()-1).getType().equals("load")){
                            int in = list.size()-1;
                            list.remove(in);
                            adapter.notifyItemRemoved(in);
                        }

                    }
                }

                Log.e("done","adapterNotified");


            }
            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        int in = list.size()-1;
                        list.remove(in);
                        adapter.notifyItemRemoved(in);
                    }
                }
                int an = list.size();
                date=objects.get(objects.size()-1).getCreatedAt();
                for(int i=0;i<objects.size();i++){
                    String a = String.valueOf(i+1);
                    if(2 == Integer.parseInt(a.substring(a.length() - 1))){
                        if(listreklam.size()>0){
                            ListObject reklam = new ListObject();
                            reklam.setType("reklam");
                            reklam.setAd(listreklam.get(0));
                            listreklam.remove(0);
                            list.add(reklam);
                        }
                    }
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

                loading =false;
                if(objects.size()<10){
                    postson = true;
                }
                else{
                    postson=false;
                    ListObject load = new ListObject();
                    load.setType("load");
                    list.add(load);
                }
                if(isrefresh){
                    adapter.notifyDataSetChanged();
                }
                else{
                    adapter.notifyItemRangeInserted(an, list.size()-an);
                }
                //adapter.notifyDataSetChanged();
                Log.e("done","adapterNotified");

            }
        }
        else{
            Log.e("done","InitListNotActive");

        }

    }
    int loadCheck = 0;
    private void getAds(List<Post> objects,String searchString){
        Log.e("done","doneGetAds");

        if(GenelUtil.isAlive(SearchActivity.this)){
            Log.e("done","doneGetAdsActive");



            int l = objects.size();
            int c = 0;
            if(l>1&&l<=11){
                c=1;
            }
            if(l>11&&l<=21){
                c=2;
            }
            if(l>21&&l<=31){
                c=3;
            }
            if(l>31&&l<=41){
                c=4;
            }
            if(l>41){
                c=5;
            }
            Log.e("Ad Count",""+c);
            if(c<=0){

                initList(objects,new ArrayList<>(),false);
            }
            else{
                int finalC = c;
                final boolean[] isfinish = {false};
                List<UnifiedNativeAd> tempList = new ArrayList<>();
                AdLoader adLoader = new AdLoader.Builder(SearchActivity.this, getString(R.string.adId))
                        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                loadCheck++;
                                if(GenelUtil.isAlive(SearchActivity.this)){
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
                                    if(GenelUtil.isAlive(SearchActivity.this)){

                                        loadCheck=0;
                                        initList(objects,tempList,false);
                                    }

                                }
                            }
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                if(GenelUtil.isAlive(SearchActivity.this)){
                                    loadCheck++;
                                    Log.e("adError: ",""+adError.getCode());
                                    Log.e("adError: ",""+adError.getCause());


                                    if(loadCheck==finalC){
                                        if(!isfinish[0]){
                                            isfinish[0] = true;
                                            Log.e("adError !isLoading: ",""+adError.getCode());

                                            loadCheck=0;
                                            initList(objects,tempList,false);
                                        }

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

                            if(GenelUtil.isAlive(SearchActivity.this)){

                                loadCheck=0;
                                initList(objects,new ArrayList<>(),false);
                            }


                        }
                    }
                }, Math.max(finalC * 6000, 12000));

                adLoader.loadAds(new AdRequest.Builder().build(), finalC);
            }






        }
        else{
            Log.e("done","doneGetNotActive");

        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
        if(adapterUser!=null){
            adapterUser.notifyDataSetChanged();
        }
    }



    @Override
    public void onOptionsClick(int position, TextView commentNumber) {
        Post post = list.get(position).getPost();
        ProgressDialog progressDialog = new ProgressDialog(SearchActivity.this);
        progressDialog.setCancelable(false);
        ArrayList<String> other = new ArrayList<>();
        if(post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            //Bu gönderi benim
            if(post.getCommentable()){
                other.add(getString(R.string.disablecomment));
            }
            if(!post.getCommentable()){
                other.add(getString(R.string.enablecomment));
            }
            other.add(getString(R.string.delete));

        }
        if(post.getSaved()){
            other.add(getString(R.string.unsavepost));
        }
        if(!post.getSaved()){
            other.add(getString(R.string.savepost));
        }
        if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            //Bu gönderi başkasına ait
            other.add(getString(R.string.report));
        }

        String[] popupMenu = other.toArray(new String[other.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
        builder.setCancelable(true);

        builder.setItems(popupMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String select = popupMenu[which];
                if(select.equals(getString(R.string.disablecomment))){

                    progressDialog.setMessage(getString(R.string.disablecomment));
                    progressDialog.show();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("enableDisableComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setCommentable(false);
                                progressDialog.dismiss();
                                commentNumber.setText(SearchActivity.this.getString(R.string.disabledcomment));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(SearchActivity.this,getString(R.string.error));
                            }
                        }
                    });

                }
                if(select.equals(getString(R.string.savepost))){
                    //savePost

                    progressDialog.setMessage(getString(R.string.savepost));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("savePost", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setSaved(true);
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(SearchActivity.this,getString(R.string.postsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(SearchActivity.this,getString(R.string.error));
                            }
                        }
                    });

                }
                if(select.equals(getString(R.string.unsavepost))){
                    //UnsavePost

                    progressDialog.setMessage(getString(R.string.unsavepost));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("unsavePost", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setSaved(false);
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(SearchActivity.this,getString(R.string.postunsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(SearchActivity.this,getString(R.string.error));
                            }
                        }
                    });
                }
                if(select.equals(getString(R.string.report))){

                    progressDialog.setMessage(getString(R.string.report));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("reportPost", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                GenelUtil.ToastLong(SearchActivity.this,getString(R.string.reportsucces));
                                progressDialog.dismiss();
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(SearchActivity.this,getString(R.string.error));
                            }
                        }
                    });


                }
                if(select.equals(getString(R.string.enablecomment))){

                    progressDialog.setMessage(getString(R.string.enablecomment));
                    progressDialog.show();

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("enableDisableComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setCommentable(true);
                                progressDialog.dismiss();

                                commentNumber.setText(GenelUtil.ConvertNumber((int)post.getCommentnumber(),SearchActivity.this));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(SearchActivity.this,getString(R.string.error));
                            }
                        }
                    });

                }
                if(select.equals(getString(R.string.delete))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
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
                            params.put("postID", post.getObjectId());
                            ParseCloud.callFunctionInBackground("deletePost", params, new FunctionCallback<String>() {
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
                                        GenelUtil.ToastLong(SearchActivity.this,getString(R.string.error));
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
            startActivity(new Intent(SearchActivity.this, HashtagActivity.class).putExtra("hashtag",text.replace("#","")));

        }
        else if(clickType==HomeAdapter.TYPE_MENTION){
            //mention
            String username = text;

            username = username.replace("@","").trim();


            if(!username.equals(ParseUser.getCurrentUser().getUsername())){
                startActivity(new Intent(SearchActivity.this, GuestProfileActivity.class).putExtra("username",username));
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
            customTabsIntent.launchUrl(SearchActivity.this, Uri.parse(url));
        }
    }

    @Override
    public void onLikeClick(int position, ImageView likeImage, TextView likeNumber) {
        Post post = list.get(position).getPost();
        if(!post.getLiked()){
            post.setLiked(true);

            likeImage.setImageDrawable(SearchActivity.this.getDrawable(R.drawable.ic_like_red));
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("postID", post.getObjectId());
            ParseCloud.callFunctionInBackground("likePost", params);


            post.increment("likenumber");
            likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),SearchActivity.this));


        }
        else{
            post.setLiked(false);
            likeImage.setImageDrawable(SearchActivity.this.getDrawable(R.drawable.ic_like));
            if(post.getLikenumber()>0){

                post.increment("likenumber",-1);
                likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),SearchActivity.this));                                                    }
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
            startActivity(new Intent(SearchActivity.this, GuestProfileActivity.class).putExtra("user",post.getUser()));
        }
    }

    @Override
    public void onOpenComments(int position) {
        Post post = list.get(position).getPost();
        if(post.getCommentable()){
            startActivity(new Intent(this,CommentActivity.class)
                    .putExtra("post",post));
        }
    }

    @Override
    public void onLinkClick(int position) {
        Post post = list.get(position).getPost();
        String url = post.getUrl();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        if(ParseUser.getCurrentUser().getBoolean("nightmode")){
            builder.setToolbarColor(Color.parseColor("#303030"));
        }
        else{
            builder.setToolbarColor(Color.parseColor("#ffffff"));
        }
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(SearchActivity.this, Uri.parse(url));
    }

    @Override
    public void onImageClick(int position,ImageView imageView,int pos) {
        Post post = list.get(position).getPost();
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
        Post post = list.get(position).getPost();

        reloadLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if(post.getNsfw()){
            Glide.with(SearchActivity.this).load(post.getMainMedia().getUrl()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).addListener(new RequestListener<Drawable>() {
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
                Glide.with(SearchActivity.this).load(post.getMainMedia().getUrl()).override(iw,ih).thumbnail(Glide.with(SearchActivity.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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
                Glide.with(SearchActivity.this).load(post.getMainMedia().getUrl()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).thumbnail(Glide.with(SearchActivity.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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
