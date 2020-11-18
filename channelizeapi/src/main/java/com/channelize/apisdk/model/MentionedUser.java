package com.channelize.apisdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.channelize.apisdk.network.response.GenericResponse;
import com.google.gson.annotations.SerializedName;

public class MentionedUser implements Parcelable, GenericResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("order")
    private Integer order;

    @SerializedName("wordCount")
    private Integer wordCount;

    @SerializedName("user")
    private User user;

    protected MentionedUser(Parcel in) {
        id = in.readString();
        userId = in.readString();
        if (in.readByte() == 0) {
            order = null;
        } else {
            order = in.readInt();
        }
        if (in.readByte() == 0) {
            wordCount = null;
        } else {
            wordCount = in.readInt();
        }
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<MentionedUser> CREATOR = new Creator<MentionedUser>() {
        @Override
        public MentionedUser createFromParcel(Parcel in) {
            return new MentionedUser(in);
        }

        @Override
        public MentionedUser[] newArray(int size) {
            return new MentionedUser[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getWordCount() {
        return wordCount;
    }

    public void setWordCount(Integer wordCount) {
        this.wordCount = wordCount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        if (order == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(order);
        }
        if (wordCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(wordCount);
        }
        dest.writeParcelable(user, flags);
    }
}
