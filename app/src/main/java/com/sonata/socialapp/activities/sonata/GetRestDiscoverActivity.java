package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.HomeAdapter;
import com.sonata.socialapp.utils.adapters.SafPostAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;
import jp.wasabeef.glide.transformations.BlurTransformation;

public class GetRestDiscoverActivity extends AppCompatActivity implements RecyclerViewClick {


    List<ListObject> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    SafPostAdapter adapter;
    private boolean loading=true;
    private List<UnifiedNativeAd> listreklam;
    List<String> kategori;
    Date date;
    RecyclerView.OnScrollListener onScrollListener;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    RelativeLayout back;

    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_rest_discover);
        if(getIntent() == null) return;
        post = getIntent().getParcelableExtra("post");
        if(post == null) return;
        kategori = post.getUser() != null ? post.getUser().getContent() : new ArrayList<>();
        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(view -> onBackPressed());

        recyclerView = findViewById(R.id.folreqrecyclerview);
        swipeRefreshLayout = findViewById(R.id.folreqSwipeRefreshLayout);
        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(recyclerView != null&&linearLayoutManager!=null){
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
                    getReqs(date,false,kategori);

                }


            }
        };


        list=new ArrayList<>();
        ListObject posto = new ListObject();
        posto.setType(post.getType());
        posto.setPost(post);
        list.add(posto);
        listreklam = new ArrayList<>();
        ListObject object2 = new ListObject();
        object2.setType("load");
        list.add(object2);
        linearLayoutManager=new LinearLayoutManager(GetRestDiscoverActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new SafPostAdapter();
        adapter.setContext(list, Glide.with(GetRestDiscoverActivity.this),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollBy(0,1);

        onRefreshListener = () -> {
            if(!loading){
                loading=true;

                date=null;

                getReqs(null,true,kategori);

            }
        };
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);



        recyclerView.addOnScrollListener(onScrollListener);

        if(GenelUtil.isAlive(this)){
            getReqs(null,false,kategori);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }




    private void getReqs(Date date,boolean isRefresh,List<String> hashtag){
        if(GenelUtil.isAlive(this)){
            HashMap<String, Object> params = new HashMap<>();
            if(date!=null){
                params.put("date",date);
            }
            params.put("lang", ParseInstallation.getCurrentInstallation() != null ? ParseInstallation.getCurrentInstallation().getString("localeIdentifier") : "en");
            params.put("text",hashtag);
            ParseCloud.callFunctionInBackground("getRestOfTheDiscoverPost", params, (FunctionCallback<List<Post>>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(GetRestDiscoverActivity.this)){
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
                        getReqs(date,isRefresh,hashtag);


                    }
                }
            });
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

                swipeRefreshLayout.setRefreshing(false);
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
                swipeRefreshLayout.setRefreshing(false);
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
    private void getAds(List<Post> objects,boolean isRefresh){
        Log.e("done","doneGetAds");

        if(GenelUtil.isAlive(GetRestDiscoverActivity.this)){
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
                if(isRefresh){
                    //refreshSetting();
                    list.clear();
                }
                initList(objects,new ArrayList<>(),isRefresh);
            }
            else{
                int finalC = c;
                final boolean[] isfinish = {false};
                List<UnifiedNativeAd> tempList = new ArrayList<>();
                AdLoader adLoader = new AdLoader.Builder(GetRestDiscoverActivity.this, getString(R.string.adId))
                        .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                loadCheck++;
                                if(GenelUtil.isAlive(GetRestDiscoverActivity.this)){
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
                                    if(GenelUtil.isAlive(GetRestDiscoverActivity.this)){
                                        if(isRefresh){
                                            //refreshSetting();
                                            list.clear();

                                        }
                                        loadCheck=0;
                                        initList(objects,tempList,isRefresh);
                                    }

                                }
                            }
                        })
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError adError) {
                                if(GenelUtil.isAlive(GetRestDiscoverActivity.this)){
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
                                            initList(objects,tempList,isRefresh);
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

                            if(GenelUtil.isAlive(GetRestDiscoverActivity.this)){
                                if(isRefresh){
                                    //refreshSetting();
                                    list.clear();

                                }
                                loadCheck=0;
                                initList(objects,new ArrayList<>(),isRefresh);
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
    }

    @Override
    public void onOptionsClick(int position, TextView commentNumber) {
        Post post = list.get(position).getPost();
        ProgressDialog progressDialog = new ProgressDialog(GetRestDiscoverActivity.this);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(GetRestDiscoverActivity.this);
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
                                commentNumber.setText(GetRestDiscoverActivity.this.getString(R.string.disabledcomment));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(GetRestDiscoverActivity.this,getString(R.string.error));
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
                                GenelUtil.ToastLong(GetRestDiscoverActivity.this,getString(R.string.postsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(GetRestDiscoverActivity.this,getString(R.string.error));
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
                                GenelUtil.ToastLong(GetRestDiscoverActivity.this,getString(R.string.postunsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(GetRestDiscoverActivity.this,getString(R.string.error));
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
                                GenelUtil.ToastLong(GetRestDiscoverActivity.this,getString(R.string.reportsucces));
                                progressDialog.dismiss();
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(GetRestDiscoverActivity.this,getString(R.string.error));
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

                                commentNumber.setText(GenelUtil.ConvertNumber((int)post.getCommentnumber(),GetRestDiscoverActivity.this));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(GetRestDiscoverActivity.this,getString(R.string.error));
                            }
                        }
                    });

                }
                if(select.equals(getString(R.string.delete))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(GetRestDiscoverActivity.this);
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
                                        GenelUtil.ToastLong(GetRestDiscoverActivity.this,getString(R.string.error));
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
            startActivity(new Intent(GetRestDiscoverActivity.this, HashtagActivity.class).putExtra("hashtag",text.replace("#","")));

        }
        else if(clickType==HomeAdapter.TYPE_MENTION){
            //mention
            String username = text;

            username = username.replace("@","").trim();


            if(!username.equals(ParseUser.getCurrentUser().getUsername())){
                startActivity(new Intent(GetRestDiscoverActivity.this, GuestProfileActivity.class).putExtra("username",username));
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
            customTabsIntent.launchUrl(GetRestDiscoverActivity.this, Uri.parse(url));
        }
    }

    @Override
    public void onLikeClick(int position, ImageView likeImage, TextView likeNumber) {
        Post post = list.get(position).getPost();
        if(!post.getLiked()){
            post.setLiked(true);

            likeImage.setImageDrawable(GetRestDiscoverActivity.this.getDrawable(R.drawable.ic_like_red));
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("postID", post.getObjectId());
            ParseCloud.callFunctionInBackground("likePost", params);


            post.increment("likenumber");
            likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),GetRestDiscoverActivity.this));


        }
        else{
            post.setLiked(false);
            likeImage.setImageDrawable(GetRestDiscoverActivity.this.getDrawable(R.drawable.ic_like));
            if(post.getLikenumber()>0){

                post.increment("likenumber",-1);
                likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),GetRestDiscoverActivity.this));                                                    }
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
            startActivity(new Intent(GetRestDiscoverActivity.this, GuestProfileActivity.class).putExtra("user",post.getUser()));
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
        customTabsIntent.launchUrl(GetRestDiscoverActivity.this, Uri.parse(url));
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
            Glide.with(GetRestDiscoverActivity.this).load(post.getMainMedia().getUrl()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).addListener(new RequestListener<Drawable>() {
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
                Glide.with(GetRestDiscoverActivity.this).load(post.getMainMedia().getUrl()).override(iw,ih).thumbnail(Glide.with(GetRestDiscoverActivity.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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
                Glide.with(GetRestDiscoverActivity.this).load(post.getMainMedia().getUrl()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).thumbnail(Glide.with(GetRestDiscoverActivity.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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
