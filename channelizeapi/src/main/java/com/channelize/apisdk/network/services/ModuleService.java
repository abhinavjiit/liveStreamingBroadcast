/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.services;


import android.content.Context;

import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.network.api.ChannelizeOkHttpUtil;
import com.channelize.apisdk.network.response.CompletionHandler;
import com.channelize.apisdk.network.response.ListModuleResponse;


public final class ModuleService {

    // Member variables.
    private Context mContext;
    private String apiDefaultUrl;

    public ModuleService(Context context) {
        mContext = context;
        apiDefaultUrl = Channelize.getInstance().getApiDefaultUrl();
    }

    /**
     * Method to get the enabled modules.
     *
     * @param completionHandler CompletionHandler instance to return the ListModuleResponse Model.
     */
    public void getModulesInfo(final CompletionHandler<ListModuleResponse> completionHandler) {
        ChannelizeOkHttpUtil channelizeApiClient = ChannelizeOkHttpUtil.getInstance(mContext);
        channelizeApiClient.getResponse(apiDefaultUrl + "modules", ListModuleResponse.class, completionHandler);
    }

}
