/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MessageRecipient implements Parcelable {

    // Member variables.
    @SerializedName("recipientId")
    private String recipientId;
    @SerializedName("createdAt")
    private String createdAt;
    @SerializedName("status")
    private int status;

    protected MessageRecipient(Parcel in) {
        recipientId = in.readString();
        createdAt = in.readString();
        status = in.readInt();
    }

    public static final Creator<MessageRecipient> CREATOR = new Creator<MessageRecipient>() {
        @Override
        public MessageRecipient createFromParcel(Parcel in) {
            return new MessageRecipient(in);
        }

        @Override
        public MessageRecipient[] newArray(int size) {
            return new MessageRecipient[size];
        }
    };

    public String getRecipientId() {
        return recipientId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(recipientId);
        dest.writeString(createdAt);
        dest.writeInt(status);
    }
}
