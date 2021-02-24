package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {

    public String getType() {
        return getString("type") != null ? getString("type") : "";
    }

    public void setType(String type){
        put("type",type);
    }

    public String getMessage(){
        return getString("message");
    }

    public void setMessage(String message){
        put("message",message);
    }


    public String getOwner(){
        return getString("owner");
    }

    public void setOwner(String owner){
        put("owner",owner);
    }

    public void setChat(String chat){
        put("chat",chat);
    }

}
