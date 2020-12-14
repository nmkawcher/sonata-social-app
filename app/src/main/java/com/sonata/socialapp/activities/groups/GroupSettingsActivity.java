package com.sonata.socialapp.activities.groups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;

public class GroupSettingsActivity extends AppCompatActivity {


    RelativeLayout back, createNewGroup, groupsYouHaveCreated, groupsYouHaveJoined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_settings);

        back = findViewById(R.id.backbuttonbutton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        createNewGroup = findViewById(R.id.createnewgroup);
        createNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GenelUtil.clickable(500)){
                    startActivity(new Intent(GroupSettingsActivity.this,CreateGroupActivity.class));
                }
            }
        });

        groupsYouHaveCreated = findViewById(R.id.groupsyouhavecreated);
        groupsYouHaveCreated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GenelUtil.clickable(500)){
                    startActivity(new Intent(GroupSettingsActivity.this,GroupsYouHaveCreatedActivity.class));
                }
            }
        });

        groupsYouHaveJoined = findViewById(R.id.groupsyouhavejoined);
        groupsYouHaveJoined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GenelUtil.clickable(500)){
                    startActivity(new Intent(GroupSettingsActivity.this,GroupsYouHaveJoinedActivity.class));
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
