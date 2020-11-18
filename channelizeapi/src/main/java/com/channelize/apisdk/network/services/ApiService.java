/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.services;

import android.content.Context;

import com.channelize.apisdk.network.api.ChannelizeOkHttpUtil;
import com.channelize.apisdk.network.response.CompletionHandler;

import java.util.Map;

public final class ApiService {

    // Member variables.
    private ChannelizeOkHttpUtil channelizeApiClient;


    public ApiService(Context context) {
        channelizeApiClient = ChannelizeOkHttpUtil.getInstance(context);
    }

    /**
     * Method to make the GET request.
     *
     * @param url               GET url which needs to be execute.
     * @param tag               Request tag, which needs to be associated with the network call.
     * @param queryParam        Map of query string which need to be merge into url.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getResponse(String url, String tag, Map<String, String> queryParam, Class responseClass, CompletionHandler completionHandler) {
        channelizeApiClient.getResponse(url, tag, queryParam, responseClass, completionHandler);
    }

    /**
     * Method to send the POST request to execute a url.
     *
     * @param postUrl           Post url which needs to be execute.
     * @param postParam         PostParams which needs to be added in POST request.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void sendPostRequest(String postUrl, Map<String, Object> postParam,
                                Class responseClass, CompletionHandler completionHandler) {
        channelizeApiClient.sendPostRequest(postUrl, postParam, responseClass, completionHandler);
    }

    /**
     * Method to send the PUT request to execute a url.
     *
     * @param postUrl           Put url which needs to be execute.
     * @param postParam         PostParams which needs to be added in PUT request.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void sendPutRequest(String postUrl, Map<String, Object> postParam,
                               Class responseClass, CompletionHandler completionHandler) {
        channelizeApiClient.sendPutRequest(postUrl, postParam, responseClass, completionHandler);
    }

    /**
     * Method to make the DELETE request.
     *
     * @param postUrl           Delete url which needs to be execute.
     * @param postParam         PostParams which needs to be added in DELETE request.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void sendDeleteRequest(String postUrl, Map<String, Object> postParam,
                                  Class responseClass, CompletionHandler completionHandler) {
        channelizeApiClient.sendDeleteRequest(postUrl, postParam, responseClass, completionHandler);
    }

}
