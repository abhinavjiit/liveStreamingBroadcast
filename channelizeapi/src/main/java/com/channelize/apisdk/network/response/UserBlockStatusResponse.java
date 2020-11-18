/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response;


import com.google.gson.annotations.SerializedName;

public class UserBlockStatusResponse implements GenericResponse {


    @SerializedName("loginUserBlockedRecipient")
    private boolean loginUserBlockedRecipient;
    @SerializedName("recipientblockedLoginUser")
    private boolean recipientBlockedLoginUser;

    public boolean isLoginUserBlockedRecipient() {
        return loginUserBlockedRecipient;
    }

    public void setLoginUserBlockedRecipient(boolean loginUserBlockedRecipient) {
        this.loginUserBlockedRecipient = loginUserBlockedRecipient;
    }

    public boolean isRecipientBlockedLoginUser() {
        return recipientBlockedLoginUser;
    }

    public void setRecipientBlockedLoginUser(boolean recipientBlockedLoginUser) {
        this.recipientBlockedLoginUser = recipientBlockedLoginUser;
    }
}
