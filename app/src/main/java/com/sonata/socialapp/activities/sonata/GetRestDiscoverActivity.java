package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.os.ConfigurationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.SafPostAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;

public class GetRestDiscoverActivity extends AppCompatActivity implements RecyclerViewClick {


    List<ListObject> list;
    List<String> seenList;

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

        seenList = new ArrayList<>();
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

        post = getIntent().getParcelableExtra("post");
        if(post != null) {
            kategori = post.getUser() != null ? post.getUser().getContent() : new ArrayList<>();
            list=new ArrayList<>();
            ListObject posto = new ListObject();
            posto.setType(post.getType());
            posto.setPost(post);
            seenList.add(post.getObjectId());
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
        else{
            String id = getIntent().getStringExtra("id");
            seenList.add(id);
            if(id!=null){

                list=new ArrayList<>();

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
                    getPost(id);
                    //getReqs(null,false,kategori);
                }
            }
        }


    }

    private void getPost(String id) {
        HashMap<String,String> params = new HashMap<>();
        params.put("id",id);
        ParseCloud.callFunctionInBackground("updatePost", params, new FunctionCallback<Post>() {
            @Override
            public void done(Post post, ParseException e) {
                if(e==null){
                    kategori = post.getUser() != null ? post.getUser().getContent() : new ArrayList<>();
                    GetRestDiscoverActivity.this.post = post;
                    ListObject posto = new ListObject();
                    posto.setType(post.getType());
                    posto.setPost(post);
                    if(list.size()>0){
                        if(list.get(list.size()-1).getType().equals("load")){
                            list.remove(list.size()-1);

                        }
                    }
                    list.add(posto);
                    adapter.notifyDataSetChanged();
                    getReqs(null,false,kategori);
                }
            }
        });
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
            params.put("seenList",seenList);
            params.put("lang", ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0).toString());
            params.put("text",hashtag);
            ParseCloud.callFunctionInBackground("getRestOfTheDiscoverPost", params, (FunctionCallback<HashMap>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(GetRestDiscoverActivity.this)){
                    if(e==null){
                        Log.e("hashmap ",objects.toString());
                        List<Post> tpL = (List<Post>) objects.get("posts");
                        if(tpL != null){
                            for(int ii = 0; ii < tpL.size(); ii++){
                                String id = tpL.get(ii).getObjectId();
                                if(!seenList.contains(id) && id != null){
                                    seenList.add(id);
                                }
                            }

                            if(isRefresh){
                                listreklam.clear();
                                listreklam = null;
                                listreklam=new ArrayList<>();
                            }
                            getAds(((List<Post>)objects.get("posts")),((boolean)objects.get("hasmore")),((Date)objects.get("date")),isRefresh);
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

    private void initList(List<Post> objects,boolean hasMore,Date date,List<UnifiedNativeAd> listreklam) {
        Log.e("done","InitList");

        if(GenelUtil.isAlive(this)){
            Log.e("done","InitListActive");
            postson = !hasMore;
            this.date = date;
            if(objects.size()==0){
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
                        if(list.size()==0){
                            ListObject post = new ListObject();
                            post.setType("boş");
                            list.add(post);
                            adapter.notifyItemInserted(0);
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
                if(!postson){
                    ListObject load = new ListObject();
                    load.setType("load");
                    list.add(load);
                }

                adapter.notifyItemRangeInserted(an, list.size()-an);

                //adapter.notifyDataSetChanged();
                Log.e("done","adapterNotified");

            }
        }
        else{
            Log.e("done","InitListNotActive");

        }

    }
    int loadCheck = 0;
    private void getAds(List<Post> objects,boolean hasMore,Date date,boolean isRefresh){
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
                    adapter.notifyDataSetChanged();
                }
                initList(objects,hasMore,date,new ArrayList<>());
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
                                            adapter.notifyDataSetChanged();

                                        }
                                        loadCheck=0;
                                        initList(objects,hasMore,date,tempList);
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
                                                adapter.notifyDataSetChanged();

                                            }
                                            loadCheck=0;
                                            initList(objects,hasMore,date,tempList);
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
                                    adapter.notifyDataSetChanged();

                                }
                                loadCheck=0;
                                initList(objects,hasMore,date,new ArrayList<>());
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
    public void onImageClick(int position,ImageView imageView,int pos) {
        Post post = list.get(position).getPost();
        List<String> ulist = new ArrayList<>();

        for(int i = 0; i < post.getImageCount(); i++){
            ulist.add(String.valueOf(i));
        }
        GenelUtil.showImage(ulist,post.getMediaList(),imageView,pos,adapter);
    }


    @Override
    public void onBackPressed() {
        if(getIntent()!=null) {
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
        }
        else{
            super.onBackPressed();
        }
    }
}
