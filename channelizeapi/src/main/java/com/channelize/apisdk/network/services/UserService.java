/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.services;

import android.content.Context;

import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.model.User;
import com.channelize.apisdk.network.api.ChannelizeOkHttpUtil;
import com.channelize.apisdk.ApiConstants;
import com.channelize.apisdk.network.response.CompletionHandler;
import com.channelize.apisdk.network.response.RequestResponse;
import com.channelize.apisdk.network.response.ListUserResponse;
import com.channelize.apisdk.network.response.SearchUserSetting;
import com.channelize.apisdk.network.response.TotalCountResponse;
import com.channelize.apisdk.network.response.UserBlockStatusResponse;
import com.channelize.apisdk.network.services.query.UserQuery;

import java.util.HashMap;
import java.util.Map;


public final class UserService {

    // Member variables.
    private String selfUid, apiDefaultUrl;
    private ChannelizeOkHttpUtil channelizeOkHttpUtil;


    public UserService(Context context) {
        Channelize channelize = Channelize.getInstance();
        selfUid = channelize.getCurrentUserId();
        apiDefaultUrl = channelize.getApiDefaultUrl();
        channelizeOkHttpUtil = ChannelizeOkHttpUtil.getInstance(context);
    }

    /**
     * Method to get the user details from the user id.
     *
     * @param id                Id of the user.
     * @param completionHandler CompletionHandler instance to return the User Model.
     */
    public void getUser(String id, final CompletionHandler<User> completionHandler) {
        channelizeOkHttpUtil.getResponse(apiDefaultUrl + "users/" + id,
                User.class, completionHandler);
    }

    /***
     * Method to get the blocked user's list which are blocked by the logged in user.
     * @param userQuery            UserQuery, which contains all the filter that need to be apply for blocked list fetching.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getBlockedList(UserQuery userQuery, final CompletionHandler<ListUserResponse> completionHandler) {
        channelizeOkHttpUtil.getResponse(apiDefaultUrl + "users/blocks", userQuery.getUserQueryParams(),
                ListUserResponse.class, completionHandler);
    }

    /**
     * Method to get the total block users count of the logged in user.
     *
     * @param userQuery         UserQuery, which contains all the filter that need to be apply for blocked count fetching.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getBlocksCount(UserQuery userQuery, final CompletionHandler<TotalCountResponse> completionHandler) {
        channelizeOkHttpUtil.getResponse(apiDefaultUrl + "users/blocks/count",
                userQuery.getUserQueryParams(), TotalCountResponse.class, completionHandler);
    }

    /**
     * Method to get the total friends count of the logged in user.
     *
     * @param userQuery         UserQuery, which contains all the filter that need to be apply for friends count fetching.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getFriendsCount(UserQuery userQuery, final CompletionHandler<TotalCountResponse> completionHandler) {
        channelizeOkHttpUtil.getResponse(apiDefaultUrl + "users/friends/count",
                userQuery.getUserQueryParams(), TotalCountResponse.class, completionHandler);
    }

    /**
     * Method to get the all friends of the logged in user with the all filters.
     *
     * @param userQuery         UserQuery, which contains all the filter that need to be apply for friend list fetching.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getFriendsList(UserQuery userQuery, final CompletionHandler<ListUserResponse> completionHandler) {
        String requestTag = null;
        if (userQuery.getUserQueryParams().containsKey("search")) {
            requestTag = ApiConstants.CONTACT_SEARCH + userQuery.getUserQueryParams().get("search");
        }
        channelizeOkHttpUtil.getResponse(apiDefaultUrl + "users/friends", requestTag,
                userQuery.getUserQueryParams(), ListUserResponse.class, completionHandler);
    }

    public void getSearchAllUsersSettings(final CompletionHandler<SearchUserSetting> completionHandler) {
        channelizeOkHttpUtil.getResponse(apiDefaultUrl + "api_security", null,
                null, SearchUserSetting.class, completionHandler);
    }

    public void getAllUserList(UserQuery userQuery, final CompletionHandler<ListUserResponse> completionHandler) {
        String requestTag = null;
        if (userQuery.getUserQueryParams().containsKey("search")) {
            requestTag = ApiConstants.CONTACT_SEARCH + userQuery.getUserQueryParams().get("search");
        }
        channelizeOkHttpUtil.getResponse(apiDefaultUrl + "users", requestTag,
                userQuery.getUserQueryParams(), ListUserResponse.class, completionHandler);
    }



    /**
     * Method to get the block status between logged-in user and receiver.
     *
     * @param recipientId       User id of the receiver.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void checkBlockStatus(String recipientId, final CompletionHandler<UserBlockStatusResponse> completionHandler) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("userId", recipientId);
        channelizeOkHttpUtil.sendPostRequest(apiDefaultUrl + "users/check_block_status", postParam, UserBlockStatusResponse.class, completionHandler);
    }

    public void logoutFromChannelize(final CompletionHandler<RequestResponse> completionHandler) {
        /*Map<String, Object> postParam = new HashMap<>();
        postParam.put("deviceId", deviceId);*/
        channelizeOkHttpUtil.sendPostRequest(apiDefaultUrl + "users/logout", null, RequestResponse.class, completionHandler);
    }

    /**
     * Method to make an user friend with the logged-in user.
     *
     * @param userId            UserId of the user with whom you want to connect as friend.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void addFriend(String userId, CompletionHandler<RequestResponse> completionHandler) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("userId", userId);
        channelizeOkHttpUtil.sendPostRequest(apiDefaultUrl + "users/add_friend",
                postParam, RequestResponse.class, completionHandler);
    }

    /**
     * Method to remove an user from the friend list of logged-in user.
     *
     * @param userId            UserId of the user.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void removeFriend(String userId, CompletionHandler<RequestResponse> completionHandler) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("userId", userId);
        channelizeOkHttpUtil.sendPostRequest(apiDefaultUrl + "users/remove_friend",
                postParam, RequestResponse.class, completionHandler);
    }

    /**
     * @param userId            UserId of the user  who needs to be block/unblock.
     * @param isBlock           True, if need to block otherwise unblock.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void blockUnBlockUser(String userId, boolean isBlock, CompletionHandler<RequestResponse> completionHandler) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        String actionUrl = apiDefaultUrl;
        if (isBlock) {
            actionUrl += "users/block";
        } else {
            actionUrl += "users/unblock";
        }
        channelizeOkHttpUtil.sendPostRequest(actionUrl, param, RequestResponse.class, completionHandler);
    }

    /**
     * Method to set logged-in user as online.
     *
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void setUserOnline(CompletionHandler<User> completionHandler) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("isOnline", true);
        /*channelizeOkHttpUtil.sendPutRequest(apiDefaultUrl + "users/" + selfUid,
                postParam, User.class, completionHandler);*/
        channelizeOkHttpUtil.sendPutRequest(apiDefaultUrl + "users/" + Channelize.getInstance().getCurrentUserId(),
                postParam, User.class, completionHandler);
    }

    /**
     * Method to set logged-in user as offline.
     *
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void setUserOffline(CompletionHandler<User> completionHandler) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("isOnline", false);
        /*channelizeOkHttpUtil.sendPutRequest(apiDefaultUrl + "users/" + selfUid,
                postParam, User.class, completionHandler);*/
        channelizeOkHttpUtil.sendPutRequest(apiDefaultUrl + "users/" + Channelize.getInstance().getCurrentUserId(),
                postParam, User.class, completionHandler);
    }

    /**
     * Method to update the setting of logged-in user.
     *
     * @param userQuery         UserQuery, which contains all the filter that need to be apply to update user's info.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void updateUserInfo(UserQuery userQuery, CompletionHandler<User> completionHandler) {
        Map<String, Object> param = new HashMap<>(userQuery.getUserQueryParams());
        /*channelizeOkHttpUtil.sendPutRequest(apiDefaultUrl + "users/" + selfUid,
                param, User.class, completionHandler);*/
        channelizeOkHttpUtil.sendPutRequest(apiDefaultUrl + "users/" + Channelize.getInstance().getCurrentUserId(),
                param, User.class, completionHandler);
    }
}
