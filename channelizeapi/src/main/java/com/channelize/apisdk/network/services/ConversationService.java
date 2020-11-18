/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.services;


import android.content.Context;

import com.channelize.apisdk.ApiConstants;
import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.model.Conversation;
import com.channelize.apisdk.model.Member;
import com.channelize.apisdk.network.api.ChannelizeOkHttpUtil;
import com.channelize.apisdk.network.response.ChannelizeError;
import com.channelize.apisdk.network.response.CompletionHandler;
import com.channelize.apisdk.network.response.ListConversationResponse;
import com.channelize.apisdk.network.response.RequestResponse;
import com.channelize.apisdk.network.response.ListMemberResponse;
import com.channelize.apisdk.network.response.ListMessageResponse;
import com.channelize.apisdk.network.response.TotalCountResponse;
import com.channelize.apisdk.network.services.query.ConversationQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class ConversationService {

    // Member variables.
    private String selfUid, apiDefaultUrl;
    private ChannelizeOkHttpUtil channelizeApiClient;


    public ConversationService(Context context) {
        Channelize channelize = Channelize.getInstance();
        selfUid = channelize.getCurrentUserId();
        apiDefaultUrl = channelize.getApiDefaultUrl();
        channelizeApiClient = ChannelizeOkHttpUtil.getInstance(context);
    }

    /**
     * Method to get the total conversation count of the logged in user.
     *
     * @param conversationQuery ConversationQuery, which contains all the filter that need to be apply for conversation count fetching.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getConversationsCount(ConversationQuery conversationQuery, final CompletionHandler<TotalCountResponse> completionHandler) {
        channelizeApiClient.getResponse(apiDefaultUrl + "conversations/count", conversationQuery.getConversationQueryParams(),
                TotalCountResponse.class, completionHandler);
    }

    /**
     * Method to get the total message count of a conversation.
     *
     * @param conversationId    Id of the conversation for which messages count need to be fetched.
     * @param conversationQuery ConversationQuery, which contains all the filter that need to be apply for messages count fetching.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getTotalMessageCount(final String conversationId, ConversationQuery conversationQuery, final CompletionHandler<TotalCountResponse> completionHandler) {
        channelizeApiClient.getResponse(apiDefaultUrl + "conversations/" + conversationId
                        + "/messages/count", conversationQuery.getConversationQueryParams(),
                TotalCountResponse.class, completionHandler);
    }

    /**
     * Method to get the members list of a conversation.
     *
     * @param conversationId    ConversationId for which members list needs to be fetched.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getMembersList(String conversationId, final CompletionHandler<ListMemberResponse> completionHandler) {
        channelizeApiClient.getResponse(apiDefaultUrl + "conversations/" + conversationId
                + "/members", ListMemberResponse.class, completionHandler);
    }


    /**
     * Method to get the all conversations of the logged in user with the all filters.
     *
     * @param conversationQuery ConversationQuery, which contains all the filter that need to be apply for conversation fetching.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getConversationList(ConversationQuery conversationQuery, final CompletionHandler<ListConversationResponse> completionHandler) {
        String requestTag = null;
        if (conversationQuery.getConversationQueryParams() != null &&
                conversationQuery.getConversationQueryParams().containsKey("search")) {
            requestTag = ApiConstants.GROUP_SEARCH + conversationQuery.getConversationQueryParams().get("search");
        }
        channelizeApiClient.getResponse(apiDefaultUrl + "conversations", requestTag,
                conversationQuery.getConversationQueryParams(),
                ListConversationResponse.class, completionHandler);
    }

    /**
     * Method to get the all messages of a conversation with the all filters.
     *
     * @param conversationId    Id of the conversation for which messages need to be fetched.
     * @param conversationQuery ConversationQuery, which contains all the filter that need to be apply for message fetching.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getMessages(String conversationId, ConversationQuery conversationQuery, final CompletionHandler<ListMessageResponse> completionHandler) {
        channelizeApiClient.getResponse(apiDefaultUrl + "conversations/" + conversationId
                        + "/messages", conversationQuery.getConversationQueryParams(),
                ListMessageResponse.class, completionHandler);
    }

    /**
     * Method used to make a conversation muted/un-muted.
     *
     * @param conversationId    Conversation Id of the conversation which needs to be marked as mute/unmute.
     * @param mute              True, if need to make conversation as muted otherwise false.
     * @param completionHandler Completion handler of the request.
     */
    public void muteUnmuteConversation(String conversationId, boolean mute, CompletionHandler<RequestResponse> completionHandler) {
        String actionUrl = apiDefaultUrl + "conversations/" + conversationId + "/update_settings";
       /* JSONObject muteObject = new JSONObject();
        try {
            muteObject.put("mute", mute);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        Map<String, Object> param = new HashMap<>();
        param.put("mute", mute);
        channelizeApiClient.sendPutRequest(actionUrl, param, RequestResponse.class, completionHandler);
    }

    /**
     * Method used to mark all messages of a conversation as read.
     *
     * @param conversationId    ConversationId of the conversation in which all messages needs to be marked as read.
     * @param completionHandler Completion handler of the request.
     */
    public void markAllMessageRead(String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        String actionUrl = apiDefaultUrl + "conversations/" + conversationId + "/mark_as_read";
        channelizeApiClient.sendPutRequest(actionUrl, null, RequestResponse.class, completionHandler);
    }

    /**
     * Method used to delete the complete conversation.
     *
     * @param conversationId    ConversationId of the conversation which needs to be deleted.
     * @param completionHandler Completion handler of the request.
     */
    public void deleteConversation(String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        String actionUrl = apiDefaultUrl + "conversations/" + conversationId + "/delete";
        channelizeApiClient.sendDeleteRequest(actionUrl, null, RequestResponse.class, completionHandler);
    }

    /**
     * Method used to clear all the messages of a conversation.
     *
     * @param conversationId    ConversationId of the conversation in which all messages needs to be cleared.
     * @param completionHandler Completion handler of the request.
     */
    public void clearConversation(String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        String actionUrl = apiDefaultUrl + "conversations/" + conversationId + "/clear";
        channelizeApiClient.sendDeleteRequest(actionUrl, null, RequestResponse.class, completionHandler);
    }

    /**
     * Method used to create a group with the title and members id.
     *
     * @param title             Title of the group.
     * @param membersArray      Ids of the user with whom the group has been made.
     * @param completionHandler Completion handler of the request.
     */
    public void createGroup(String title, JSONArray membersArray, CompletionHandler<Conversation> completionHandler) {
        Map<String, Object> param = new HashMap<>();
        param.put("title", title);
        param.put("isGroup", true);
        //param.put("ownerId", selfUid);
        param.put("ownerId", Channelize.getInstance().getCurrentUserId());
        param.put("members", membersArray);
        channelizeApiClient.sendPostRequest(apiDefaultUrl + "conversations", param, Conversation.class, completionHandler);
    }

    public void createOneToOneChat(JSONArray membersArray, CompletionHandler<Conversation> completionHandler) {
        Map<String, Object> param = new HashMap<>();
        param.put("isGroup", false);
        param.put("members", membersArray);
        channelizeApiClient.sendPostRequest(apiDefaultUrl + "conversations", param, Conversation.class, completionHandler);
    }

    /**
     * Method used to add members to an existing group.
     *
     * @param conversationId    ConversationId of the conversation in which members needs to be added.
     * @param membersArray      Ids of the users which you wanted to add in the group.
     * @param completionHandler Completion handler of the request.
     */
    public void addMembersToGroup(String conversationId, JSONArray membersArray,
                                  CompletionHandler<RequestResponse> completionHandler) {
        String actionUrl = apiDefaultUrl + "conversations/" + conversationId + "/add_members";
        Map<String, Object> param = new HashMap<>();
        param.put("members", membersArray);
        channelizeApiClient.sendPostRequest(actionUrl, param, RequestResponse.class, completionHandler);
    }

    /**
     * Method used to remove the members from a group.
     *
     * @param conversationId    ConversationId of the conversation in which member needs to be removed.
     * @param membersArray      Ids of the users which you wanted to remove from the group.
     * @param completionHandler Completion handler of the request.
     */
    public void removeMemberFromGroup(String conversationId, JSONArray membersArray,
                                      CompletionHandler<RequestResponse> completionHandler) {
        String actionUrl = apiDefaultUrl + "conversations/" + conversationId + "/remove_members";
        Map<String, Object> param = new HashMap<>();
        param.put("members", membersArray);
        channelizeApiClient.sendPostRequest(actionUrl, param, RequestResponse.class, completionHandler);
    }

    /**
     * Method used to make a user as admin in the group.
     *
     * @param conversationId    ConversationId of the conversation in which a user has been promoted to the admin.
     * @param userId            UserId of the group member who needs to be promoted as admin.
     * @param completionHandler Completion handler of the request.
     */
    public void makeAdmin(String conversationId, String userId, CompletionHandler<RequestResponse> completionHandler) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", userId);
        channelizeApiClient.sendPutRequest(apiDefaultUrl + "conversations/"
                + conversationId + "/add_admin", param, RequestResponse.class, completionHandler);
    }

    /**
     * Method to leave from a group.
     *
     * @param conversationId    ConversationId of the conversation in which a logged-in user want to leave.
     * @param completionHandler Completion handler of the request.
     */
    public void leaveGroup(String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        String actionUrl = apiDefaultUrl + "conversations/" + conversationId + "/leave";
        channelizeApiClient.sendPostRequest(actionUrl, null, RequestResponse.class, completionHandler);
    }

    /**
     * Method to update the title of the group.
     *
     * @param conversationId    ConversationId of the conversation
     * @param title             New title that needs to be update.
     * @param completionHandler Completion handler of the request.
     */
    public void updateGroupTitle(String conversationId, String title, CompletionHandler<RequestResponse> completionHandler) {
        Map<String, Object> param = new HashMap<>();
        param.put("title", title);
        channelizeApiClient.sendPutRequest(apiDefaultUrl + "conversations/"
                + conversationId + "/update_title", param, RequestResponse.class, completionHandler);
    }

    /**
     * Method to update the profile image of the group.
     *
     * @param conversationId    ConversationId of the conversation
     * @param imagePath         New local image path that needs to be update.
     * @param completionHandler Completion handler of the request.
     */
    public void updateGroupProfilePhoto(String conversationId, String imagePath,
                                        CompletionHandler<RequestResponse> completionHandler) {
        if (imagePath != null && !imagePath.isEmpty() && conversationId != null && !conversationId.isEmpty()) {
            Map<String, Object> postParam = new HashMap<>();
            String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            postParam.put("name", fileName);
            channelizeApiClient.sendPostRequest(apiDefaultUrl + "files", postParam, null, new CompletionHandler<JSONObject>() {
                @Override
                public void onComplete(JSONObject response, ChannelizeError error) {
                    if (response != null) {
                        String uploadUrl = response.optString("uploadUrl");
                        String fileUrl = response.optString("fileUrl");
                        channelizeApiClient.uploadFileAwsS3(uploadUrl, imagePath, imagePath, new CompletionHandler<RequestResponse>() {
                            @Override
                            public void onComplete(RequestResponse result, ChannelizeError error) {
                                if (result != null && result.isSuccessful()) {
                                    Map<String, Object> postParam = new HashMap<>();
                                    postParam.put("profileImageUrl", fileUrl);
                                    channelizeApiClient.sendPutRequest(apiDefaultUrl + "conversations/"
                                            + conversationId + "/update_profile", postParam, RequestResponse.class, completionHandler);
                                } else {
                                    completionHandler.onComplete(null, error);
                                }
                            }
                        });
                    } else if (error != null) {
                        completionHandler.onComplete(null, error);
                    }
                }
            });
        }
    }

    /**
     * Method to set typing status of a user.
     *
     * @param conversationId      Conversation Id in which user is typing.
     * @param isTyping            True, if user is typing otherwise false.
     */
    public void setTyping(String conversationId, boolean isTyping) {
        setTyping(null, conversationId, isTyping);
    }
    /**
     * Method to set typing status of a user.
     *
     * @param conversationMembers Set of UserIds of conversation members.
     * @param conversationId      Conversation Id in which user is typing.
     * @param isTyping            True, if user is typing otherwise false.
     */
    public void setTyping(Set<String> conversationMembers, String conversationId, boolean isTyping) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("id", conversationId);
        postParam.put("conversationId", conversationId);
        postParam.put("isTyping", isTyping);
        //postParam.put("userId", selfUid);
        postParam.put("userId", Channelize.getInstance().getCurrentUserId());

        Channelize channelize = Channelize.getInstance();
        // Sending quick message of typing if the mqtt is connected.
        /*if (channelize.isConnected() && conversationMembers != null
                && !conversationMembers.isEmpty() && conversationId != null) {
            JSONObject parameter = new JSONObject(postParam);
            new Thread(() -> {
                try {
                    for (String userId : conversationMembers) {
                        Channelize.publishMqttData("users/" + userId + "/conversation/typing", parameter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }*/

        Map<String, Object> param = new HashMap<>();
        param.put("isTyping", isTyping);
        channelizeApiClient.sendPostRequest(apiDefaultUrl + "conversations/"
                + conversationId + "/send_typing_status", param, RequestResponse.class, null);
    }

    //***********************************************************************************************Verfied Functions according to V2 Changes**********************

    /***
     * Method to get the one to one conversation object between 2 users. (Sender and receiver)
     * @param recipientId UserId of the receiver.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getOneToOneConversation(final String recipientId, final CompletionHandler<ListConversationResponse> completionHandler) {
        channelizeApiClient.getResponse(apiDefaultUrl + "conversations?membersExactly=" + recipientId + "&isGroup=false&includeDeleted=true&include=members",
                ListConversationResponse.class, completionHandler);
    }

    /*public void getOneToOneConversation(String recipientId, CompletionHandler<Conversation> completionHandler) {
        String actionUrl = apiDefaultUrl + "conversations";
        Map<String, Object> param = new HashMap<>();
        param.put("isGroup", false);
        ArrayList<String> members=new ArrayList<>();
        members.add(recipientId);
        members.add(Channelize.getInstance().getCurrentUserId());
        param.put("members", members);
        channelizeApiClient.sendPostRequest(actionUrl, param, Conversation.class, completionHandler);
    }*/


    /**
     * Method to get the single conversation object from the conversationId.
     *
     * @param conversationId    Conversation Id for which conversation response need to be fetched.
     * @param conversationQuery ConversationQuery, which contains all the filter that need to be apply for single conversation fetching.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getConversation(final String conversationId, ConversationQuery conversationQuery, final CompletionHandler<Conversation> completionHandler) {
        channelizeApiClient.getResponse(apiDefaultUrl + "conversations/" + conversationId,
                conversationQuery.getConversationQueryParams(), Conversation.class, completionHandler);
    }
}
