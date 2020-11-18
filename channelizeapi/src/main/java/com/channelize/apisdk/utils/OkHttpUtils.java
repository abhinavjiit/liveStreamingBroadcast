/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.utils;

import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.network.api.ChannelizeOkHttpUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


public class OkHttpUtils {

    public static final List<String> CANCELLED_REQUEST = new ArrayList<>();


    /**
     * Method to cancel all the api calling (Which are executing with OkHttp).
     *
     * @param tag Request tag associated with the request.
     */
    public static void cancelCallWithTag(String tag) {
        CANCELLED_REQUEST.add(tag);
        ChannelizeOkHttpUtil channelizeOkHttpUtil = ChannelizeOkHttpUtil.getInstance(Channelize.getInstance().getContext());
        for (Call call : channelizeOkHttpUtil.dispatcher().queuedCalls()) {
            if (call.request().tag() != null && call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
        for (Call call : channelizeOkHttpUtil.dispatcher().runningCalls()) {
            if (call.request().tag() != null && call.request().tag().equals(tag)) {
                call.cancel();
            }
        }
    }

    /**
     * Method to cancel all the api calling (Which are executing with OkHttp).
     *
     * @param tagList List of Request tags associated with the request.
     */
    public static void cancelMultipleCallWithTag(List<String> tagList) {
        if (!tagList.isEmpty()) {
            for (String tag : tagList) {
                cancelCallWithTag(tag);
            }
        }
    }

    /**
     * Method to get the List of cancelled requests.
     *
     * @return Returns the List of Cancelled requests tags.
     */
    public static List<String> getCancelledRequest() {
        return CANCELLED_REQUEST;
    }
}
