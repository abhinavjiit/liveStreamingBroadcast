package com.channelize.apisdk.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class LastReadAt {

    @SerializedName("lastReadAt")
    private String lastReadAt;

    public HashMap<String, String> getmLastReadHashMap() {
        return mLastReadHashMap;
    }

    public void setmLastReadHashMap(HashMap<String, String> mLastReadHashMap) {
        this.mLastReadHashMap = mLastReadHashMap;
    }

    private HashMap<String,String> mLastReadHashMap;


}
