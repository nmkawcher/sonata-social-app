package com.sonata.socialapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sonata.socialapp.R;
import com.sonata.socialapp.activities.groups.GroupSearchActivity;
import com.sonata.socialapp.activities.groups.GroupSettingsActivity;
import com.sonata.socialapp.utils.GenelUtil;

public class GroupFragment extends Fragment {

    public GroupFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_backyard, container, false);

        RelativeLayout appOptions = view.findViewById(R.id.appoptionsripple);
        appOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GenelUtil.clickable(500)){
                    startActivity(new Intent(getActivity(),GroupSettingsActivity.class));
                }
            }
        });

        RelativeLayout searchGroup = view.findViewById(R.id.groupsearchripple);
        searchGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GenelUtil.clickable(500)){
                    startActivity(new Intent(getActivity(),GroupSearchActivity.class));
                }
            }
        });

        return view;
    }


}
