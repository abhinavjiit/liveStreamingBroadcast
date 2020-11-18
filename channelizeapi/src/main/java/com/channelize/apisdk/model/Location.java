/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Location implements Parcelable {

    // Member variables.
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("title")
    private String locationTitle;
    @SerializedName("address")
    private String locationDescription;
    @SerializedName("locationImageUrl")
    private String locationImageUrl;

    public Location(double latitude, double longitude, String locationTitle,
                    String locationDescription) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationTitle = locationTitle;
        this.locationDescription = locationDescription;
    }

    protected Location(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        locationTitle = in.readString();
        locationDescription = in.readString();
        locationImageUrl = in.readString();
    }

    public static final Creator<Location> CREATOR = new Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getLocationImageUrl() {
        return locationImageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(locationTitle);
        dest.writeString(locationDescription);
        dest.writeString(locationImageUrl);
    }
}
