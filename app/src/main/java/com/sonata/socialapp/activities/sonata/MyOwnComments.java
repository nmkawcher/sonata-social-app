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
import com.bumptech.glide.request.target.Target;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.adapters.HomeAdapter;
import com.sonata.socialapp.utils.adapters.SafCommentAdapter;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.interfaces.CommentReplyAdapterClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyOwnComments extends AppCompatActivity implements CommentReplyAdapterClick {

    List<Comment> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    SafCommentAdapter adapter;
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
        setContentView(R.layout.activity_my_own_comments);

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
        Comment object2 = new Comment();
        object2.setType("load");
        list.add(object2);
        linearLayoutManager=new LinearLayoutManager(MyOwnComments.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new SafCommentAdapter();
        adapter.setContext(list, Glide.with(MyOwnComments.this),this);
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

        if(GenelUtil.isAlive(this)){
            getReqs(null,false);
        }

    }


    private void refreshSetting(){
        list.clear();
    }


    private void getReqs(Date date,boolean isRefresh){
        if(GenelUtil.isAlive(this)){
            HashMap<String, Object> params = new HashMap<String, Object>();
            if(date!=null){
                params.put("date",date);
            }
            ParseCloud.callFunctionInBackground("getOwnComments", params, new FunctionCallback<List<Comment>>() {
                @Override
                public void done(List<Comment>  objects, ParseException e) {
                    Log.e("done","done");
                    if(GenelUtil.isAlive(MyOwnComments.this)){
                        if(e==null){

                            if(objects!= null){
                                if(isRefresh){
                                    refreshSetting();
                                }
                                initList(objects);
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

    private void initList(List<Comment> objects){
        if(GenelUtil.isAlive(this)){
            if(objects.size()==0){
                postson =true;
                swipeRefreshLayout.setRefreshing(false);
                if(list.size()>0){
                    if(list.get(list.size()-1).getString("type").equals("load")){
                        list.remove(list.size()-1);
                    }
                    if(list.size()==0){
                        Comment load = new Comment();
                        load.setType("boş");
                        list.add(load);
                    }
                }
                if(list.size()==0){
                    Comment load = new Comment();
                    load.setType("boş");
                    list.add(load);
                }


                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(list.size()-1);
                loading =false;
                swipeRefreshLayout.setRefreshing(false);
            }
            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getString("type").equals("load")){
                        list.remove(list.size()-1);
                    }
                }
                if(objects.size()<10){
                    date = objects.get(objects.size()-1).getCreatedAt();
                    postson =true;
                    loading =false;
                    for(int i=0;i<objects.size();i++){
                        if(!list.contains(objects.get(i))){
                            Comment comment = objects.get(i);
                            comment.setVote(comment.getVote2());
                            comment.setReplyCount(comment.getReplyCount2());
                            comment.setUpvote(comment.getUpvote2());
                            comment.setDownvote(comment.getDownvote2());
                            comment.setSaved(comment.getSaved2());

                            list.add(comment);
                        }





                    }

                    adapter.notifyDataSetChanged();



                    loading =false;
                    swipeRefreshLayout.setRefreshing(false);


                }
                else{
                    date = objects.get(objects.size()-1).getCreatedAt();
                    for(int i=0;i<objects.size();i++){
                        if(!list.contains(objects.get(i))){
                            Comment comment = objects.get(i);
                            comment.setVote(comment.getVote2());
                            comment.setReplyCount(comment.getReplyCount2());
                            comment.setUpvote(comment.getUpvote2());
                            comment.setDownvote(comment.getDownvote2());
                            comment.setSaved(comment.getSaved2());

                            list.add(comment);
                        }



                    }



                    Comment load = new Comment();
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
    public void onReloadImageClick(int position, RoundKornerRelativeLayout reloadLayout, ProgressBar progressBar, ImageView imageView) {
        Comment post = (Comment) list.get(position);

        reloadLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        if(post.getRatioH()>1000||post.getRatioW()>1000){
            int ih = 1000;
            int iw = 1000;
            if(post.getRatioH()>post.getRatioW()){
                ih = 1000;
                iw = 1000 * (post.getRatioW()/post.getRatioH());
            }
            else{
                iw = 1000;
                ih = 1000 * (post.getRatioH()/post.getRatioW());
            }
            Glide.with(MyOwnComments.this).load(post.getMainMedia().getUrl()).override(iw,ih).thumbnail(Glide.with(MyOwnComments.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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
            Glide.with(MyOwnComments.this).load(post.getMainMedia().getUrl()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).thumbnail(Glide.with(MyOwnComments.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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

    @Override
    public void onImageClick(int position, ImageView imageView,int pos) {
        Comment post = (Comment)list.get(position);
        ArrayList<String> ulist = new ArrayList<>();
        ArrayList<String> uList2 = new ArrayList<>();

        ulist.add(post.getMainMedia().getUrl());
        uList2.add(post.getThumbMedia().getUrl());

        GenelUtil.showImage(ulist,uList2,imageView,pos,adapter);
    }

    @Override
    public void onGoToProfileClick(int position) {
        Comment post =  list.get(position);
        if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            startActivity(new Intent(MyOwnComments.this, GuestProfileActivity.class).putExtra("user",post.getUser()));
        }
    }

    @Override
    public void onCommentOptionsClick(int position) {
        Comment post = (Comment)list.get(position);
        ArrayList<String> other = new ArrayList<>();
        if(post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            //Bu gönderi benim
            other.add(getString(R.string.delete));

        }
        if(post.getSaved()){
            other.add(getString(R.string.unsavecomment));
        }
        if(!post.getSaved()){
            other.add(getString(R.string.savecomment));
        }
        if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            //Bu gönderi başkasına ait
            other.add(getString(R.string.report));
        }

        String[] popupMenu = other.toArray(new String[other.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(MyOwnComments.this);
        builder.setCancelable(true);

        builder.setItems(popupMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String select = popupMenu[which];
                if(select.equals(getString(R.string.savecomment))){
                    //savePost
                    ProgressDialog progressDialog = new ProgressDialog(MyOwnComments.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.savecomment));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("saveComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setSaved(true);
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(MyOwnComments.this,getString(R.string.commentsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(MyOwnComments.this,getString(R.string.error));
                            }
                        }
                    });

                }
                if(select.equals(getString(R.string.unsavecomment))){
                    //UnsavePost
                    ProgressDialog progressDialog = new ProgressDialog(MyOwnComments.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.unsavecomment));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("unsaveComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                post.setSaved(false);
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(MyOwnComments.this,getString(R.string.commentunsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(MyOwnComments.this,getString(R.string.error));
                            }
                        }
                    });
                }
                if(select.equals(getString(R.string.report))){
                    ProgressDialog progressDialog = new ProgressDialog(MyOwnComments.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.report));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("reportComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                GenelUtil.ToastLong(MyOwnComments.this,getString(R.string.reportsucces));
                                progressDialog.dismiss();
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(MyOwnComments.this,getString(R.string.error));
                            }
                        }
                    });

                }

                if(select.equals(getString(R.string.delete))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(MyOwnComments.this);
                    builder.setTitle(R.string.deletetitlecomment);
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
                            ProgressDialog progressDialog = new ProgressDialog(MyOwnComments.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage(getString(R.string.delete));
                            progressDialog.show();
                            HashMap<String, Object> params = new HashMap<String, Object>();
                            params.put("postID", post.getObjectId());
                            ParseCloud.callFunctionInBackground("deleteComment", params, new FunctionCallback<String>() {
                                @Override
                                public void done(String object, ParseException e) {
                                    if(e==null&&object.equals("deleted")){
                                        list.remove(position);
                                        adapter.notifyDataSetChanged();
                                        progressDialog.dismiss();
                                    }
                                    else{
                                        progressDialog.dismiss();
                                        GenelUtil.ToastLong(MyOwnComments.this,getString(R.string.error));
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
    public void onCommentUpvoteClick(int position, ImageView upvoteimage, ImageView downvoteimage, TextView votecount) {
        Comment post = (Comment) list.get(position);


        if(post.getObjectId()!=null){
            if(post.getDownvote()){
                post.setDownvote(false);
                post.setUpvote(true);
                upvoteimage.setImageDrawable( getDrawable(R.drawable.ic_upvote_blue));
                votecount.setTextColor(Color.parseColor("#2d72bc"));
                post.increment("vote",2);
                votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),MyOwnComments.this));


                //upvote
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("postID", post.getObjectId());
                ParseCloud.callFunctionInBackground("upvoteComment", params);

            }
            else{
                if(post.getUpvote()){
                    post.setUpvote(false);
                    upvoteimage.setImageDrawable(getDrawable(R.drawable.ic_upvote));
                    votecount.setTextColor(Color.parseColor("#999999"));
                    post.increment("vote",-1);
                    votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),MyOwnComments.this));


                    //unupvote
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("unvoteComment", params);
                }
                else{
                    post.setUpvote(true);
                    upvoteimage.setImageDrawable(getDrawable(R.drawable.ic_upvote_blue));
                    votecount.setTextColor(Color.parseColor("#2d72bc"));
                    post.increment("vote");
                    votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),MyOwnComments.this));


                    //upvote
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("upvoteComment", params);

                }
            }
        }
    }

    @Override
    public void onCommentDownvoteClick(int position, ImageView upvoteimage, ImageView downvoteimage, TextView votecount) {
        Comment post = (Comment) list.get(position);
        if(post.getObjectId()!=null){
            if(post.getUpvote()){
                post.setUpvote(false);
                upvoteimage.setImageDrawable(getDrawable(R.drawable.ic_upvote));
                post.setDownvote(true);
                downvoteimage.setImageDrawable(getDrawable(R.drawable.ic_downvote_red));
                votecount.setTextColor(Color.parseColor("#a64942"));

                post.increment("vote",-2);
                votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),MyOwnComments.this));



                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("postID", post.getObjectId());
                ParseCloud.callFunctionInBackground("downvoteComment", params);

            }
            else{
                if(post.getDownvote()){
                    post.setDownvote(false);
                    downvoteimage.setImageDrawable(getDrawable(R.drawable.ic_downvote));
                    votecount.setTextColor(Color.parseColor("#999999"));
                    post.increment("vote");
                    votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),MyOwnComments.this));


                    //undownvote
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("unvoteComment", params);
                }
                else{
                    post.setDownvote(true);
                    downvoteimage.setImageDrawable(getDrawable(R.drawable.ic_downvote_red));
                    votecount.setTextColor(Color.parseColor("#a64942"));
                    post.increment("vote",-1);
                    votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),MyOwnComments.this));


                    //downvote
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("downvoteComment", params);
                }
            }
        }
    }





    @Override
    public void onCommentReply(int position) {
        startActivity(new Intent(this
                , CommentActivity.class).putExtra("commentid",list.get(position).getObjectId()));
    }

    @Override
    public void onCommentSocialClick(int position, int clickType, String text) {
        if(clickType== HomeAdapter.TYPE_HASHTAG){
            //hashtag
            startActivity(new Intent(MyOwnComments.this, HashtagActivity.class).putExtra("hashtag",text.replace("#","")));

        }
        else if(clickType==HomeAdapter.TYPE_MENTION){
            //mention
            String username = text;

            username = username.replace("@","").trim();


            if(!username.equals(ParseUser.getCurrentUser().getUsername())){
                startActivity(new Intent(MyOwnComments.this, GuestProfileActivity.class).putExtra("username",username));
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
            customTabsIntent.launchUrl(MyOwnComments.this, Uri.parse(url));
        }
    }

}
