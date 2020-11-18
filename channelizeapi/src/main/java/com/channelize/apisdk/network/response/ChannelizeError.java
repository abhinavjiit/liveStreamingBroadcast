/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.R;

import org.json.JSONObject;

import java.util.Map;


public class ChannelizeError implements Parcelable {

    @SerializedName("statusCode")
    private int statusCode;
    @SerializedName("name")
    private String name;
    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private String code;
    private Map<String, Object> requestData;


    public ChannelizeError(int statusCode, String name, String message, String code,
                           Map<String, Object> requestParam) {
        this.statusCode = statusCode;
        this.name = name;
        this.message = message;
        this.code = code;
        this.requestData = requestParam;
    }

    public ChannelizeError(String message) {
        this.message = message;
    }

    public ChannelizeError(int statusCode, String response, Map<String, Object> requestParam) {
        this.statusCode = statusCode;
        this.requestData = requestParam;
        if (response != null && !response.isEmpty()) {
            try {
                JSONObject responseObject = new JSONObject(response);
                if (responseObject.length() > 0 && responseObject.has("error")) {
                    JSONObject errorObject = responseObject.optJSONObject("error");
                    name = errorObject.optString("name");
                    message = errorObject.optString("message");
                    code = errorObject.optString("code");
                } else {
                    message = Channelize.getInstance().getContext().getResources().getString(R.string.pm_something_went_wrong);
                }
            } catch (Exception e) {
                message = Channelize.getInstance().getContext().getResources().getString(R.string.pm_something_went_wrong);
                e.printStackTrace();
            }
        } else {
            message = Channelize.getInstance().getContext().getResources().getString(R.string.pm_something_went_wrong);
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public void setRequestData(Map<String, Object> requestData) {
        this.requestData = requestData;
    }

    public Map<String, Object> getRequestData() {
        return requestData;
    }

    public ChannelizeError(Parcel in) {
        statusCode = in.readInt();
        name = in.readString();
        message = in.readString();
        code = in.readString();
        in.readMap(this.requestData, Object.class.getClassLoader());
    }

    public static final Creator<ChannelizeError> CREATOR = new Creator<ChannelizeError>() {
        @Override
        public ChannelizeError createFromParcel(Parcel in) {
            return new ChannelizeError(in);
        }

        @Override
        public ChannelizeError[] newArray(int size) {
            return new ChannelizeError[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(statusCode);
        dest.writeString(name);
        dest.writeString(message);
        dest.writeString(code);
        dest.writeMap(this.requestData);
    }

    @Override
    public String toString() {
        return "ChannelizeError{" +
                "statusCode=" + statusCode +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", requestData=" + requestData +
                '}';
    }
}
