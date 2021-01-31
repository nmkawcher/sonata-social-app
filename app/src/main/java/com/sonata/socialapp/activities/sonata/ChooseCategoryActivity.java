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

    RelativeLayout ripple0, ripple1, ripple2, ripple3, ripple4, ripple5, ripple6, ripple7, ripple8, ripple9
            , ripple10, ripple11, ripple12, ripple13, ripple14, ripple15, ripple16, next;

    CheckBox check0, check1, check2, check3, check4, check5, check6, check7, check8, check9
            , check10, check11, check12, check13, check14, check15, check16;

    ArrayList<String> list;
    Button nextButton;
    TextView skip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(GenelUtil.getNightMode()){
            setTheme(R.style.ThemeNight);
        }else{
            setTheme(R.style.ThemeDay);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        list = new ArrayList<String>();
        next = findViewById(R.id.nexttextripple);
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
        /*nextButton = findViewById(R.id.saveButton);
        skip = findViewById(R.id.skipButton);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseCategoryActivity.this,FollowSuggestActivity.class));
                finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(ChooseCategoryActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("list",list);
                ParseCloud.callFunctionInBackground("newRegisterCategoryChoose",hashMap);
                startActivity(new Intent(ChooseCategoryActivity.this,FollowSuggestActivity.class)
                        .putStringArrayListExtra("list",list));
            }
        });
        */
        ripple0 = findViewById(R.id.content_ripple_0);
        check0 = findViewById(R.id.content_check_0);
        ripple0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check0.setChecked(!check0.isChecked());
            }
        });
        check0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("3Chefq52Ky5wBFbG8E74eRMksnqdFk")){
                        list.add("3Chefq52Ky5wBFbG8E74eRMksnqdFk");
                    }
                    if(!list.contains("yLSwFZHnSDzM399mxuPEhKTvNq2jBr")){
                        list.add("yLSwFZHnSDzM399mxuPEhKTvNq2jBr");
                    }
                }
                else{
                    list.remove("3Chefq52Ky5wBFbG8E74eRMksnqdFk");
                    list.remove("yLSwFZHnSDzM399mxuPEhKTvNq2jBr");
                }
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
                    if(!list.contains("th2CCLbLhuXXgHFaqSq6r4yWL9m4rQ")){
                        list.add("th2CCLbLhuXXgHFaqSq6r4yWL9m4rQ");
                    }
                    if(!list.contains("2Ume5eqU5YAkG2tLV7teGYyWGNCQLZ")){
                        list.add("2Ume5eqU5YAkG2tLV7teGYyWGNCQLZ");
                    }
                }
                else{
                    list.remove("tx4bJH6tWkT6MgFwHm2GZz6BcAcdFC");
                    list.remove("th2CCLbLhuXXgHFaqSq6r4yWL9m4rQ");
                    list.remove("2Ume5eqU5YAkG2tLV7teGYyWGNCQLZ");
                }
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
                    if(!list.contains("A6xmzuLXXd5J6ZD27RS2ddUzc2yx6Y")){
                        list.add("A6xmzuLXXd5J6ZD27RS2ddUzc2yx6Y");
                    }
                }
                else{
                    list.remove("5aT7qyvEVTEf3aTFNuvUwmR9HFwnVq");
                    list.remove("A6xmzuLXXd5J6ZD27RS2ddUzc2yx6Y");
                }
            }
        });


        ripple9 = findViewById(R.id.content_ripple_9);
        check9 = findViewById(R.id.content_check_9);
        ripple9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check9.setChecked(!check9.isChecked());
            }
        });
        check9.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("bcytkHHQPpUrBgAUdSD2E9FeTc4kew")){
                        list.add("bcytkHHQPpUrBgAUdSD2E9FeTc4kew");
                    }
                }
                else{
                    list.remove("bcytkHHQPpUrBgAUdSD2E9FeTc4kew");
                }
            }
        });


        ripple10 = findViewById(R.id.content_ripple_10);
        check10 = findViewById(R.id.content_check_10);
        ripple10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check10.setChecked(!check10.isChecked());
            }
        });
        check10.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("TLd6thGJeFAbE9Ru7xqVvjwVvEy3Lw")){
                        list.add("TLd6thGJeFAbE9Ru7xqVvjwVvEy3Lw");
                    }
                }
                else{
                    list.remove("TLd6thGJeFAbE9Ru7xqVvjwVvEy3Lw");
                }
            }
        });


        ripple11 = findViewById(R.id.content_ripple_11);
        check11 = findViewById(R.id.content_check_11);
        ripple11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check11.setChecked(!check11.isChecked());
            }
        });
        check11.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("6AzNkNttmGN2hHwzGGRDYWnjtYKFPN")){
                        list.add("6AzNkNttmGN2hHwzGGRDYWnjtYKFPN");
                    }
                }
                else{
                    list.remove("6AzNkNttmGN2hHwzGGRDYWnjtYKFPN");
                }
            }
        });


        ripple12 = findViewById(R.id.content_ripple_12);
        check12 = findViewById(R.id.content_check_12);
        ripple12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check12.setChecked(!check12.isChecked());
            }
        });
        check12.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("f37HWXBUGXuqw8gG5uxAB3ECRWrYAz")){
                        list.add("f37HWXBUGXuqw8gG5uxAB3ECRWrYAz");
                    }
                }
                else{
                    list.remove("f37HWXBUGXuqw8gG5uxAB3ECRWrYAz");
                }
            }
        });


        ripple13 = findViewById(R.id.content_ripple_13);
        check13 = findViewById(R.id.content_check_13);
        ripple13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check13.setChecked(!check13.isChecked());
            }
        });
        check13.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("9G3v9X2BC8gjVTj2N8QqCyemLcMdhB")){
                        list.add("9G3v9X2BC8gjVTj2N8QqCyemLcMdhB");
                    }
                }
                else{
                    list.remove("9G3v9X2BC8gjVTj2N8QqCyemLcMdhB");
                }
            }
        });


        ripple14 = findViewById(R.id.content_ripple_14);
        check14 = findViewById(R.id.content_check_14);
        ripple14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check14.setChecked(!check14.isChecked());
            }
        });
        check14.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!list.contains("JAPSBPK4tHumcMQjA2sDR2bgC6dudN")){
                        list.add("JAPSBPK4tHumcMQjA2sDR2bgC6dudN");
                    }
                }
                else{
                    list.remove("JAPSBPK4tHumcMQjA2sDR2bgC6dudN");
                }
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
            }
        });


    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
