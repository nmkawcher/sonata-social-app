package com.sonata.socialapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.sonata.GetRestDiscoverActivity;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.adapters.DiscoverGridProfilAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.Post;
import com.sonata.socialapp.utils.interfaces.GridClick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class DiscoverFragment extends Fragment implements GridClick {

    public DiscoverFragment() {

    }

    private List<ListObject> list;
    private RecyclerView recyclerView;
    private DiscoverGridProfilAdapter adapter;
    private boolean loading=true;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    RelativeLayout back;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        recyclerView = view.findViewById(R.id.mainrecyclerview);
        swipeRefreshLayout = view.findViewById(R.id.homeFragmentSwipeRefreshLayout);


        list=new ArrayList<>();
        ListObject object2 = new ListObject();
        object2.setType("load");
        list.add(object2);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        ((GridLayoutManager)recyclerView.getLayoutManager()).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                String type = list.get(position).getType();
                if(type.equals("load")||type.equals("boş")||type.equals("private")){
                    return 3;
                }
                return 1;
            }
        });
        adapter=new DiscoverGridProfilAdapter();
        adapter.setContext(list, Glide.with(getActivity()),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        onRefreshListener = () -> {
            if(!loading){
                loading=true;

                getReqs(null,true);

            }
        };
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);


        RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int llm = ((GridLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                for(int a = llm; a < Math.min(llm+15,list.size()); a++){
                    try{
                        Post post = list.get(a).getPost();

                        if(post != null ){

                            if(post.getType().equals("video")){
                                HashMap<String,Object> mediaObject = post.getMediaList().get(0);

                                ParseFile thumb = (ParseFile) mediaObject.get("thumbnail");
                                String thumburl = thumb.getUrl();
                                Glide.with(getActivity()).load(thumburl).preload();
                            }
                            else{
                                HashMap<String,Object> mediaObject = post.getMediaList().get(0);
                                ParseFile parseFile = (ParseFile) mediaObject.get("media");
                                String url = parseFile.getUrl();
                                Glide.with(getActivity()).load(url).preload();
                            }
                        }
                    } catch (Exception ignored){}

                }
            }
        };

        recyclerView.addOnScrollListener(listener);


        if(getActivity() != null && Util.isAlive(getActivity()) && !this.isDetached()){
            getReqs(null,true);
        }


        return view;
    }

    private void getReqs(Date date,boolean isRefresh){
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("lang", Util.getCurrentCountryCode(getActivity()));
        ParseCloud.callFunctionInBackground("getDiscoverObjects", params, new FunctionCallback<HashMap>() {
            @Override
            public void done(HashMap  objects, ParseException e) {
                Log.e("done","done");
                if(getActivity() != null && Util.isAlive(getActivity()) && !DiscoverFragment.this.isDetached()){
                    if(e==null){

                        if(objects!= null){
                            if(isRefresh){
                                list.clear();
                            }
                            Collections.shuffle(((List<Post>)objects.get("posts")));
                            initList(((List<Post>)objects.get("posts")));

                        }


                    }
                    else{
                        Log.e("error code",""+e.toString());
                        if(e.getCode() == ParseException.CONNECTION_FAILED){
                            getReqs(date,isRefresh);
                        }
                    }
                }
            }
        });
    }



    private void initList(List<Post> objects) {
        if(getActivity() != null && Util.isAlive(getActivity()) && !DiscoverFragment.this.isDetached()){

            if(objects.size()==0){
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


            }
            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.get(list.size()-1));
                    }
                }
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
                loading =false;
                swipeRefreshLayout.setRefreshing(false);

            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(int position) {
        startActivity(new Intent(getActivity(), GetRestDiscoverActivity.class).putExtra("post",list.get(position).getPost()));
    }
}
