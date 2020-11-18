/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.channelize.apisdk.network.response.GenericResponse;
import com.google.gson.annotations.SerializedName;


public class Module implements Parcelable, GenericResponse {

    // Member variables.
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("identifier")
    private String identifier;
    @SerializedName("enabled")
    private boolean enabled;

    public Module(String identifier) {
        this.identifier = identifier;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected Module(Parcel in) {
        id = in.readString();
        name = in.readString();
        identifier = in.readString();
        enabled = in.readByte() != 0;
    }

    public static final Creator<Module> CREATOR = new Creator<Module>() {
        @Override
        public Module createFromParcel(Parcel in) {
            return new Module(in);
        }

        @Override
        public Module[] newArray(int size) {
            return new Module[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(identifier);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Module module = (Module) object;
            if (this.identifier.equals(module.getIdentifier())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.identifier.hashCode();
        return hash;
    }
}
