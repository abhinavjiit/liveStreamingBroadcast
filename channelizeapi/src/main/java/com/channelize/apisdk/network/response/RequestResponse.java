/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class RequestResponse implements GenericResponse, Parcelable {

    @SerializedName("isSuccessful")
    private boolean isSuccessful;

    public RequestResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    protected RequestResponse(Parcel in) {
        isSuccessful = in.readByte() != 0;
    }

    public static final Creator<RequestResponse> CREATOR = new Creator<RequestResponse>() {
        @Override
        public RequestResponse createFromParcel(Parcel in) {
            return new RequestResponse(in);
        }

        @Override
        public RequestResponse[] newArray(int size) {
            return new RequestResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isSuccessful ? 1 : 0));
    }

    @Override
    public String toString() {
        return "RequestResponse{" +
                "isSuccessful=" + isSuccessful +
                '}';
    }
}
