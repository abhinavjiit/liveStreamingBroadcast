/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response;

import com.google.gson.annotations.SerializedName;
import com.channelize.apisdk.model.Message;

import java.util.List;

public class ListMessageResponse implements GenericResponse {

    @SerializedName("response")
    private List<Message> response;

    public List<Message> getMessages() {
        return response;
    }

    public void setMessages(List<Message> messages) {
        this.response = messages;
    }

}
