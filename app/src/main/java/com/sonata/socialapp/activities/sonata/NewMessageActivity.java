package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;
import com.sonata.socialapp.utils.adapters.SearchAccountAdapter;
import com.sonata.socialapp.utils.adapters.SearchUserAdapter;
import com.sonata.socialapp.utils.classes.ListObject;
import com.sonata.socialapp.utils.classes.SonataUser;
import com.sonata.socialapp.utils.interfaces.BlockedAdapterClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewMessageActivity extends AppCompatActivity implements BlockedAdapterClick {

    RelativeLayout back;
    List<ListObject> list;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    SearchAccountAdapter adapter;

    SearchView searchView;
    String searchText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = findViewById(R.id.folreqrecyclerview);
        list=new ArrayList<>();

        linearLayoutManager=new LinearLayoutManager(NewMessageActivity.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new SearchAccountAdapter();
        adapter.setContext(list, Glide.with(NewMessageActivity.this),this);
        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

        searchView = findViewById(R.id.searchView);
        searchView.requestFocus();
        GenelUtil.showKeyboard(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                list.clear();
                ListObject object2 = new ListObject();
                object2.setType("load");
                list.add(object2);
                adapter.notifyDataSetChanged();
                if(GenelUtil.isAlive(NewMessageActivity.this)){
                    getReqs(searchText);
                }
                searchView.setIconified(false);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }


    private void getReqs(String searchStr){
        if(GenelUtil.isAlive(this)){
            HashMap<String, Object> params = new HashMap<>();
            params.put("text",searchStr);
            ParseCloud.callFunctionInBackground("search", params, (FunctionCallback<List<HashMap>>) (objects, e) -> {
                Log.e("done","done");
                if(GenelUtil.isAlive(NewMessageActivity.this)){
                    if(e==null){
                        if(searchText.equals(searchStr)){
                            if(objects!= null){
                                List<SonataUser> userList = (List<SonataUser>) objects.get(0).get("users");
                                initUser(userList);

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
        if(GenelUtil.isAlive(NewMessageActivity.this)){
            if(list.size()>0){
                if(list.get(list.size()-1).getType().equals("load")){
                    list.remove(list.size()-1);
                }
            }
            for(int i=0;i<objects.size();i++){

                ListObject post = new ListObject();
                post.setType("user");
                SonataUser p2 = objects.get(i);

                post.setUser(p2);
                list.add(post);

            }

            adapter.notifyDataSetChanged();
        }
    }




    @Override
    public void goToProfileClick(int position) {
        SonataUser user = list.get(position).getUser();
        startActivity(new Intent(this,DirectMessageActivity.class).putExtra("user",user));
    }

    @Override
    public void buttonClick(int position, TextView buttonText, RoundKornerRelativeLayout buttonLay) {

    }
}
