/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response;


import com.google.gson.annotations.SerializedName;
import com.channelize.apisdk.model.User;

public class LoginResponse implements GenericResponse {


    @SerializedName("id")
    private String accessToken;
    @SerializedName("userId")
    private String userId;
    @SerializedName("user")
    private User user;

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }
}
