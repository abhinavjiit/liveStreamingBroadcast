/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class TotalCountResponse implements GenericResponse, Parcelable {

    @SerializedName(value="count", alternate={"newMessageCount"})
    private int count;

    protected TotalCountResponse(Parcel in) {
        count = in.readInt();
    }

    public static final Creator<TotalCountResponse> CREATOR = new Creator<TotalCountResponse>() {
        @Override
        public TotalCountResponse createFromParcel(Parcel in) {
            return new TotalCountResponse(in);
        }

        @Override
        public TotalCountResponse[] newArray(int size) {
            return new TotalCountResponse[size];
        }
    };

    public int getCount() {
        return count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(count);
    }

    @Override
    public String toString() {
        return "TotalCountResponse{" +
                "count=" + count +
                '}';
    }
}
