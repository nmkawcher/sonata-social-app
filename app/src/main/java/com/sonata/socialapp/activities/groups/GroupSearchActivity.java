package com.sonata.socialapp.activities.groups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
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

public class GroupSearchActivity extends AppCompatActivity implements GroupAdapterClick {

    SearchView searchView;
    RelativeLayout back;
    RelativeLayout mainLay;
    String searchText = "";

    //Post Stuff
    List<ListObject> list;
    RecyclerView recyclerView;
    private boolean postson=false;
    private LinearLayoutManager linearLayoutManager;
    GroupAdapter adapter;
    String searchString = "";
    private boolean loading=true;

    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_search);
        back = findViewById(R.id.backbuttonbutton);


        mainLay = findViewById(R.id.mainLay);

        list=new ArrayList<>();


        recyclerView = findViewById(R.id.searchPostRecycler);



        linearLayoutManager=new LinearLayoutManager(GroupSearchActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new GroupAdapter();
        adapter.setContext(list, Glide.with(GroupSearchActivity.this),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.searchView);
        searchView.requestFocus();
        GenelUtil.showKeyboard(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                refreshSettingPost();
                ListObject object2 = new ListObject();
                object2.setType("load");
                list.add(object2);
                adapter.notifyDataSetChanged();
                if(GenelUtil.isAlive(GroupSearchActivity.this)){
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


    }


    private void getReqs(String searchStr){
        if(GenelUtil.isAlive(this)){
            HashMap<String, Object> params = new HashMap<>();
            params.put("text",searchStr);
            ParseCloud.callFunctionInBackground("searchGroup", params, (FunctionCallback<List<Group>>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(GroupSearchActivity.this)){
                    if(e==null){
                        if(searchText.equals(searchStr)){
                            if(objects!= null){
                                initList(objects);
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

    private void refreshSettingPost(){
        list.clear();
        postson=false;

    }

    private void initList(List<Group> objects) {
        Log.e("done","InitList");

        if(GenelUtil.isAlive(GroupSearchActivity.this)){
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

                adapter.notifyDataSetChanged();
                Log.e("done","adapterNotified");


            }
            else{
                if(list.size()>0){
                    if(list.get(list.size()-1).getType().equals("load")){
                        list.remove(list.get(list.size()-1));
                    }
                }

                date=objects.get(objects.size()-1).getCreatedAt();
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
                        GroupSearchActivity.this
                        ,GroupActivity.class)
                        .putExtra("group",list.get(position).getGroup()));

    }
}
