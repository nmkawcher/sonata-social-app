package com.sonata.socialapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;
import com.sonata.socialapp.activities.sonata.HashtagActivity;
import com.sonata.socialapp.activities.sonata.MainActivity;
import com.sonata.socialapp.activities.sonata.MessagesActivity;
import com.sonata.socialapp.activities.sonata.SearchActivity;
import com.sonata.socialapp.activities.sonata.StartActivity;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.adapters.PostAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;
import com.vincan.medialoader.DownloadManager;
import com.vincan.medialoader.MediaLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.jzvd.Jzvd;
import q.rorbin.badgeview.QBadgeView;


public class HomeFragment extends Fragment implements RecyclerViewClick, BlockedAdapterClick {

    public HomeFragment() {

    }
    private List<ListObject> list;
    private List<UnifiedNativeAd> listreklam;
    private RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    private PostAdapter adapter;
    private boolean loading=true;
    private Date date;
    private OnScrollListener onScrollListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    private ProgressBar progressBar;
    private RelativeLayout search,upload,messages;
    RoundKornerRelativeLayout messageLayout;
    List<String> seenList;
    int hmt = 0;


    @Override
    public void onDestroy() {

        for(int i = 0;i<listreklam.size();i++){
            listreklam.get(i).destroy();
        }

        super.onDestroy();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = view.findViewById(R.id.homeprogressbar);
        list=new ArrayList<>();

        listreklam=new ArrayList<>();
        recyclerView = view.findViewById(R.id.mainrecyclerview);


        seenList = Util.getSeenList(getActivity());

        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new PostAdapter();
        adapter.setContext(list,Glide.with(getActivity()),this,this);
        adapter.setHasStableIds(true);


        recyclerView.setAdapter(adapter);











        swipeRefreshLayout = view.findViewById(R.id.homeFragmentSwipeRefreshLayout);
        onRefreshListener = this::subRefresh;
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);


        //DownloadManager.getInstance(getContext()).enqueue(new DownloadManager.Request(""));

        onScrollListener = new OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(linearLayoutManager != null){
                        AutoPlayUtils.onScrollPlayVideo(recyclerView, R.id.masterExoPlayer, linearLayoutManager.findFirstVisibleItemPosition(), linearLayoutManager.findLastVisibleItemPosition());
                        if(!recyclerView.canScrollVertically(-1)){
                            Jzvd.releaseAllVideos();
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
                                    Glide.with(getActivity()).load(url).preload();
                                }
                                if(post.getType().equals("video")){
                                    HashMap<String,Object> mediaObject = post.getMediaList().get(0);
                                    ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                    String url = parseFile.getUrl();
                                    Log.e("MediaLoaderURL:",MediaLoader.getInstance(getActivity()).getProxyUrl(url));
                                    DownloadManager.getInstance(getActivity()).enqueue(new DownloadManager.Request(MediaLoader.getInstance(getActivity()).getProxyUrl(url)));
                                    //VideoUtils.preLoadVideos(getActivity(),((llm+a)%10),MyApp.getProxy(getActivity()).getProxyUrl(url));
                                    ParseFile thumb = (ParseFile) mediaObject.get("thumbnail");
                                    String thumburl = thumb.getUrl();
                                    Glide.with(getActivity()).load(thumburl).preload();
                                }
                                else{
                                    for (int im = 0; im < post.getImageCount(); im++){
                                        HashMap<String,Object> mediaObject = post.getMediaList().get(im);
                                        ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                        String url = parseFile.getUrl();
                                        Glide.with(getActivity()).load(url).preload();
                                    }
                                }
                            }
                        } catch (Exception ignored){}

                    }
                    for(int a = Math.max(llm-5,0); a < llm; a++){
                        try{
                            Post post = list.get(a).getPost();

                            if(post != null ){
                                if(!seenList.contains(post.getObjectId())){
                                    seenList.add(post.getObjectId());
                                }
                            }
                        } catch (Exception ignored){}
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy>0){

                    if(linearLayoutManager.findLastVisibleItemPosition()>(list.size()-4)&&!loading&&!postson){
                        loading=true;
                        get(date,false);
                    }


                }


            }
        };
        recyclerView.addOnScrollListener(onScrollListener);

        search = view.findViewById(R.id.homesearchripple);
        upload = view.findViewById(R.id.homeaddripple);
        messages = view.findViewById(R.id.homemessageripple);
        messageLayout = view.findViewById(R.id.homemessageimage);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) Objects.requireNonNull(getActivity())).startActivityResult();
            }
        });

        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MessagesActivity.class));
                if(badgeView!=null){
                    badgeView.hide(true);
                    badgeView=null;
                    ParseCloud.callFunctionInBackground("notifResetMessages",new HashMap<>());
                }
            }
        });


        get(null,false);
        //((MainActivity) Objects.requireNonNull(getActivity())).startExploreTab();


        return view;
    }





    public void openComments(Post post){
        ((MainActivity) Objects.requireNonNull(getActivity())).homeFragmentComment(post);
    }


    QBadgeView badgeView = null;
    public void addBadgeToMessages(int i){
        if(messages!=null && i > 0){
            if (badgeView == null){
                badgeView = new QBadgeView(getContext());
            }

            badgeView.setBadgeNumber(i)
                    .setBadgeGravity(Gravity.TOP|Gravity.END)
                    .setGravityOffset(-3, -3, true)
                    .bindTarget(messages);
        }
    }






    private void get(Date date,boolean isRefresh){

        HashMap<String, Object> params = new HashMap<>();
        if(date!=null){
            params.put("date", date);
        }
        params.put("seenList",seenList);
        params.put("hmt",hmt);
        params.put("lang", Util.getCurrentCountryCode(getActivity()));
        ParseCloud.callFunctionInBackground("getHomeObjects", params, (FunctionCallback<HashMap>) (objects, e) -> {
            Log.e("done","doneGet");
            if(getActive()){
                if(e==null){
                    hmt++;
                    //if(tabLayout.getSelectedTabPosition() != 1)return;
                    Log.e("done","doneGetErrorNull");


                    getAds(objects.get("suggestions") != null ? (List<Post>) objects.get("suggestions") : new ArrayList<>()
                            ,(List<Post>) objects.get("posts")
                            ,objects.get("users") != null ? (List<SonataUser>) objects.get("users") : new ArrayList<>()
                            ,(boolean) objects.get("hasmore")
                            ,(Date) objects.get("date")
                            ,isRefresh,1);

                    //initList(objects);


                }
                else{
                    Log.e("done","doneGetError "+e.getCode());

                    if(e.getCode()==ParseException.CONNECTION_FAILED){
                        get(date,isRefresh);
                    }
                    else if(e.getCode()==ParseException.INVALID_SESSION_TOKEN){
                        Util.ToastLong(getActivity(),getString(R.string.invalidsessiontoken));
                        ParseUser.logOut();
                        startActivity(new Intent(getActivity(), StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();

                    }
                    Log.e("error code",""+e.getCode());
                    Log.e("error message", Objects.requireNonNull(e.getMessage()));

                }
            }
        });



    }

    @Override
    public void onPause() {
        super.onPause();
        if(seenList!=null){
            Util.setSeenList(getActivity(),seenList);
        }
    }

    private void getInteresting(boolean isRefresh){

        HashMap<String, Object> params = new HashMap<>();
        if(date!=null){
            params.put("date", date);
        }
        params.put("seenList",seenList);
        params.put("hmt",hmt);
        params.put("lang", Util.getCurrentCountryCode(getActivity()));
        ParseCloud.callFunctionInBackground("getHomeDiscoverObjects", params, (FunctionCallback<HashMap>) (objects, e) -> {
            Log.e("done","doneGet");
            hmt++;
            if(getActive()){
                if(e==null){
                    Log.e("done","doneGetErrorNull");


                    List<Post> tList = (List<Post>) objects.get("posts");

                    Collections.shuffle(tList);
                    getAds(objects.get("suggestions") != null ? (List<Post>) objects.get("suggestions") : new ArrayList<>()
                            ,tList
                            ,objects.get("users") != null ? (List<SonataUser>) objects.get("users") : new ArrayList<>()
                            ,true
                            ,(Date) objects.get("date")
                            ,isRefresh,0);

                    //initList(objects);


                }
                else{
                    Log.e("done","doneGetError "+e.getCode());

                    if(e.getCode()==ParseException.CONNECTION_FAILED){
                        get(date,isRefresh);
                    }
                    else if(e.getCode()==ParseException.INVALID_SESSION_TOKEN){
                        Util.ToastLong(getActivity(),getString(R.string.invalidsessiontoken));
                        ParseUser.logOut();
                        startActivity(new Intent(getActivity(), StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        getActivity().finish();

                    }
                    Log.e("error code",""+e.getCode());
                    Log.e("error message", Objects.requireNonNull(e.getMessage()));

                }
            }
        });



    }


    public void Refresh(){
        if(linearLayoutManager!=null){

            if(linearLayoutManager.findFirstCompletelyVisibleItemPosition()==0){
                swipeRefreshLayout.setRefreshing(true);
                subRefresh();
            }
            else{
                recyclerView.scrollToPosition(0);
            }
        }
    }

    private void subRefresh(){
        if(!loading){
            loading=true;
            postson=false;
            get(null,true);
        }
    }

    public void notifyAdapter(){
        if(adapter!=null){
            adapter.notifyDataSetChanged();

        }
    }





    private void initList(List<Post> suggestions,List<Post> objects,List<SonataUser> users,boolean hasmore,Date date,List<UnifiedNativeAd> listreklam) {
        Log.e("done","InitList");

        if(getActive()){
            Collections.shuffle(users);
            objects.addAll(suggestions);
            Collections.shuffle(objects);
            Log.e("done","InitListActive");
            postson =!hasmore;
            this.date = date;
            if(objects.size()==0){
                loading =false;
                if(list!=null){
                    if(list.size()==0){
                        if(users.size()<=0){
                            ListObject post = new ListObject();
                            post.setType("boş");
                            list.add(post);
                            adapter.notifyItemInserted(0);
                        }

                    }
                    else{
                        if(list.get(list.size()-1).getType().equals("load")){
                            int in = list.size()-1;
                            list.remove(in);
                            adapter.notifyItemRemoved(in);
                        }

                    }
                }
                if(suggestions.size()>0){
                    ListObject postas = new ListObject();
                    postas.setType("suggest");
                    list.add(postas);
                    for(int i=0;i<suggestions.size();i++){

                        ListObject post = new ListObject();
                        post.setType(suggestions.get(i).getType());
                        Post p2 = suggestions.get(i);
                        p2.setLikenumber(p2.getLikenumber2());
                        p2.setCommentnumber(p2.getCommentnumber2());
                        p2.setSaved(p2.getSaved2());
                        p2.setCommentable(p2.getCommentable2());
                        p2.setLiked(p2.getLiked2());
                        post.setPost(p2);
                        list.add(post);

                    }
                }
                if(users.size()>0){
                    ListObject postas = new ListObject();
                    postas.setType("suggest");
                    list.add(postas);
                    for(int i=0;i<users.size();i++){

                        ListObject post = new ListObject();
                        post.setType("user");
                        post.setUser(users.get(i));
                        post.getUser().setFollowRequest(post.getUser().getFollowRequest2());
                        post.getUser().setBlock(post.getUser().getBlock2());
                        post.getUser().setFollow(post.getUser().getFollow2());
                        list.add(post);

                    }
                }

                adapter.notifyItemRangeInserted(0, list.size());
                swipeRefreshLayout.setRefreshing(false);
                Log.e("done","adapterNotified");

                progressBar.setVisibility(View.INVISIBLE);

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
                    ListObject posta = new ListObject();
                    posta.setType(objects.get(i).getType());
                    Post p22 = objects.get(i);
                    p22.setLikenumber(p22.getLikenumber2());
                    p22.setCommentnumber(p22.getCommentnumber2());
                    p22.setSaved(p22.getSaved2());
                    p22.setCommentable(p22.getCommentable2());
                    p22.setLiked(p22.getLiked2());
                    posta.setPost(p22);
                    list.add(posta);


                }

                loading =false;
                progressBar.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);

                if(users.size()>0){
                    ListObject postas = new ListObject();
                    postas.setType("suggest");
                    list.add(postas);
                    for(int i=0;i<users.size();i++){

                        ListObject post = new ListObject();
                        post.setType("user");
                        post.setUser(users.get(i));
                        post.getUser().setFollowRequest(post.getUser().getFollowRequest2());
                        post.getUser().setBlock(post.getUser().getBlock2());
                        post.getUser().setFollow(post.getUser().getFollow2());
                        list.add(post);

                    }
                }
                if(hasmore){
                    ListObject load = new ListObject();
                    load.setType("load");
                    list.add(load);
                }


                adapter.notifyItemRangeInserted(an, list.size()-an);

                //adapter.notifyDataSetChanged();

            }
        }


    }

    private void getAds(List<Post> suggestions,List<Post> objects,List<SonataUser> users,boolean hasmore,Date date,boolean isRefresh,int tab){
        Log.e("done","doneGetAds");
        if(getActive()){
            Util.loadAds(objects.size()+suggestions.size(),getActivity(), new com.sonata.socialapp.utils.interfaces.AdListener() {
                @Override
                public void done(List<UnifiedNativeAd> list) {
                    if(Util.isAlive(getActivity())){
                        listreklam.addAll(list);
                        if(isRefresh){
                            //refreshSetting();
                            HomeFragment.this.list.clear();
                            adapter.notifyDataSetChanged();

                        }
                        initList(suggestions,objects,users,hasmore,date,list);
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

    public void backPress(){
        if(linearLayoutManager!=null){
            Log.e("back1","true");
            int pos = linearLayoutManager.findFirstVisibleItemPosition();
            Log.e("back2","true");
            if(linearLayoutManager.findViewByPosition(pos)!=null){
                Log.e("back3","true");
                if(!recyclerView.canScrollVertically(-1) && pos==0){
                    Log.e("back4","true");
                    Objects.requireNonNull((MainActivity)getActivity()).homeBackPress();
                }
                else{
                    Log.e("back5","true");
                    recyclerView.scrollToPosition(0);
                }
            }
            else{
                Objects.requireNonNull((MainActivity)getActivity()).homeBackPress();
            }

        }
        else{
            Objects.requireNonNull((MainActivity)getActivity()).homeBackPress();
        }
    }

    private boolean getActive(){
        return getActivity()!=null && Util.isAlive(getActivity());
    }

    private static  String TAG ="HomeFragment";


    @Override
    public void onOptionsClick(int position, TextView commentNumber) {
        Util.handlePostOptionsClick(getContext(),position,list,adapter,commentNumber);
    }

    @Override
    public void onSocialClick(int positiona, int clickType, String text) {
        if(clickType== MyApp.TYPE_HASHTAG){
            //hashtag
            startActivity(new Intent(getContext(), HashtagActivity.class).putExtra("hashtag",text.replace("#","")));

        }
        else if(clickType==MyApp.TYPE_MENTION){
            //mention
            String username = text;

            username = username.replace("@","").trim();


            if(!username.equals(ParseUser.getCurrentUser().getUsername())){
                startActivity(new Intent(getContext(), GuestProfileActivity.class).putExtra("username",username));
            }

        }
        else if(clickType==MyApp.TYPE_LINK){
            Util.handleLinkClicks(getContext(),text,clickType);
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
            likeNumber.setText(Util.ConvertNumber((int)post.getLikenumber(),getContext()));


        }
        else{
            post.setLiked(false);
            likeImage.setImageDrawable(getContext().getDrawable(R.drawable.ic_like));
            if(post.getLikenumber()>0){

                post.increment("likenumber",-1);
                likeNumber.setText(Util.ConvertNumber((int)post.getLikenumber(),getContext()));                                                    }
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
        Post post = list.get(position).getPost();
        if(post.getCommentable()){
            openComments(post);
        }
    }



    @Override
    public void onImageClick(int position,ImageView imageView,int pos) {
        Post post = list.get(position).getPost();
        List<String> ulist = new ArrayList<>();

        for(int i = 0; i < post.getImageCount(); i++){
            ulist.add(String.valueOf(i));
        }
        Util.showImage(ulist,post.getMediaList(),imageView,pos,adapter);
    }


    @Override
    public void goToProfileClick(int position) {
        SonataUser user = list.get(position).getUser();
        if(Util.clickable(700)){
            if(!user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                startActivity(new Intent(getActivity(), GuestProfileActivity.class).putExtra("user",user));
            }
        }
    }

    @Override
    public void buttonClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay) {
        Log.e("Click","Block button Click");
        SonataUser user = list.get(position).getUser();
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
                                Util.ToastLong(getActivity(),getString(R.string.error));
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
                                Util.ToastLong(getActivity(),getString(R.string.error));
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
                                Util.ToastLong(getActivity(),getString(R.string.error));
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
                                Util.ToastLong(getActivity(),getString(R.string.error));
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
                            Util.ToastLong(getActivity(),getString(R.string.error));
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
                            Util.ToastLong(getActivity(),getString(R.string.error));
                        }


                    }
                });

            }
        }


    }
}
