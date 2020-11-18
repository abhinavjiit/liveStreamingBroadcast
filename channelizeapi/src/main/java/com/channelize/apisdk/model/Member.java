/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.channelize.apisdk.Utils;
import com.channelize.apisdk.network.response.GenericResponse;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Member implements Parcelable, GenericResponse {

    // Member variables.
    @SerializedName("id")
    private String id;//Okay

    @SerializedName("userId")
    private String userId;//Okay

    @SerializedName("isAdmin")
    private boolean isAdmin;//Okay

    @SerializedName("user")
    private User user;//Okay


    @SerializedName("lastMessage")
    private Message message;
    @SerializedName("isActive")
    private boolean isActive;
    @SerializedName("isTyping")
    private boolean isTyping;
    @SerializedName("mute")
    private boolean isMuted;
    @SerializedName("newMessageCount")
    private int newMessageCount;
    @SerializedName("updatedAt")
    private String lastUpdatedTime;
    private String groupUpdatedTime;
    private long timeStamp;


    public Member() {
    }

    protected Member(Parcel in) {
        id = in.readString();
        userId = in.readString();
        isAdmin = in.readByte() != 0;
        user = in.readParcelable(User.class.getClassLoader());
        message = in.readParcelable(Message.class.getClassLoader());
        isActive = in.readByte() != 0;
        isTyping = in.readByte() != 0;
        isMuted = in.readByte() != 0;
        newMessageCount = in.readInt();
        lastUpdatedTime = in.readString();
        groupUpdatedTime = in.readString();
        timeStamp = in.readLong();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    // Method to set the chat time for the recent conversation screen as well as for the group conversation screen.
    public void setChatTime() {
        // Checking if the updatedAt time is available for the chat or not.
        if (lastUpdatedTime != null && !lastUpdatedTime.isEmpty()
                && !lastUpdatedTime.equals("null")) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                        Locale.getDefault());
                Date date = format.parse(lastUpdatedTime.replaceAll("Z$", "+0000"));
                timeStamp = date.getTime();
                lastUpdatedTime = Utils.getFormattedTime(timeStamp);
                groupUpdatedTime = Utils.getRelativeTimeString(timeStamp);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public String getGroupUpdatedTime() {
        return groupUpdatedTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        lastUpdatedTime = Utils.getFormattedTime(timeStamp);
    }

    public int getNewMessageCount() {
        return newMessageCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getUserId() {
        return userId != null && !userId.isEmpty() && !userId.equals("null")
                ? userId : "";
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNewMessageCount(int newMessageCount) {
        this.newMessageCount = newMessageCount;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public void setGroupUpdatedTime(String groupUpdatedTime) {
        this.groupUpdatedTime = groupUpdatedTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeByte((byte) (isAdmin ? 1 : 0));
        dest.writeParcelable(user, flags);
        dest.writeParcelable(message, flags);
        dest.writeByte((byte) (isActive ? 1 : 0));
        dest.writeByte((byte) (isTyping ? 1 : 0));
        dest.writeByte((byte) (isMuted ? 1 : 0));
        dest.writeInt(newMessageCount);
        dest.writeString(lastUpdatedTime);
        dest.writeString(groupUpdatedTime);
        dest.writeLong(timeStamp);
    }
}
