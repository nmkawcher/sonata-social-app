package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
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
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.SafPostAdapter;
import com.sonata.socialapp.utils.adapters.SearchUserAdapter;
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
                int llm = linearLayoutManager.findLastVisibleItemPosition();
                for(int a = llm; a < Math.min(llm+5,list.size()); a++){
                    try{
                        Post post = list.get(a).getPost();

                        if(post != null ){
                            SonataUser user = post.getUser();
                            if(user != null){
                                String url = user.getPPAdapter();
                                Glide.with(SearchActivity.this).load(url).preload();
                            }
                            if(post.getType().equals("video")){
                                HashMap<String,Object> mediaObject = post.getMediaList().get(0);
                                ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                String url = parseFile.getUrl();
                                DownloadManager.getInstance(SearchActivity.this).enqueue(new DownloadManager.Request(MediaLoader.getInstance(SearchActivity.this).getProxyUrl(url)));
                                ParseFile thumb = (ParseFile) mediaObject.get("thumbnail");
                                String thumburl = thumb.getUrl();
                                Glide.with(SearchActivity.this).load(thumburl).preload();
                            }
                            else{
                                for (int im = 0; im < post.getImageCount(); im++){
                                    HashMap<String,Object> mediaObject = post.getMediaList().get(im);
                                    ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                    String url = parseFile.getUrl();
                                    Glide.with(SearchActivity.this).load(url).preload();
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
            ParseCloud.callFunctionInBackground("searchPost", params, (FunctionCallback<HashMap>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(SearchActivity.this)){
                    if(e==null){
                        if(searchStr.equals(searchText)){
                            if(objects!= null){

                                getAds((List<Post>) objects.get("posts")
                                        ,(boolean) objects.get("hasmore")
                                        ,(Date) objects.get("date")
                                        ,searchStr);
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
            ParseCloud.callFunctionInBackground("search", params, (FunctionCallback<List<HashMap>>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(SearchActivity.this)){
                    if(e==null){
                        if(searchText.equals(searchStr)){
                            if(objects!= null){
                                List<SonataUser> userList = (List<SonataUser>) objects.get(0).get("users");
                                List<Post> postList = (List<Post>) objects.get(1).get("posts");
                                boolean hasmore = (boolean)objects.get(1).get("hasmore");
                                Date date = (Date)objects.get(1).get("date");
                                initUser(userList);

                                getAds(postList,hasmore,date,searchStr);
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


    private void initList(List<Post> objects,boolean hasmore,Date date,List<UnifiedNativeAd> listreklam,boolean isrefresh) {
        Log.e("done","InitList");

        if(GenelUtil.isAlive(this)){
            Log.e("done","InitListActive");
            postson =!hasmore;
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
                if(hasmore){

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
    private void getAds(List<Post> objects,boolean hasmore,Date date,String searchString){
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

                initList(objects,hasmore,date,new ArrayList<>(),false);
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
                                        initList(objects,hasmore,date,tempList,false);
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
                                            initList(objects,hasmore,date,tempList,false);
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
                                initList(objects,hasmore,date,new ArrayList<>(),false);
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
    public void onImageClick(int position,ImageView imageView,int pos) {
        Post post = list.get(position).getPost();
        List<String> ulist = new ArrayList<>();

        for(int i = 0; i < post.getImageCount(); i++){
            ulist.add(String.valueOf(i));
        }
        GenelUtil.showImage(ulist,post.getMediaList(),imageView,pos,adapter);
    }


}
