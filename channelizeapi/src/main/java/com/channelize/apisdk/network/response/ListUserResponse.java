/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response;


import com.google.gson.annotations.SerializedName;
import com.channelize.apisdk.model.User;

import java.util.List;

public class ListUserResponse implements GenericResponse {

    @SerializedName("response")
    private List<User> response;

    public List<User> getUsers() {
        return response;
    }

    public void setUsers(List<User> users) {
        this.response = users;
    }

}
