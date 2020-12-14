package com.sonata.socialapp.activities.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.adapters.GroupAdapter;
import com.sonata.socialapp.utils.classes.Group;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.interfaces.GroupAdapterClick;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GroupsYouHaveJoinedActivity extends AppCompatActivity implements GroupAdapterClick {

    RelativeLayout back;

    List<ListObject> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    GroupAdapter adapter;
    private boolean loading=true;

    Date date;
    RecyclerView.OnScrollListener onScrollListener;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_you_have_joined);
        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        recyclerView = findViewById(R.id.folreqrecyclerview);
        swipeRefreshLayout = findViewById(R.id.folreqSwipeRefreshLayout);
        onScrollListener = new RecyclerView.OnScrollListener() {
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
        ListObject object2 = new ListObject();
        object2.setType("load");
        list.add(object2);
        linearLayoutManager=new LinearLayoutManager(GroupsYouHaveJoinedActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new GroupAdapter();
        adapter.setContext(list, Glide.with(GroupsYouHaveJoinedActivity.this),this);
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

    private void refreshSetting(){
        list.clear();
    }


    private void getReqs(Date date,boolean isRefresh){
        if(GenelUtil.isAlive(this)){
            HashMap<String, Object> params = new HashMap<>();
            if(date!=null){
                params.put("date",date);
            }
            ParseCloud.callFunctionInBackground("getGroupsIHaveJoined", params, (FunctionCallback<List<Group>>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(GroupsYouHaveJoinedActivity.this)){
                    if(e==null){

                        if(objects!= null){
                            if(isRefresh){
                                refreshSetting();
                            }
                            initList(objects);
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

    private void initList(List<Group> objects) {
        Log.e("done","InitList");

        if(GenelUtil.isAlive(GroupsYouHaveJoinedActivity.this)){
            Log.e("done","InitListActive");

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
                        ListObject post = new ListObject();
                        post.setType("boş");
                        list.add(post);
                    }
                }

                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
                Log.e("done","adapterNotified");


            }
            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.get(list.size()-1));
                    }
                }

                date=objects.get(objects.size()-1).getDate("date");
                if(objects.size()<40){
                    postson =true;
                    for(int i=0;i<objects.size();i++){
                        ListObject post = new ListObject();
                        post.setType("group");
                        Group p2 = objects.get(i);
                        post.setGroup(p2);
                        list.add(post);
                    }

                    adapter.notifyDataSetChanged();
                    Log.e("done","adapterNotified");

                    loading =false;
                    swipeRefreshLayout.setRefreshing(false);


                }
                else{
                    for(int i=0;i<objects.size();i++){
                        ListObject post = new ListObject();
                        post.setType("group");
                        Group p2 = objects.get(i);
                        post.setGroup(p2);
                        list.add(post);
                    }



                    ListObject load = new ListObject();
                    load.setType("load");
                    list.add(load);

                    adapter.notifyDataSetChanged();
                    Log.e("done","adapterNotified");


                    loading =false;
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        }
        else{
            Log.e("done","InitListNotActive");

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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(int position) {
        startActivity(
                new Intent(
                        GroupsYouHaveJoinedActivity.this
                        ,GroupActivity.class)
                        .putExtra("group",list.get(position).getGroup()));

    }
}