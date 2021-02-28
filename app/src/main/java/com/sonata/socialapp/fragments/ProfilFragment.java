package com.sonata.socialapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.GridProfilAdapter;
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
        name=view.findViewById(R.id.profilename);
        bio=view.findViewById(R.id.profilebio);
        followers=view.findViewById(R.id.followersnumbers);
        followings=view.findViewById(R.id.followingnumbers);
        followerstext = view.findViewById(R.id.followerstext);
        followingtext = view.findViewById(R.id.followingtext);


        user = (SonataUser) ParseUser.getCurrentUser();
        setProfile(user);
        HashMap<String,String> params = new HashMap<>();
        ParseCloud.callFunctionInBackground("refreshOwnProfile", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap object, ParseException e) {
                if(getActive()){
                    if(e==null){
                        setProfile((SonataUser)object.get("user"));
                    }
                }

            }
        });
        RelativeLayout settings = view.findViewById(R.id.appoptionsripple);
        settings.setOnClickListener(v -> {
            if(dialog != null){
                dialog.dismiss();
            }
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
            ParseCloud.callFunctionInBackground("refreshOwnProfile", params, (FunctionCallback<HashMap>) (object, e) -> {
                if(e==null){
                    user = (SonataUser)object.get("user");
                    setProfile(user);
                }

            });

        }

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


    private void openComments(Post post){
        ((MainActivity) Objects.requireNonNull(getActivity())).profileFragmentComment(post);
    }

    private void initList(List<Post> objects,boolean hasmore,Date date) {
        Log.e("done","InitList");

        if(getActive()){
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

                loading =false;
                swipeRefreshLayout.setRefreshing(false);
                if(hasmore){

                    ListObject load = new ListObject();
                    load.setType("load");
                    list.add(load);
                }

                adapter.notifyItemRangeInserted(an, list.size()-an);
                if(postAdapter!=null){
                    postAdapter.notifyItemRangeInserted(an, list.size()-an);
                }

                //adapter.notifyDataSetChanged();
                Log.e("done","adapterNotified");

            }
        }
        else{
            Log.e("done","InitListNotActive");

        }

    }








    private void get(Date date,boolean isRefresh){

        HashMap<String, Object> params = new HashMap<>();
        if(date!=null){
            params.put("date", date);
        }
        ParseCloud.callFunctionInBackground("getOwnPosts", params, (FunctionCallback<HashMap>) (objects, e) -> {
            Log.e("done","done");
            if(getActive()){
                if(e==null){

                    if(objects!= null){
                        if(isRefresh){
                            list.clear();
                            adapter.notifyDataSetChanged();
                            if(postAdapter!=null){
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                        initList((List<Post>)objects.get("posts")
                                ,(boolean) objects.get("hasmore")
                                ,(Date)objects.get("date"));
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
        if(lastCommentPosition > -1 && list != null){
            if(list.get(lastCommentPosition).getPost() != null && list.get(lastCommentPosition).getPost().getIsDeleted()){
                list.remove(lastCommentPosition);
            }
        }
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
        GenelUtil.handlePostOptionsClick(getContext(),position,list,postAdapter,commentNumber);
    }

    @Override
    public void onSocialClick(int position, int clickType, String text) {
        GenelUtil.handleLinkClicks(getContext(),text,clickType);
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

    int lastCommentPosition = -1;
    @Override
    public void onOpenComments(int position) {
        lastCommentPosition = position;
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
    public void onImageClick(int position,ImageView imageView,int pos) {
        Post post = list.get(position).getPost();
        List<String> ulist = new ArrayList<>();

        for(int i = 0; i < post.getImageCount(); i++){
            ulist.add(String.valueOf(i));
        }
        GenelUtil.showImage(ulist,post.getMediaList(),imageView,pos,postAdapter);
    }

    @Override
    public void addAccount() {
        assert getActivity()!=null;
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
                        String text = String.format(getResources().getString(R.string.accsw), "@"+ParseUser.getCurrentUser().getUsername());
                        GenelUtil.ToastLong(getActivity(),text);
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
