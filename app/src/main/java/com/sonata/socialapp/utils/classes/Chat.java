package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("Chat")
public class Chat extends ParseObject {

    public Date getLastEdit(){
        return getDate("lastedit");
    }

    public void setLastEdit(Date date){
        put("lastedit",date);
    }

    public String getLastPoster(){
        return getString("lastposter");
    }

    public void setRead(boolean read){
        put("read",read);
    }

    public void setMessage(String message){
        put("message",message);
    }
    public boolean isRead(){
        return getBoolean("read");
    }

    public String getKey(){
        return getString("key");
    }

    public String getEncryptedMessage(){
        return getString("lastmessage");
    }


}
