package com.channelize.apisdk.network.response;

import com.google.gson.annotations.SerializedName;

public class SearchUserSetting implements GenericResponse {

    @SerializedName("id")
    private String id;

    @SerializedName("accessCallbackUrl")
    private String accessCallbackUrl;

    @SerializedName("allowUsersSearch")
    private boolean allowUsersSearch;

    public String getId() {
        return id;
    }

    public String getAccessCallbackUrl() {
        return accessCallbackUrl;
    }

    public boolean isAllowUsersSearch() {
        return allowUsersSearch;
    }
}
