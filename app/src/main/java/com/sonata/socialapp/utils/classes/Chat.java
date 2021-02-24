package com.sonata.socialapp.utils.classes;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Comparator;
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

    public boolean isRead(){
        return getBoolean("read");
    }

    public String getKey(){
        return getString("key");
    }

    public String getMessage(){
        return getString("lastmessage");
    }


    class SortByDate implements Comparator<Chat> {
        public int compare(Chat a, Chat b) {
            return a.getLastEdit().after(b.getLastEdit()) ? 1 : -1;
        }
    }

}
