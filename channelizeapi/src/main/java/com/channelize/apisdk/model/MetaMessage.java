/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class MetaMessage implements Parcelable {

    // Member variables.
    @SerializedName("subId")
    private String ownerId;
    @SerializedName("objValues")
    private Object objValues;

    protected MetaMessage(Parcel in) {
        ownerId = in.readString();
    }

    public static final Creator<MetaMessage> CREATOR = new Creator<MetaMessage>() {
        @Override
        public MetaMessage createFromParcel(Parcel in) {
            return new MetaMessage(in);
        }

        @Override
        public MetaMessage[] newArray(int size) {
            return new MetaMessage[size];
        }
    };

    public String getOwnerId() {
        return ownerId;
    }

    public Object getObjValues() {
        return objValues;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ownerId);
    }
}
