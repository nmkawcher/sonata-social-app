package com.sonata.socialapp.activities.sonata;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.SafPostAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;
import com.vincan.medialoader.DownloadManager;
import com.vincan.medialoader.MediaLoader;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.jzvd.Jzvd;

public class SavedPostsActivity extends AppCompatActivity implements RecyclerViewClick {

    List<ListObject> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    SafPostAdapter adapter;
    private boolean loading=true;
    private AdLoader adLoader;
    private List<UnifiedNativeAd> listreklam;

    Date date;
    RecyclerView.OnScrollListener onScrollListener;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    RelativeLayout back;

    @Override
    protected void onDestroy() {
        recyclerView.removeOnScrollListener(onScrollListener);
        onScrollListener=null;
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
        swipeRefreshLayout.setOnRefreshListener(null);
        swipeRefreshLayout=null;
        onRefreshListener=null;
        back=null;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);
        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(view -> onBackPressed());

        recyclerView = findViewById(R.id.folreqrecyclerview);
        swipeRefreshLayout = findViewById(R.id.folreqSwipeRefreshLayout);
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
                int llm = linearLayoutManager.findLastVisibleItemPosition();
                for(int a = llm; a < Math.min(llm+5,list.size()); a++){
                    try{
                        Post post = list.get(a).getPost();

                        if(post != null ){
                            SonataUser user = post.getUser();
                            if(user != null){
                                String url = user.getPPAdapter();
                                Glide.with(SavedPostsActivity.this).load(url).preload();
                            }
                            if(post.getType().equals("video")){
                                HashMap<String,Object> mediaObject = post.getMediaList().get(0);
                                ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                String url = parseFile.getUrl();
                                DownloadManager.getInstance(SavedPostsActivity.this).enqueue(new DownloadManager.Request(MediaLoader.getInstance(SavedPostsActivity.this).getProxyUrl(url)));
                                ParseFile thumb = (ParseFile) mediaObject.get("thumbnail");
                                String thumburl = thumb.getUrl();
                                Glide.with(SavedPostsActivity.this).load(thumburl).preload();
                            }
                            else{
                                for (int im = 0; im < post.getImageCount(); im++){
                                    HashMap<String,Object> mediaObject = post.getMediaList().get(im);
                                    ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                    String url = parseFile.getUrl();
                                    Glide.with(SavedPostsActivity.this).load(url).preload();
                                }
                            }
                        }
                    } catch (Exception ignored){}

                }

            }
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0&&linearLayoutManager.findLastVisibleItemPosition()>(list.size()-7)&&!loading&&!postson){
                    loading=true;
                    postson=false;
                    getReqs(date,false);
                }
            }
        };


        list=new ArrayList<>();
        listreklam = new ArrayList<>();
        ListObject object2 = new ListObject();
        object2.setType("load");
        list.add(object2);
        linearLayoutManager=new LinearLayoutManager(SavedPostsActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new SafPostAdapter();
        adapter.setContext(list, Glide.with(SavedPostsActivity.this),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        onRefreshListener = () -> {
            if(!loading){
                loading=true;

                date=null;

                getReqs(null,true);

            }
        };
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);



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
            ParseCloud.callFunctionInBackground("getSavedPosts", params, (FunctionCallback<HashMap>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(SavedPostsActivity.this)){
                    if(e==null){

                        if(objects!= null){
                            if(isRefresh){
                                listreklam.clear();
                                listreklam = null;
                                listreklam=new ArrayList<>();
                            }
                            getAds((List<Post>)objects.get("posts")
                                    ,(boolean)objects.get("hasmore")
                                    ,(Date)objects.get("date")
                                    ,isRefresh);
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

    private void initList(List<Post> objects,boolean hasmore,Date date,List<UnifiedNativeAd> listreklam) {
        Log.e("done","InitList");

        if(GenelUtil.isAlive(this)){
            Log.e("done","InitListActive");
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
                    }
                    else{
                        if(list.get(list.size()-1).getType().equals("load")){
                            int in = list.size()-1;
                            list.remove(in);
                            adapter.notifyItemRemoved(in);
                        }

                    }
                    if(list.size()==0){
                        ListObject post = new ListObject();
                        post.setType("boş");
                        list.add(post);
                        adapter.notifyItemInserted(0);
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
    private void getAds(List<Post> objects,boolean hasmore,Date date,boolean isRefresh){
        Log.e("done","doneGetAds");

        if(GenelUtil.isAlive(SavedPostsActivity.this)){
            GenelUtil.loadAds(objects.size(),SavedPostsActivity.this, new com.sonata.socialapp.utils.interfaces.AdListener() {
                @Override
                public void done(List<UnifiedNativeAd> list) {
                    if(GenelUtil.isAlive(SavedPostsActivity.this)){
                        listreklam.addAll(list);
                        if(isRefresh){
                            //refreshSetting();
                            SavedPostsActivity.this.list.clear();
                            adapter.notifyDataSetChanged();

                        }
                        initList(objects,hasmore,date,list);
                    }
                    else{
                        for(int i = 0; i < list.size(); i++){
                            list.get(i).destroy();
                        }
                    }
                }
            });

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
    public void onLikeClick(int position, ImageView likeImage, TextView likeNumber) {
        Post post = list.get(position).getPost();
        if(!post.getLiked()){
            post.setLiked(true);

            likeImage.setImageDrawable(SavedPostsActivity.this.getDrawable(R.drawable.ic_like_red));
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("postID", post.getObjectId());
            ParseCloud.callFunctionInBackground("likePost", params);


            post.increment("likenumber");
            likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),SavedPostsActivity.this));


        }
        else{
            post.setLiked(false);
            likeImage.setImageDrawable(SavedPostsActivity.this.getDrawable(R.drawable.ic_like));
            if(post.getLikenumber()>0){

                post.increment("likenumber",-1);
                likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),SavedPostsActivity.this));                                                    }
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
    public void onOptionsClick(int position, TextView commentNumber) {
        GenelUtil.handlePostOptionsClick(this,position,list,adapter,commentNumber);
    }

    @Override
    public void onSocialClick(int position, int clickType, String text) {
        GenelUtil.handleLinkClicks(this,text,clickType);
    }

    @Override
    public void onGoToProfileClick(int position) {
        Post post = list.get(position).getPost();
        if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            startActivity(new Intent(SavedPostsActivity.this, GuestProfileActivity.class).putExtra("user",post.getUser()));
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


}

