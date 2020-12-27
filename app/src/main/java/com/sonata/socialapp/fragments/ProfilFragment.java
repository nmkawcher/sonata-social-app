package com.sonata.socialapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.EditProfileActivity;
import com.sonata.socialapp.activities.sonata.FollowersActivity;
import com.sonata.socialapp.activities.sonata.FollowingsActivity;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;
import com.sonata.socialapp.activities.sonata.HashtagActivity;
import com.sonata.socialapp.activities.sonata.LoginActivity;
import com.sonata.socialapp.activities.sonata.MainActivity;
import com.sonata.socialapp.activities.sonata.SettingsActivity;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.GridProfilAdapter;
import com.sonata.socialapp.utils.adapters.HomeAdapter;
import com.sonata.socialapp.utils.adapters.ProfilAdapter;
import com.sonata.socialapp.utils.classes.BottomSheetDialog;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.AccountManagerClicks;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.jzvd.Jzvd;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.parse.Parse.getApplicationContext;


public class ProfilFragment extends Fragment implements RecyclerViewClick, AccountManagerClicks {
    private Date date;

    private List<ListObject> list;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    private boolean postson=false;
    private boolean loading=true;
    private GridProfilAdapter adapter;
    private SonataUser user;
    private RecyclerView.OnScrollListener onScrollListener;

    private ImageView profilephoto,arrowdown;
    private RelativeLayout editprofile;
    private RecyclerView recyclerView;
    private TextView name,bio,followers,followerstext,followingtext,followings,username;


    private ProfilAdapter postAdapter;
    private RecyclerView.OnScrollListener postOnScrollListener;
    private RecyclerView postRecyclerView;

    private CoordinatorLayout mainlayout;
    private RelativeLayout listPostLayout,backButton;

    BottomSheetDialog dialog;





    @Override
    public void onDestroy() {
        for(int i = 0;i<list.size();i++){
            if(list.get(i).getAd()!=null){
                list.get(i).getAd().destroy();
            }
        }
        name=null;
        bio=null;
        adapter.setFinish(true);
        adapter.notifyDataSetChanged();
        recyclerView.removeOnScrollListener(onScrollListener);
        onScrollListener=null;
        adapter=null;
        followers=null;
        followings = null;
        username = null;
        swipeRefreshLayout.setOnRefreshListener(null);
        swipeRefreshLayout =null;
        onRefreshListener=null;
        editprofile.setOnClickListener(null);
        editprofile=null;
        profilephoto = null;

        recyclerView.removeOnScrollListener(onScrollListener);
        onScrollListener=null;
        recyclerView.setLayoutManager(null);
        recyclerView.setAdapter(null);
        list = null;
        date = null;
        recyclerView=null;
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        setProfile(user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);
        editprofile=view.findViewById(R.id.profileditprofilebutton);
        editprofile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), EditProfileActivity.class));
        });
        postRecyclerView = view.findViewById(R.id.profile_posts_recyclerview);
        mainlayout = view.findViewById(R.id.profile_main_layout);
        listPostLayout = view.findViewById(R.id.profile_post_layout);
        backButton = view.findViewById(R.id.backbutton1);

        onRefreshListener = this::refreshProfile;

        swipeRefreshLayout=view.findViewById(R.id.pSwipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);


        username = view.findViewById(R.id.profileusernametext);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new BottomSheetDialog(
                        GenelUtil.getSavedUsersFinal(getActivity())
                        ,getActivity()
                        ,ParseUser.getCurrentUser().getObjectId()
                        ,ProfilFragment.this);

                dialog.show(getActivity().getSupportFragmentManager(),"bottomSheet");

            }
        });
        arrowdown = view.findViewById(R.id.usernarrd);
        arrowdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new BottomSheetDialog(
                        GenelUtil.getSavedUsersFinal(getActivity())
                        ,getActivity()
                        ,ParseUser.getCurrentUser().getObjectId()
                        ,ProfilFragment.this);

                dialog.show(getActivity().getSupportFragmentManager(),"bottomSheet");
            }
        });
        profilephoto=view.findViewById(R.id.profilephotophoto);
        profilephoto.setOnClickListener(view1 -> {
            if(user.getHasPp()){
                if(GenelUtil.clickable(500)) {
                    ArrayList<String> ulist = new ArrayList<>();
                    ulist.add(user.getPPbig());

                    ArrayList<String> ulist2 = new ArrayList<>();
                    ulist2.add(user.getPPAdapter());

                    GenelUtil.showImage(ulist,ulist2
                            ,profilephoto,0,null);
                }

            }
        });
        name=view.findViewById(R.id.profilename);
        bio=view.findViewById(R.id.profilebio);
        followers=view.findViewById(R.id.followersnumbers);
        followings=view.findViewById(R.id.followingnumbers);
        followerstext = view.findViewById(R.id.followerstext);
        followingtext = view.findViewById(R.id.followingtext);


        user = (SonataUser) ParseUser.getCurrentUser();
        setProfile(user);
        HashMap<String,String> params = new HashMap<>();
        ParseCloud.callFunctionInBackground("refreshOwnProfile", params, new FunctionCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if(getActive()){
                    if(e==null){
                        setProfile((SonataUser)object);
                    }
                }

            }
        });
        RelativeLayout settings = view.findViewById(R.id.appoptionsripple);
        settings.setOnClickListener(v -> {
            if(GenelUtil.clickable(500)){
                ((MainActivity)Objects.requireNonNull(getActivity())).startSettingsActivity();
            }

        });








        list=new ArrayList<>();
        ListObject post = new ListObject();
        post.setType("load");
        list.add(post);
        adapter = new GridProfilAdapter();
        recyclerView = view.findViewById(R.id.profilerecyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        recyclerView.setAdapter(adapter);
        adapter.setContext(list,Glide.with(ProfilFragment.this),this,(SonataUser)ParseUser.getCurrentUser());
        ((GridLayoutManager)recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(list.get(position).getType().equals("load")||list.get(position).getType().equals("boş")){
                    return 3;
                }
                return 1;
            }
        });



        FloatingActionButton fab = view.findViewById(R.id.uploadbutton);
        fab.setOnClickListener(view12 -> {
            ((MainActivity) Objects.requireNonNull(getActivity())).startActivityResult();
            //startActivity(new Intent(getContext(), UploadActivity.class));
        });

        onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!ProfilFragment.this.isDetached()){
                    if(dy>0){
                        if(dy>5){
                            fab.hide();
                        }

                        if(((GridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition()>(list.size()-7)&&!loading&&!postson){
                            loading=true;
                            get(date,false);
                        }

                    }

                    if(dy<-5){
                        fab.show();

                    }
                }

            }
        };

        recyclerView.setOnScrollListener(onScrollListener);




        if(getActive()){
            get(null,false);
        }

        return view;
    }



    private void refreshProfile(){
        if(!loading){
            loading = true;
            postson=false;
            get(null,true);
            HashMap<String,String> params = new HashMap<>();
            ParseCloud.callFunctionInBackground("refreshOwnProfile", params, (FunctionCallback<ParseUser>) (object, e) -> {
                if(e==null){
                    user = (SonataUser) object;
                    user.revert("followreqcount");
                    setProfile(user);
                }

            });

        }

    }


    private void refreshSetting(){
        list.clear();


    }


    public void setProfile(SonataUser user){
        username.setText(user.getUsername() != null ? user.getUsername():"");
        if(getContext()!=null){
            if(user.getHasPp()){
                Glide.with(profilephoto.getContext())
                        .load(user.getPPbig())
                        .apply(RequestOptions.circleCropTransform())
                        .thumbnail(
                                Glide.with(profilephoto.getContext())
                                        .load(user.getPPAdapter()).apply(RequestOptions.circleCropTransform()))
                        .into(profilephoto);
            }
            else{
                Glide.with(profilephoto.getContext()).load(getResources().getDrawable(R.drawable.emptypp)).apply(RequestOptions.circleCropTransform())
                        .into(profilephoto);
            }

            followings.setText(GenelUtil.ConvertNumber((int)user.getFollowing(),followings.getContext()));
            followers.setText(GenelUtil.ConvertNumber((int)user.getFollower(),followers.getContext()));
            followingtext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), FollowingsActivity.class).putExtra("id",GenelUtil.getCurrentUser().getObjectId()));
                }
            });
            followings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), FollowingsActivity.class).putExtra("id",GenelUtil.getCurrentUser().getObjectId()));
                }
            });
            followerstext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), FollowersActivity.class).putExtra("id",GenelUtil.getCurrentUser().getObjectId()));
                }
            });
            followers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().startActivity(new Intent(getActivity(), FollowersActivity.class).putExtra("id",GenelUtil.getCurrentUser().getObjectId()));
                }
            });
        }
        name.setText(user.getName()!=null?user.getName():"");
        if(user.getBio()==null||user.getBio().length()==0){
            bio.setVisibility(View.GONE);
        }
        else{
            bio.setVisibility(View.VISIBLE);
            bio.setText(user.getBio());
        }


    }


    public void openComments(Post post){
        ((MainActivity) Objects.requireNonNull(getActivity())).profileFragmentComment(post);
    }


    private void initList(List<Post> objects) {
        if(getActive()){


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
                        if(list.size()==0){
                            ListObject post = new ListObject();
                            post.setType("boş");
                            list.add(post);
                        }

                    }
                }

                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                if(postAdapter!=null){
                    postAdapter.notifyDataSetChanged();
                }


            }
            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.get(list.size()-1));
                    }
                }

                date=objects.get(objects.size()-1).getCreatedAt();
                if(objects.size()<39){
                    postson =true;
                    for(int i=0;i<objects.size();i++){
                        if(objects.get(i).getType().equals("image")||objects.get(i).getType().equals("video")){
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

                    }

                    adapter.notifyDataSetChanged();
                    if(postAdapter!=null){
                        postAdapter.notifyDataSetChanged();
                    }
                    loading =false;
                    swipeRefreshLayout.setRefreshing(false);


                }
                else{
                    for(int i=0;i<objects.size();i++){
                        ListObject post = new ListObject();
                        if(objects.get(i).getType().equals("image")||objects.get(i).getType().equals("video")){
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

                    }



                    ListObject load = new ListObject();
                    load.setType("load");
                    list.add(load);

                    adapter.notifyDataSetChanged();
                    if(postAdapter!=null){
                        postAdapter.notifyDataSetChanged();
                    }
                    loading =false;
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }

    }






    private void get(Date date,boolean isRefresh){

        HashMap<String, Object> params = new HashMap<>();
        if(date!=null){
            params.put("date", date);
        }
        ParseCloud.callFunctionInBackground("getOwnPosts", params, (FunctionCallback<List<Post>>) (objects, e) -> {
            Log.e("done","done");
            if(getActive()){
                if(e==null){

                    if(objects!= null){
                        if(isRefresh){
                            list.clear();
                        }
                        initList(objects);
                    }


                }
                else{
                    Log.e("error code",""+e.getCode());
                    swipeRefreshLayout.setRefreshing(false);
                    get(date,isRefresh);
                }
            }
        });


    }








    public void backPressed(){
        if(listPostLayout.getVisibility()==View.VISIBLE){
            Jzvd.releaseAllVideos();
            mainlayout.setVisibility(View.VISIBLE);
            listPostLayout.setVisibility(View.INVISIBLE);
        }
        else{
            if(getActivity()!=null){
                ((MainActivity)Objects.requireNonNull(getActivity())).backPressProfile();
            }
        }

    }



    public void Refresh(){
        if(getActive()){
            if(listPostLayout.getVisibility()==View.VISIBLE){
                mainlayout.setVisibility(View.VISIBLE);
                listPostLayout.setVisibility(View.INVISIBLE);
            }
            else{
                if(swipeRefreshLayout!=null){
                    swipeRefreshLayout.setRefreshing(true);
                }
                refreshProfile();
            }

        }
    }
    public void notifyAdapter(){
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
        if(postAdapter!=null){
            postAdapter.notifyDataSetChanged();
        }
    }

    private boolean getActive(){
        return getActivity()!=null && GenelUtil.isAlive(getActivity());
    }

    @Override
    public void onOptionsClick(int position, TextView commentNumber) {
        Post post = list.get(position).getPost();
        ProgressDialog progressDialog = new ProgressDialog(getContext());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                commentNumber.setText(getContext().getString(R.string.disabledcomment));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(getContext(),getString(R.string.error));
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
                                GenelUtil.ToastLong(getContext(),getString(R.string.postsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(getContext(),getString(R.string.error));
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
                                GenelUtil.ToastLong(getContext(),getString(R.string.postunsaved));

                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(getContext(),getString(R.string.error));
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
                                GenelUtil.ToastLong(getContext(),getString(R.string.reportsucces));
                                progressDialog.dismiss();
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(getContext(),getString(R.string.error));
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

                                commentNumber.setText(GenelUtil.ConvertNumber((int)post.getCommentnumber(),getContext()));
                            }
                            else{
                                progressDialog.dismiss();
                                GenelUtil.ToastLong(getContext(),getString(R.string.error));
                            }
                        }
                    });

                }
                if(select.equals(getString(R.string.delete))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                        postAdapter.notifyItemRemoved(position);
                                        postAdapter.notifyItemRangeChanged(position,list.size());
                                        progressDialog.dismiss();

                                    }
                                    else{
                                        progressDialog.dismiss();
                                        GenelUtil.ToastLong(getContext(),getString(R.string.error));
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
            startActivity(new Intent(getContext(), HashtagActivity.class).putExtra("hashtag",text.replace("#","")));

        }
        else if(clickType==HomeAdapter.TYPE_MENTION){
            //mention
            String username = text;

            username = username.replace("@","").trim();


            if(!username.equals(ParseUser.getCurrentUser().getUsername())){
                startActivity(new Intent(getContext(), GuestProfileActivity.class).putExtra("username",username));
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
            customTabsIntent.launchUrl(getContext(), Uri.parse(url));
        }
    }

    @Override
    public void onLikeClick(int position, ImageView likeImage, TextView likeNumber) {
        Post post = list.get(position).getPost();
        if(!post.getLiked()){
            post.setLiked(true);

            likeImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_like_red));
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("postID", post.getObjectId());
            ParseCloud.callFunctionInBackground("likePost", params);


            post.increment("likenumber");
            likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),getContext()));


        }
        else{
            post.setLiked(false);
            likeImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_like));
            if(post.getLikenumber()>0){

                post.increment("likenumber",-1);
                likeNumber.setText(GenelUtil.ConvertNumber((int)post.getLikenumber(),getContext()));                                                    }
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
            startActivity(new Intent(getContext(), GuestProfileActivity.class).putExtra("user",post.getUser()));
        }
    }

    @Override
    public void onOpenComments(int position) {
        if(listPostLayout.getVisibility()==View.VISIBLE){
            Post post = list.get(position).getPost();
            if(post.getCommentable()){
                openComments(post);
            }
        }
        else{
            if(postAdapter == null){
                postAdapter = new ProfilAdapter();
                postAdapter.setContext(list,Glide.with(getActivity()),this,user);
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
                    }

                    @Override
                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if(((LinearLayoutManager)postRecyclerView.getLayoutManager()).findFirstVisibleItemPosition()>(list.size()-10)&&!loading&&!postson){
                            loading=true;
                            get(date,false);
                        }
                    }
                };


                postRecyclerView.setOnScrollListener(postOnScrollListener);
                postAdapter.notifyDataSetChanged();

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Jzvd.releaseAllVideos();
                        listPostLayout.setVisibility(View.INVISIBLE);
                        mainlayout.setVisibility(View.VISIBLE);
                    }
                });
                postRecyclerView.scrollToPosition(position);

                mainlayout.setVisibility(View.INVISIBLE);
                listPostLayout.setVisibility(View.VISIBLE);
                postRecyclerView.smoothScrollBy(0,1);

            }
            else{

                postRecyclerView.scrollToPosition(position);
                mainlayout.setVisibility(View.INVISIBLE);
                listPostLayout.setVisibility(View.VISIBLE);
                postRecyclerView.smoothScrollBy(0,1);
            }
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
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));
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
        GenelUtil.showImage(ulist,uList2,imageView,pos,postAdapter);
    }

    @Override
    public void onReloadImageClick(int position, RoundKornerRelativeLayout reloadLayout, ProgressBar progressBar, ImageView imageView) {
        Post post = list.get(position).getPost();

        reloadLayout.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if(post.getNsfw()){
            Glide.with(getActivity()).load(post.getMainMedia().getUrl()).apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3))).addListener(new RequestListener<Drawable>() {
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
                Glide.with(getActivity()).load(post.getMainMedia().getUrl()).override(iw,ih).thumbnail(Glide.with(getActivity()).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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
                Glide.with(getActivity()).load(post.getMainMedia().getUrl()).override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).thumbnail(Glide.with(getActivity()).load(post.getThumbMedia().getUrl())).addListener(new RequestListener<Drawable>() {
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
    public void addAccount() {
        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    @Override
    public void switchAccount(String session,String id) {
        Log.e("session",session);

        if(!session.equals(ParseUser.getCurrentUser().getSessionToken())){
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.switchaccount));
            progressDialog.show();
            ParseUser.becomeInBackground(session, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {

                    if(e==null){
                        progressDialog.dismiss();
                        getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().finish();
                    }
                    else{
                        if(e.getCode() == ParseException.INVALID_SESSION_TOKEN){
                            GenelUtil.removeUserFromCache(id, getActivity());
                        }
                        progressDialog.dismiss();
                        GenelUtil.ToastLong(getActivity(),getString(R.string.invalidsessiontoken));
                    }
                }
            });
        }


    }
}
