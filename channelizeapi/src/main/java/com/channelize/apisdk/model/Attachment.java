/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.model;


import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;

import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.R;
import com.channelize.apisdk.network.response.GenericResponse;
import com.google.gson.annotations.SerializedName;

import java.io.File;


public class Attachment implements Parcelable, GenericResponse {

    // Member variables.

    @SerializedName("type")
    private String type;//Okay

    @SerializedName("downsampledUrl")
    private String downsampledUrl;//Okay

    @SerializedName("originalUrl")
    private String originalUrl;//Okay

    @SerializedName("stillUrl")
    private String stillUrl;//Okay

    @SerializedName("latitude")
    private Double latitude;//Okay

    @SerializedName("longitude")
    private Double longitude;//Okay

    @SerializedName("address")
    private String address;//Okay

    @SerializedName("title")
    private String title;//Okay

    @SerializedName("name")
    private String name;//Okay

    @SerializedName("mimeType")
    private String mimeType;//Okay

    @SerializedName("extension")
    private String extension;//Okay

    @SerializedName("size")
    private String size;//Okay

    @SerializedName("duration")
    private float duration;//Okay

    @SerializedName("thumbnailUrl")
    private String thumbnailUrl;//Okay

    @SerializedName("fileUrl")
    private String fileUrl;//Okay

    @SerializedName("filePath")
    private String filePath;//Okay

    @SerializedName("mapUrl")
    private String mapUrl;

    @SerializedName("adminMessageType")
    private String adminMessageType;

    @SerializedName("metaData")
    private MetaData metaData;

    public Attachment(){

    }


    protected Attachment(Parcel in) {
        type = in.readString();
        downsampledUrl = in.readString();
        originalUrl = in.readString();
        stillUrl = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        address = in.readString();
        title = in.readString();
        name = in.readString();
        mimeType = in.readString();
        extension = in.readString();
        size = in.readString();
        duration = in.readFloat();
        thumbnailUrl = in.readString();
        fileUrl = in.readString();
        filePath = in.readString();
        mapUrl = in.readString();
        adminMessageType = in.readString();
        metaData = in.readParcelable(MetaData.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(downsampledUrl);
        dest.writeString(originalUrl);
        dest.writeString(stillUrl);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        dest.writeString(address);
        dest.writeString(title);
        dest.writeString(name);
        dest.writeString(mimeType);
        dest.writeString(extension);
        dest.writeString(size);
        dest.writeFloat(duration);
        dest.writeString(thumbnailUrl);
        dest.writeString(fileUrl);
        dest.writeString(filePath);
        dest.writeString(mapUrl);
        dest.writeString(adminMessageType);
        dest.writeParcelable(metaData, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Attachment> CREATOR = new Creator<Attachment>() {
        @Override
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        @Override
        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDownsampledUrl() {
        return downsampledUrl;
    }

    public void setDownsampledUrl(String downsampledUrl) {
        this.downsampledUrl = downsampledUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getStillUrl() {
        return stillUrl;
    }

    public void setStillUrl(String stillUrl) {
        this.stillUrl = stillUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAdminMessageType() {
        return adminMessageType;
    }

    public void setAdminMessageType(String adminMessageType) {
        this.adminMessageType = adminMessageType;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }
}
