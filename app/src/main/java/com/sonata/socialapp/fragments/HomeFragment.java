package com.sonata.socialapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.os.ConfigurationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.AdvancedSettingsActivity;
import com.sonata.socialapp.activities.sonata.GuestProfileActivity;
import com.sonata.socialapp.activities.sonata.HashtagActivity;
import com.sonata.socialapp.activities.sonata.MainActivity;
import com.sonata.socialapp.activities.sonata.MessagesActivity;
import com.sonata.socialapp.activities.sonata.SearchActivity;
import com.sonata.socialapp.activities.sonata.StartActivity;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.MyApp;
import com.sonata.socialapp.utils.VideoUtils.AutoPlayUtils;
import com.sonata.socialapp.utils.VideoUtils.VideoUtils;
import com.sonata.socialapp.utils.adapters.SafPostAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;
import com.sonata.socialapp.utils.interfaces.RecyclerViewClick;
import com.vincan.medialoader.DownloadManager;
import com.vincan.medialoader.MediaLoader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.jzvd.Jzvd;
import q.rorbin.badgeview.QBadgeView;


public class HomeFragment extends Fragment implements RecyclerViewClick, BlockedAdapterClick {


    private List<ListObject> list;
    private List<UnifiedNativeAd> listreklam;
    private RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    private SafPostAdapter adapter;
    private boolean loading=true;
    private Date date;
    private OnScrollListener onScrollListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    private ProgressBar progressBar;
    private RelativeLayout search,upload,messages;
    Spinner spinner;
    AdapterView.OnItemSelectedListener onItemSelectedListener;
    int spinnerPosition;
    RoundKornerRelativeLayout messageLayout;
    List<String> seenList;


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


        seenList = GenelUtil.getSeenList(getActivity());
        spinner = view.findViewById(R.id.settingsaccountypespinner);
        List<String> listspinner = new ArrayList<>();

        listspinner.add(getString(R.string.suggestions));
        listspinner.add(getString(R.string.followings));

        ArrayAdapter<String> adapterspinner = new ArrayAdapter<String>(getContext(),R.layout.spinner_item_home,listspinner);
        spinner.setAdapter(adapterspinner);




        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new SafPostAdapter();
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
                        if(spinner.getSelectedItemPosition() == 1){
                            get(date,false);
                        }
                        else if(spinner.getSelectedItemPosition() == 0){
                            getInteresting(false);
                        }
                        spinner.setEnabled(false);
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


        onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemSelected: "+listspinner.get(position));
                spinner.setEnabled(false);
                if(position == 1){
                    list.clear();
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.VISIBLE);
                    get(null,true);
                }
                else if(position == 0){
                    list.clear();
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.VISIBLE);
                    getInteresting(false);
                    //spinner.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner.setOnItemSelectedListener(onItemSelectedListener);
        spinner.setEnabled(false);

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
        ParseCloud.callFunctionInBackground("getHomeObjects", params, (FunctionCallback<HashMap>) (objects, e) -> {
            Log.e("done","doneGet");
            if(getActive()){
                if(e==null){
                    Log.e("done","doneGetErrorNull");


                    getAds((List<Post>) objects.get("posts")
                            ,objects.get("users") != null ? (List<SonataUser>) objects.get("users") : new ArrayList<>()
                            ,(boolean) objects.get("hasmore")
                            ,(Date) objects.get("date")
                            ,isRefresh);

                    //initList(objects);


                }
                else{
                    spinner.setEnabled(true);
                    Log.e("done","doneGetError "+e.getCode());

                    if(e.getCode()==ParseException.CONNECTION_FAILED){
                        get(date,isRefresh);
                    }
                    else if(e.getCode()==ParseException.INVALID_SESSION_TOKEN){
                        GenelUtil.ToastLong(getActivity(),getString(R.string.invalidsessiontoken));
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
            GenelUtil.setSeenList(getActivity(),seenList);
        }
    }

    private void getInteresting(boolean isRefresh){

        HashMap<String, Object> params = new HashMap<>();
        if(date!=null){
            params.put("date", date);
        }
        params.put("seenList",seenList);
        params.put("lang", ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0).toString());
        ParseCloud.callFunctionInBackground("getHomeDiscoverObjects", params, (FunctionCallback<HashMap>) (objects, e) -> {
            Log.e("done","doneGet");
            if(getActive()){
                if(e==null){
                    Log.e("done","doneGetErrorNull");


                    List<Post> tList = (List<Post>) objects.get("posts");

                    Collections.shuffle(tList);
                    getAds(tList
                            ,objects.get("users") != null ? (List<SonataUser>) objects.get("users") : new ArrayList<>()
                            ,true
                            ,(Date) objects.get("date")
                            ,isRefresh);

                    //initList(objects);


                }
                else{
                    spinner.setEnabled(true);
                    Log.e("done","doneGetError "+e.getCode());

                    if(e.getCode()==ParseException.CONNECTION_FAILED){
                        get(date,isRefresh);
                    }
                    else if(e.getCode()==ParseException.INVALID_SESSION_TOKEN){
                        GenelUtil.ToastLong(getActivity(),getString(R.string.invalidsessiontoken));
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
            if(spinner.getSelectedItemPosition()==1){
                get(null,true);
            }
            else if(spinner.getSelectedItemPosition()==0){
                getInteresting(true);
            }
            spinner.setEnabled(false);
        }
    }

    public void notifyAdapter(){
        if(adapter!=null){
            adapter.notifyDataSetChanged();

        }
    }





    private void initList(List<Post> objects,List<SonataUser> users,boolean hasmore,Date date,List<UnifiedNativeAd> listreklam) {
        Log.e("done","InitList");

        if(getActive()){
            Collections.shuffle(users);
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
                spinner.setEnabled(true);

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
                spinner.setEnabled(true);
                //adapter.notifyDataSetChanged();

            }
        }


    }

    private void getAds(List<Post> objects,List<SonataUser> users,boolean hasmore,Date date,boolean isRefresh){
        Log.e("done","doneGetAds");
        if(getActive()){
            GenelUtil.loadAds(objects.size(),getActivity(), new com.sonata.socialapp.utils.interfaces.AdListener() {
                @Override
                public void done(List<UnifiedNativeAd> list) {
                    if(GenelUtil.isAlive(getActivity())){
                        listreklam.addAll(list);
                        if(isRefresh){
                            //refreshSetting();
                            list.clear();
                            adapter.notifyDataSetChanged();

                        }
                        initList(objects,users,hasmore,date,list);
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
        return getActivity()!=null && GenelUtil.isAlive(getActivity());
    }

    private static  String TAG ="HomeFragment";


    @Override
    public void onOptionsClick(int position, TextView commentNumber) {
        GenelUtil.handlePostOptionsClick(getContext(),position,list,adapter,commentNumber);
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
            GenelUtil.handleLinkClicks(getContext(),text,clickType);
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
        GenelUtil.showImage(ulist,post.getMediaList(),imageView,pos,adapter);
    }


    @Override
    public void goToProfileClick(int position) {
        SonataUser user = list.get(position).getUser();
        if(GenelUtil.clickable(700)){
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
                                GenelUtil.ToastLong(getActivity(),getString(R.string.error));
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
                                GenelUtil.ToastLong(getActivity(),getString(R.string.error));
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
                                GenelUtil.ToastLong(getActivity(),getString(R.string.error));
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
                                GenelUtil.ToastLong(getActivity(),getString(R.string.error));
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
                            GenelUtil.ToastLong(getActivity(),getString(R.string.error));
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
                            GenelUtil.ToastLong(getActivity(),getString(R.string.error));
                        }


                    }
                });

            }
        }


    }
}
