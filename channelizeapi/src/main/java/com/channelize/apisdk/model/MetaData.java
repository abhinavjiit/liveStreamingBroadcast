package com.channelize.apisdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.channelize.apisdk.network.response.GenericResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MetaData implements Parcelable, GenericResponse {

    @SerializedName("subId")
    private String subId;

    @SerializedName("subType")
    private String subType;

    @SerializedName("objType")
    private String objType;

    @SerializedName("objValues")
    private Object objValues=null;

    @SerializedName("subUser")
    private User user;

    @SerializedName("objUsers")
    private Object objUsers = null;


    protected MetaData(Parcel in) {
        subId = in.readString();
        subType = in.readString();
        objType = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<MetaData> CREATOR = new Creator<MetaData>() {
        @Override
        public MetaData createFromParcel(Parcel in) {
            return new MetaData(in);
        }

        @Override
        public MetaData[] newArray(int size) {
            return new MetaData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(subId);
        parcel.writeString(subType);
        parcel.writeString(objType);
        parcel.writeParcelable(user, i);
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public Object getObjValues() {
        return objValues;
    }

    public void setObjValues(Object objValues) {
        this.objValues = objValues;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Object getObjUsers() {
        return objUsers;
    }

    public void setObjUsers(Object objUsers) {
        this.objUsers = objUsers;
    }

}
