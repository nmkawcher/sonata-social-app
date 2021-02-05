package com.sonata.socialapp.activities.sonata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.socialview.Mention;
import com.sonata.socialapp.socialview.SocialAutoCompleteTextView;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.adapters.ComReplyAdapter;
import com.sonata.socialapp.utils.adapters.HomeAdapter;
import com.sonata.socialapp.utils.adapters.MentionAdapter;
import com.sonata.socialapp.utils.classes.Comment;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.CommentReplyAdapterClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class CommentReplyActivity extends AppCompatActivity implements CommentReplyAdapterClick {

    List<Comment> list;

    Comment parentComment, childComment;


    List<String> saveList;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    ComReplyAdapter adapter;
    Post post;
    private boolean loading=true;
    Date date;
    ImageButton sendbutton;
    SocialAutoCompleteTextView commenttext;
    private long lastClick=0;
    RecyclerView.OnScrollListener onScrollListener;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    RelativeLayout back;



    ProgressDialog alertDialog;


    Uri uri;

    private final int imagerequestcode = 9514;

    RelativeLayout makecommentlayout;

    RoundKornerRelativeLayout errorlayout;

    private ArrayAdapter<Mention> defaultMentionAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_reply);
        if(getIntent().getExtras()==null){
            finish();
            return;
        }


        errorlayout=findViewById(R.id.errorlayout);
        makecommentlayout = findViewById(R.id.makecommentlayout);
        post = getIntent().getParcelableExtra("post");
        parentComment = getIntent().getParcelableExtra("parentcomment");
        alertDialog = new ProgressDialog(this);
        alertDialog.setCancelable(false);

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sendbutton = findViewById(R.id.sendbutton);
        commenttext = findViewById(R.id.commentedittext);
        String text = String.format(getResources().getString(R.string.comment_hint), "@"+ParseUser.getCurrentUser().getUsername());
        commenttext.setHint(text);
        commenttext.setMentionPattern(Pattern.compile("(^|[^\\w])@([\\w\\_\\.]+)"));
        commenttext.setHashtagColor(getResources().getColor(R.color.blue));
        commenttext.setMentionColor(getResources().getColor(R.color.blue));
        commenttext.setHyperlinkColor(getResources().getColor(R.color.blue));

        defaultMentionAdapter = new MentionAdapter(CommentReplyActivity.this);
        commenttext.setMentionAdapter(defaultMentionAdapter);

        commenttext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(count > before){
                    if(s.charAt(start) == ' ' || s.charAt(start) == '\n') return;

                    final String text = s.toString();

                    if(start > 0){


                        int spacePosition = -1;
                        int cursorPosition = start + count -1;
                        if(cursorPosition < 0) return;
                        while (true) {
                            if(cursorPosition < 0) {
                                break;
                            }
                            else {
                                if(s.charAt(cursorPosition) == ' ' || s.charAt(cursorPosition) == '\n'){
                                    spacePosition = cursorPosition + 1;
                                    break;
                                }
                                else {
                                    cursorPosition -=1;
                                }
                            }

                        }
                        if(spacePosition < 0) return;
                        if((start + count) - spacePosition < 1) return;
                        final String queryString = text.substring(spacePosition,start + count);
                        Log.e("Query String",queryString);
                        final String t = s.toString();
                        if(queryString.startsWith("@")&&queryString.replace("@","").length()>0){
                            //Do Query


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms
                                    if(GenelUtil.isAlive(CommentReplyActivity.this)){
                                        if(t.equals(commenttext.getText().toString())){
                                            HashMap<String, Object> params = new HashMap<>();
                                            params.put("text",queryString.replace("@","").toLowerCase());
                                            ParseCloud.callFunctionInBackground("searchPerson", params, (FunctionCallback<List<SonataUser>>) (object, e) -> {
                                                Log.e("done","done");
                                                if(GenelUtil.isAlive(CommentReplyActivity.this)){
                                                    if(e==null){
                                                        if(t.equals(commenttext.getText().toString())){
                                                            if(object!= null){
                                                                defaultMentionAdapter.clear();
                                                                for(int i = 0; i < object.size(); i++){
                                                                    if(object.get(i).getHasPp()){
                                                                        defaultMentionAdapter.add(new Mention(object.get(i).getUsername()
                                                                                ,object.get(i).getName(),object.get(i).getPPAdapter()));
                                                                    }
                                                                    else{
                                                                        defaultMentionAdapter.add(new Mention(object.get(i).getUsername()
                                                                                ,object.get(i).getName(),"empty"));
                                                                    }

                                                                }
                                                                defaultMentionAdapter.notifyDataSetChanged();

                                                                //initList(objects);
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }

                                }
                            }, 1000);
                        }


                    }


                }
                else{
                    defaultMentionAdapter.clear();
                    defaultMentionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().contains(" ")){
                    if(s.toString().startsWith("@")){
                        final String t = s.toString();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                if(GenelUtil.isAlive(CommentReplyActivity.this)){
                                    if(t.equals(commenttext.getText().toString())){
                                        HashMap<String, Object> params = new HashMap<>();
                                        params.put("text",t.replace("@","").toLowerCase());
                                        ParseCloud.callFunctionInBackground("searchPerson", params, (FunctionCallback<List<SonataUser>>) (object, e) -> {
                                            Log.e("done","done");
                                            if(GenelUtil.isAlive(CommentReplyActivity.this)){
                                                if(e==null){
                                                    if(t.equals(commenttext.getText().toString())){
                                                        if(object!= null){
                                                            defaultMentionAdapter.clear();
                                                            for(int i = 0; i < object.size(); i++){
                                                                if(object.get(i).getHasPp()){
                                                                    defaultMentionAdapter.add(new Mention(object.get(i).getUsername()
                                                                            ,object.get(i).getName(),object.get(i).getPPAdapter()));
                                                                }
                                                                else{
                                                                    defaultMentionAdapter.add(new Mention(object.get(i).getUsername()
                                                                            ,object.get(i).getName(),"empty"));
                                                                }

                                                            }
                                                            defaultMentionAdapter.notifyDataSetChanged();

                                                            //initList(objects);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }

                            }
                        }, 1000);
                    } else {
                        defaultMentionAdapter.clear();
                        defaultMentionAdapter.notifyDataSetChanged();
                    }
                } else {
                    defaultMentionAdapter.clear();
                    defaultMentionAdapter.notifyDataSetChanged();
                }

            }
        });
        if(Objects.requireNonNull(getIntent()).getBooleanExtra("reply",false)){
            commenttext.requestFocus();
            GenelUtil.showKeyboard(this);
        }


        recyclerView = findViewById(R.id.commentrecyclerview);
        swipeRefreshLayout = findViewById(R.id.homeFragmentSwipeRefreshLayout);






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
                    getComments(date,false);
                }
            }
        };

        list=new ArrayList<>();
        saveList= new ArrayList<>();

        linearLayoutManager=new LinearLayoutManager(CommentReplyActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new ComReplyAdapter();
        adapter.setContext(list,
                Glide.with(CommentReplyActivity.this),CommentReplyActivity.this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!loading){
                    loading=true;

                    getComments(null,true);
                    refreshComment(parentComment.getObjectId());


                }
            }
        };




         childComment = getIntent().getParcelableExtra("childcomment");





        if(!CommentReplyActivity.this.isFinishing()||!CommentReplyActivity.this.isDestroyed()){
            list.add(parentComment);
            if(childComment!=null){
                list.add(childComment);
                reply(childComment.getUser().getUsername());
            }
            Comment load = new Comment();
            load.setType("load");
            list.add(load);

            adapter.notifyDataSetChanged();
            recyclerView.addOnScrollListener(onScrollListener);

            swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
            getComments(null,false);
            //refreshComment(parentComment.getObjectId());
            setSendButtonClickListener();



            // getPostAndLoad(getIntent().getStringExtra("post"),getIntent().getStringExtra("parentcomment"));
        }



    }

    private void refreshComment(String id){
        HashMap<String, Object> params = new HashMap<String, Object>();

        params.put("id",id);
        ParseCloud.callFunctionInBackground("updateComment", params, new FunctionCallback<Comment>() {
            @Override
            public void done(Comment  comment, ParseException e) {
                Log.e("done","done");
                if(!CommentReplyActivity.this.isFinishing()||!CommentReplyActivity.this.isDestroyed()){
                    if(e==null){

                        comment.setVote(comment.getVote2());
                        comment.setReplyCount(comment.getReplyCount2());
                        comment.setUpvote(comment.getUpvote2());
                        comment.setDownvote(comment.getDownvote2());
                        comment.setSaved(comment.getSaved2());




                    }
                    else{
                        Log.e("error code",""+e.getCode());
                        if(e.getCode()==ParseException.CONNECTION_FAILED){
                            refreshComment(id);
                        }

                    }
                }
            }
        });
    }


    private void refreshSetting(){
        list.clear();
        list.add(parentComment);

    }


    private void setSendButtonClickListener(){
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - lastClick < 1000){
                    return;
                }
                lastClick = SystemClock.elapsedRealtime();
                if(post!=null){
                    if(commenttext.getText().toString().trim().length()>0){
                        alertDialog.show();
                        alertDialog.setMessage(getString(R.string.commenting));

                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("text", commenttext.getText().toString().trim());
                        params.put("post",post.getObjectId());
                        params.put("reply",parentComment.getObjectId());
                        ParseCloud.callFunctionInBackground("commentText", params, new FunctionCallback<HashMap>() {
                            @Override
                            public void done(HashMap postID, ParseException e) {
                                if(!CommentReplyActivity.this.isFinishing()||!CommentReplyActivity.this.isDestroyed()){
                                    alertDialog.dismiss();
                                    if(e==null){
                                        if(list.get(list.size()-1).getString("type").equals("load")||list.get(list.size()-1).getString("type").equals("boş")){
                                            list.remove(list.size()-1);
                                        }
                                        Comment com = (Comment) postID.get("comment");
                                        list.add(com);
                                        parentComment.increment("replycount");
                                        post.increment("commentnumber");
                                        adapter.notifyDataSetChanged();
                                        recyclerView.scrollToPosition(list.size()-1);
                                        alertDialog.dismiss();
                                        GenelUtil.hideKeyboard(CommentReplyActivity.this);
                                        commenttext.setText("");
                                        commenttext.clearFocus();


                                    }
                                    else{
                                        Log.e("error message", Objects.requireNonNull(e.getMessage()));
                                        if(e.getCode()==ParseException.INVALID_SESSION_TOKEN){
                                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                            if (notificationManager != null) {
                                                notificationManager.cancelAll();
                                            }
                                            ParseUser.logOut();
                                            Intent intent = new Intent(CommentReplyActivity.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            if(e.getMessage().equals("CommentsDisabled")){
                                                GenelUtil.ToastLong(getApplicationContext(),getString(R.string.commentdisable));
                                            }
                                            else{
                                                GenelUtil.ToastLong(getApplicationContext(),getString(R.string.error));
                                            }
                                        }
                                    }
                                }
                            }
                        });


                    }

                }

            }
        });

    }




    public void reply(String username){
        if(commenttext.getText().toString().length()>0){
            commenttext.setText(commenttext.getText().toString()+" @"+username+" ");
        }
        else{
            commenttext.setText("@"+username+" ");

        }

        commenttext.append("");
        commenttext.setSelection(commenttext.getText().length());
        commenttext.requestFocus();
        GenelUtil.showKeyboard(this);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }













    private void getComments(Date date1,boolean isRefresh){
        if(!CommentReplyActivity.this.isFinishing()||!CommentReplyActivity.this.isDestroyed()){
            HashMap<String, Object> params = new HashMap<String, Object>();
            if(date1!=null){
                params.put("date",date1);
            }
            params.put("reply",parentComment.getObjectId());
            params.put("post", post.getObjectId());
            ParseCloud.callFunctionInBackground("getComments", params, new FunctionCallback<HashMap>() {
                @Override
                public void done(HashMap  objects, ParseException e) {
                    Log.e("done","done");
                    if(!CommentReplyActivity.this.isFinishing()||!CommentReplyActivity.this.isDestroyed()){
                        if(e==null){



                            if(objects!= null){
                                if(isRefresh){
                                    refreshSetting();
                                }

                                initList((List<Comment>)objects.get("comments")
                                        ,(boolean)objects.get("hasmore")
                                        ,(Date)objects.get("date"));
                            }




                        }
                        else{
                            Log.e("error code",""+e.getCode());
                            Log.e("error message",e.getMessage());
                            if(e.getCode()==ParseException.CONNECTION_FAILED){
                                getComments(date1,isRefresh);
                            }

                        }
                    }
                }
            });

        }
    }



    private void initList(List<Comment> objects,boolean hasmore,Date date){
        if(GenelUtil.isAlive(this)){
            postson = !hasmore;
            this.date = date;
            if(objects.size()==0){
                swipeRefreshLayout.setRefreshing(false);
                if(list.get(list.size()-1).getString("type").equals("load")){
                    list.remove(list.size()-1);
                }
                if(list.size()==1){
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
                if(list.get(list.size()-1).getString("type").equals("load")){
                    list.remove(list.size()-1);
                }
                if(objects.size()<20){
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
    public void onReloadImageClick(int position, RoundKornerRelativeLayout reloadLayout, ProgressBar progressBar, ImageView imageView) {
        Comment post = (Comment) list.get(position);

        reloadLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        if(post.getNsfw()){
            Glide.with(CommentReplyActivity.this).load(post.getMainMedia().getUrl()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).addListener(new RequestListener<Drawable>() {
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
                Glide.with(CommentReplyActivity.this).load(post.getMainMedia().getUrl()).override(iw,ih).thumbnail(Glide.with(CommentReplyActivity.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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
                Glide.with(CommentReplyActivity.this).load(post.getMainMedia().getUrl()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).thumbnail(Glide.with(CommentReplyActivity.this).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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

    @Override
    public void onImageClick(int position, ImageView imageView,int pos) {
        Comment post = (Comment) list.get(position);
        List<String> ulist = new ArrayList<>();


        ulist.add(String.valueOf(0));

        GenelUtil.showImage(ulist,post.getMediaList(),imageView,pos,adapter);
    }

    @Override
    public void onGoToProfileClick(int position) {
        Comment post =  list.get(position);
        if(!post.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
            startActivity(new Intent(CommentReplyActivity.this, GuestProfileActivity.class).putExtra("user",post.getUser()));
        }
    }

    @Override
    public void onCommentOptionsClick(int position) {
        Comment post = (Comment)list.get(position);
        ArrayList<String> other = new ArrayList<>();
        String id = ParseUser.getCurrentUser().getObjectId();
        if(post.getUser().getObjectId().equals(id)||this.post.getUser().getObjectId().equals(id)){
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

        AlertDialog.Builder builder = new AlertDialog.Builder(CommentReplyActivity.this);
        builder.setCancelable(true);

        builder.setItems(popupMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String select = popupMenu[which];
                if(select.equals(getString(R.string.savecomment))){
                    //savePost
                    ProgressDialog progressDialog = new ProgressDialog(CommentReplyActivity.this);
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
                                GenelUtil.ToastLong(CommentReplyActivity.this,getString(R.string.commentsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(CommentReplyActivity.this,getString(R.string.error));
                            }
                        }
                    });

                }
                if(select.equals(getString(R.string.unsavecomment))){
                    //UnsavePost
                    ProgressDialog progressDialog = new ProgressDialog(CommentReplyActivity.this);
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
                                GenelUtil.ToastLong(CommentReplyActivity.this,getString(R.string.commentunsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(CommentReplyActivity.this,getString(R.string.error));
                            }
                        }
                    });
                }
                if(select.equals(getString(R.string.report))){
                    ProgressDialog progressDialog = new ProgressDialog(CommentReplyActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.report));
                    progressDialog.show();
                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("postID", post.getObjectId());
                    ParseCloud.callFunctionInBackground("reportComment", params, new FunctionCallback<String>() {
                        @Override
                        public void done(String object, ParseException e) {
                            if(e==null){
                                GenelUtil.ToastLong(CommentReplyActivity.this,getString(R.string.reportsucces));
                                progressDialog.dismiss();
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(CommentReplyActivity.this,getString(R.string.error));
                            }
                        }
                    });

                }

                if(select.equals(getString(R.string.delete))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(CommentReplyActivity.this);
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
                            ProgressDialog progressDialog = new ProgressDialog(CommentReplyActivity.this);
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
                                        GenelUtil.ToastLong(CommentReplyActivity.this,getString(R.string.error));
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
                votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),CommentReplyActivity.this));


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
                    votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),CommentReplyActivity.this));


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
                    votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),CommentReplyActivity.this));


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
                votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),CommentReplyActivity.this));



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
                    votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),CommentReplyActivity.this));


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
                    votecount.setText(GenelUtil.ConvertNumber((int)post.getVote(),CommentReplyActivity.this));


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
        Comment post = list.get(position);
        reply(post.getUser().getUsername());
    }

    @Override
    public void onCommentSocialClick(int position, int clickType, String text) {
        if(clickType== HomeAdapter.TYPE_HASHTAG){
            //hashtag
            startActivity(new Intent(CommentReplyActivity.this, HashtagActivity.class).putExtra("hashtag",text.replace("#","")));

        }
        else if(clickType==HomeAdapter.TYPE_MENTION){
            //mention
            String username = text;

            username = username.replace("@","").trim();


            if(!username.equals(ParseUser.getCurrentUser().getUsername())){
                startActivity(new Intent(CommentReplyActivity.this, GuestProfileActivity.class).putExtra("username",username));
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
            customTabsIntent.launchUrl(CommentReplyActivity.this, Uri.parse(url));
        }
    }

}

