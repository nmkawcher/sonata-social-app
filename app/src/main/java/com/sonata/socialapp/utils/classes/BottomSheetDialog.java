package com.sonata.socialapp.utils.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jcminarro.roundkornerlayout.RoundKornerRelativeLayout;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.Util;
import com.sonata.socialapp.utils.interfaces.AccountManagerClicks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private JSONArray list;
    private Context context;
    private String id;
    private AccountManagerClicks clicks;
    public BottomSheetDialog(JSONArray list, Context context, String id, AccountManagerClicks clicks) {
        this.list = list;
        this.context = context;
        this.id = id;
        this.clicks = clicks;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container,false);

        RelativeLayout addAccount = view.findViewById(R.id.user_lay_add_account);
        if(list.length()> Util.ACCOUNT_LIMIT-1){
            addAccount.setVisibility(View.GONE);
        }
        else{
            addAccount.setVisibility(View.VISIBLE);
            RelativeLayout addaccrip = view.findViewById(R.id.rippleaddacc);
            addaccrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clicks.addAccount();
                }
            });
        }
        try {
            if(list.length() > 0){
                RelativeLayout it1 = view.findViewById(R.id.user_lay_1);
                it1.setVisibility(View.VISIBLE);
                JSONObject user = (JSONObject) list.get(0);

                ImageView imageView = view.findViewById(R.id.followreqlayoutpp);
                if(user.getBoolean("haspp")){
                    Glide.with(context).load(user.getString("pp")).into(imageView);
                }
                else{
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.emptypp)).into(imageView);
                }

                TextView name = view.findViewById(R.id.followreqname);
                name.setText(user.getString("namesurname"));

                TextView username = view.findViewById(R.id.followrequsername);
                username.setText("@"+user.getString("username"));

                RoundKornerRelativeLayout rl = view.findViewById(R.id.folreqacceptlayout);
                if(user.getString("id").equals(id)){
                    rl.setVisibility(View.VISIBLE);
                }
                else{
                    rl.setVisibility(View.GONE);
                }

                RelativeLayout ripple = view.findViewById(R.id.rippleselect);
                ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            clicks.switchAccount(user.getString("session"),user.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if(list.length() > 1){
                RelativeLayout it1 = view.findViewById(R.id.user_lay_2);
                it1.setVisibility(View.VISIBLE);
                JSONObject user = (JSONObject) list.get(1);

                ImageView imageView = view.findViewById(R.id.followreqlayoutpp2);
                if(user.getBoolean("haspp")){
                    Glide.with(context).load(user.getString("pp")).into(imageView);
                }
                else{
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.emptypp)).into(imageView);
                }

                TextView name = view.findViewById(R.id.followreqname2);
                name.setText(user.getString("namesurname"));

                TextView username = view.findViewById(R.id.followrequsername2);
                username.setText("@"+user.getString("username"));

                RoundKornerRelativeLayout rl = view.findViewById(R.id.folreqacceptlayout2);
                if(user.getString("id").equals(id)){
                    rl.setVisibility(View.VISIBLE);
                }
                else{
                    rl.setVisibility(View.GONE);
                }

                RelativeLayout ripple = view.findViewById(R.id.rippleselect2);
                ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            clicks.switchAccount(user.getString("session"),user.getString("id"));
                            BottomSheetDialog.this.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if(list.length() > 2){
                RelativeLayout it1 = view.findViewById(R.id.user_lay_3);
                it1.setVisibility(View.VISIBLE);
                JSONObject user = (JSONObject) list.get(2);

                ImageView imageView = view.findViewById(R.id.followreqlayoutpp3);
                if(user.getBoolean("haspp")){
                    Glide.with(context).load(user.getString("pp")).into(imageView);
                }
                else{
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.emptypp)).into(imageView);
                }

                TextView name = view.findViewById(R.id.followreqname3);
                name.setText(user.getString("namesurname"));

                TextView username = view.findViewById(R.id.followrequsername3);
                username.setText("@"+user.getString("username"));

                RoundKornerRelativeLayout rl = view.findViewById(R.id.folreqacceptlayout3);
                if(user.getString("id").equals(id)){
                    rl.setVisibility(View.VISIBLE);
                }
                else{
                    rl.setVisibility(View.GONE);
                }

                RelativeLayout ripple = view.findViewById(R.id.rippleselect3);
                ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            clicks.switchAccount(user.getString("session"),user.getString("id"));
                            BottomSheetDialog.this.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if(list.length() > 3){
                RelativeLayout it1 = view.findViewById(R.id.user_lay_4);
                it1.setVisibility(View.VISIBLE);
                JSONObject user = (JSONObject) list.get(3);

                ImageView imageView = view.findViewById(R.id.followreqlayoutpp4);
                if(user.getBoolean("haspp")){
                    Glide.with(context).load(user.getString("pp")).into(imageView);
                }
                else{
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.emptypp)).into(imageView);
                }

                TextView name = view.findViewById(R.id.followreqname4);
                name.setText(user.getString("namesurname"));

                TextView username = view.findViewById(R.id.followrequsername4);
                username.setText("@"+user.getString("username"));

                RoundKornerRelativeLayout rl = view.findViewById(R.id.folreqacceptlayout4);
                if(user.getString("id").equals(id)){
                    rl.setVisibility(View.VISIBLE);
                }
                else{
                    rl.setVisibility(View.GONE);
                }

                RelativeLayout ripple = view.findViewById(R.id.rippleselect4);
                ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            clicks.switchAccount(user.getString("session"),user.getString("id"));
                            BottomSheetDialog.this.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if(list.length() > 4){
                RelativeLayout it1 = view.findViewById(R.id.user_lay_5);
                it1.setVisibility(View.VISIBLE);
                JSONObject user = (JSONObject) list.get(4);

                ImageView imageView = view.findViewById(R.id.followreqlayoutpp5);
                if(user.getBoolean("haspp")){
                    Glide.with(context).load(user.getString("pp")).into(imageView);
                }
                else{
                    Glide.with(context).load(context.getResources().getDrawable(R.drawable.emptypp)).into(imageView);
                }

                TextView name = view.findViewById(R.id.followreqname5);
                name.setText(user.getString("namesurname"));

                TextView username = view.findViewById(R.id.followrequsername5);
                username.setText("@"+user.getString("username"));

                RoundKornerRelativeLayout rl = view.findViewById(R.id.folreqacceptlayout5);
                if(user.getString("id").equals(id)){
                    rl.setVisibility(View.VISIBLE);
                }
                else{
                    rl.setVisibility(View.GONE);
                }

                RelativeLayout ripple = view.findViewById(R.id.rippleselect5);
                ripple.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            clicks.switchAccount(user.getString("session"),user.getString("id"));
                            BottomSheetDialog.this.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }




        return view;
    }
}
