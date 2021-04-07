package com.sonata.socialapp.activities.sonata;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseCloud;
import com.sonata.socialapp.R;
import com.sonata.socialapp.utils.GenelUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseCategoryActivity extends AppCompatActivity {

    RelativeLayout ripple0, ripple1, ripple2, ripple3, ripple4, ripple5, ripple6, ripple7, ripple8
            , ripple15, ripple16, next;

    CheckBox check0, check1, check2, check3, check4, check5, check6, check7, check8
            , check15, check16;

    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        list = new ArrayList<>();
        next = findViewById(R.id.nexttextripple);
        next.setVisibility(View.INVISIBLE);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()>0){
                    ProgressDialog progressDialog = new ProgressDialog(ChooseCategoryActivity.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage(getString(R.string.loading));
                    progressDialog.show();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("list",list);
                    ParseCloud.callFunctionInBackground("newRegisterCategoryChoose",hashMap);
                    startActivity(new Intent(ChooseCategoryActivity.this,FollowSuggestActivity.class)
                            .putStringArrayListExtra("list",list));
                    progressDialog.dismiss();
                    finish();
                }
                else{
                    startActivity(new Intent(ChooseCategoryActivity.this,FollowSuggestActivity.class));
                    finish();
                }
            }
        });

        ripple0 = findViewById(R.id.content_ripple_0);
        check0 = findViewById(R.id.content_check_0);
        ripple0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check0.setChecked(!check0.isChecked());
            }
        });
        //exist
        check0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("3Chefq52Ky5wBFbG8E74eRMksnqdFk")){
                        list.add("3Chefq52Ky5wBFbG8E74eRMksnqdFk");
                    }
                }
                else{
                    list.remove("3Chefq52Ky5wBFbG8E74eRMksnqdFk");
                }
                checkIt();
            }
        });

        ripple1 = findViewById(R.id.content_ripple_1);
        check1 = findViewById(R.id.content_check_1);
        ripple1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check1.setChecked(!check1.isChecked());
            }
        });
        //exist
        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("h9LBH2LVNj9PVpsH9HT7BS6fP8ML3X")){
                        list.add("h9LBH2LVNj9PVpsH9HT7BS6fP8ML3X");
                    }
                }
                else{
                    list.remove("h9LBH2LVNj9PVpsH9HT7BS6fP8ML3X");
                }
                checkIt();
            }
        });

        ripple2 = findViewById(R.id.content_ripple_2);
        check2 = findViewById(R.id.content_check_2);
        ripple2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check2.setChecked(!check2.isChecked());
            }
        });
        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("6N4Q6BfWsXC25vRNhAu5PkZaU9N69k")){
                        list.add("6N4Q6BfWsXC25vRNhAu5PkZaU9N69k");
                    }
                }
                else{
                    list.remove("6N4Q6BfWsXC25vRNhAu5PkZaU9N69k");
                }
                checkIt();
            }
        });


        ripple3 = findViewById(R.id.content_ripple_3);
        check3 = findViewById(R.id.content_check_3);
        ripple3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check3.setChecked(!check3.isChecked());
            }
        });
        check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("tx4bJH6tWkT6MgFwHm2GZz6BcAcdFC")){
                        list.add("tx4bJH6tWkT6MgFwHm2GZz6BcAcdFC");
                    }
                }
                else{
                    list.remove("tx4bJH6tWkT6MgFwHm2GZz6BcAcdFC");
                }
                checkIt();
            }
        });


        ripple4 = findViewById(R.id.content_ripple_4);
        check4 = findViewById(R.id.content_check_4);
        ripple4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check4.setChecked(!check4.isChecked());
            }
        });
        check4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("kC8vrjRNG7AxjP6xqUdWLkDn2NbCsh")){
                        list.add("kC8vrjRNG7AxjP6xqUdWLkDn2NbCsh");
                    }
                }
                else{
                    list.remove("kC8vrjRNG7AxjP6xqUdWLkDn2NbCsh");
                }
                checkIt();
            }
        });


        ripple5 = findViewById(R.id.content_ripple_5);
        check5 = findViewById(R.id.content_check_5);
        ripple5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check5.setChecked(!check5.isChecked());
            }
        });
        check5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("mwhrT4CauTx622t5r524GVwzb7Evvz")){
                        list.add("mwhrT4CauTx622t5r524GVwzb7Evvz");
                    }
                }
                else{
                    list.remove("mwhrT4CauTx622t5r524GVwzb7Evvz");
                }
                checkIt();
            }
        });


        ripple6 = findViewById(R.id.content_ripple_6);
        check6 = findViewById(R.id.content_check_6);
        ripple6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check6.setChecked(!check6.isChecked());
            }
        });
        check6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("JsJCapxsL74fVBmPa3FpMK4QkfeQYP")){
                        list.add("JsJCapxsL74fVBmPa3FpMK4QkfeQYP");
                    }
                }
                else{
                    list.remove("JsJCapxsL74fVBmPa3FpMK4QkfeQYP");
                }
                checkIt();
            }
        });


        ripple7 = findViewById(R.id.content_ripple_7);
        check7 = findViewById(R.id.content_check_7);
        ripple7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check7.setChecked(!check7.isChecked());
            }
        });
        check7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("CjQ9Tbv7ZsJqTC4Yw2xmL4NEfh3GdN")){
                        list.add("CjQ9Tbv7ZsJqTC4Yw2xmL4NEfh3GdN");
                    }
                }
                else{
                    list.remove("CjQ9Tbv7ZsJqTC4Yw2xmL4NEfh3GdN");
                }
                checkIt();
            }
        });


        ripple8 = findViewById(R.id.content_ripple_8);
        check8 = findViewById(R.id.content_check_8);
        ripple8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check8.setChecked(!check8.isChecked());
            }
        });
        check8.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("5aT7qyvEVTEf3aTFNuvUwmR9HFwnVq")){
                        list.add("5aT7qyvEVTEf3aTFNuvUwmR9HFwnVq");
                    }
                }
                else{
                    list.remove("5aT7qyvEVTEf3aTFNuvUwmR9HFwnVq");
                }
                checkIt();
            }
        });




        ripple15 = findViewById(R.id.content_ripple_15);
        check15 = findViewById(R.id.content_check_15);
        ripple15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check15.setChecked(!check15.isChecked());
            }
        });
        check15.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("UnNfPHcUpUyF8nZd6Vjs2kg3BEbPdX")){
                        list.add("UnNfPHcUpUyF8nZd6Vjs2kg3BEbPdX");
                    }
                }
                else{
                    list.remove("UnNfPHcUpUyF8nZd6Vjs2kg3BEbPdX");
                }
                checkIt();
            }
        });


        ripple16 = findViewById(R.id.content_ripple_16);
        check16 = findViewById(R.id.content_check_16);
        ripple16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check16.setChecked(!check16.isChecked());
            }
        });
        check16.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("3c9DPnT8ZNGnQ3pzUG474XvhHHZvzN")){
                        list.add("3c9DPnT8ZNGnQ3pzUG474XvhHHZvzN");
                    }
                }
                else{
                    list.remove("3c9DPnT8ZNGnQ3pzUG474XvhHHZvzN");
                }
                checkIt();
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void checkIt(){
        if(next!=null && list !=null){
            if(list.size()>=1){
                next.setVisibility(View.VISIBLE);
            }
            else{
                next.setVisibility(View.INVISIBLE);
            }
        }
    }
}
