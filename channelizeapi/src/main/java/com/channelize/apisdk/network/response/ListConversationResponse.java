/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response;

import com.google.gson.annotations.SerializedName;
import com.channelize.apisdk.model.Conversation;

import java.util.List;

public class ListConversationResponse implements GenericResponse {

    @SerializedName("response")
    private List<Conversation> response;

    public List<Conversation> getConversation() {
        return response;
    }

    public void setConversation(List<Conversation> conversation) {
        this.response = conversation;
    }

}
