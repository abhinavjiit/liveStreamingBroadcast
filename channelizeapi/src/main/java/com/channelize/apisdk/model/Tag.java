/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class Tag implements Parcelable {

    // Member variables.
    @SerializedName("userId")
    private String userId;
    @SerializedName("wordCount")
    private int wordCount;
    @SerializedName("order")
    private int order;

    public Tag(JSONObject jsonObject) {
        this.userId = jsonObject.optString("userId");
        this.wordCount = jsonObject.optInt("wordCount");
        this.order = jsonObject.optInt("order");
    }

    public String getUserId() {
        return userId;
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getOrder() {
        return order;
    }

    protected Tag(Parcel in) {
        userId = in.readString();
        wordCount = in.readInt();
        order = in.readInt();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeInt(wordCount);
        dest.writeInt(order);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "userId='" + userId + '\'' +
                ", wordCount=" + wordCount +
                ", order=" + order +
                '}';
    }
}
