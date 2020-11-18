/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.api;




import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.channelize.apisdk.model.Conversation;
import com.channelize.apisdk.model.Message;
import com.channelize.apisdk.model.User;
import com.channelize.apisdk.network.response.CompletionHandler;
import com.channelize.apisdk.network.response.ListConversationResponse;
import com.channelize.apisdk.network.response.ListMemberResponse;
import com.channelize.apisdk.network.response.ListMessageResponse;
import com.channelize.apisdk.network.response.ListModuleResponse;
import com.channelize.apisdk.network.response.ListUserResponse;
import com.channelize.apisdk.network.response.RequestResponse;
import com.channelize.apisdk.network.response.SearchUserSetting;
import com.channelize.apisdk.network.response.TotalCountResponse;
import com.channelize.apisdk.network.response.UserBlockStatusResponse;
import com.channelize.apisdk.network.services.query.ConversationQuery;
import com.channelize.apisdk.network.services.query.MessageQuery;
import com.channelize.apisdk.network.services.query.UserQuery;

import org.json.JSONArray;

import java.util.List;
import java.util.Set;


public interface ChannelizeApi {


    //============================================================
    //                      User related API
    //============================================================

    void getUser(@NonNull String id, @NonNull CompletionHandler<User> completionHandler);

    void getFriendsCount(@NonNull UserQuery userQuery, @NonNull CompletionHandler<TotalCountResponse> completionHandler);

    void getFriendsList(@NonNull UserQuery userQuery, @NonNull CompletionHandler<ListUserResponse> completionHandler);

    void getSearchAllUsersSettings(@NonNull CompletionHandler<SearchUserSetting> completionHandler);

    void getAllUserList(@NonNull UserQuery userQuery, @NonNull CompletionHandler<ListUserResponse> completionHandler);

    void getBlocksCount(@NonNull UserQuery userQuery, @NonNull CompletionHandler<TotalCountResponse> completionHandler);

    void getBlockedUsersList(@NonNull UserQuery userQuery, @NonNull CompletionHandler<ListUserResponse> completionHandler);

    void checkBlockStatus(@NonNull String recipientId, CompletionHandler<UserBlockStatusResponse> completionHandler);


    //============================================================
    //                  Message related API
    //============================================================

    void getTotalUnReadMessageCount(@NonNull CompletionHandler<TotalCountResponse> completionHandler);


    //============================================================
    //                  Conversation related API
    //============================================================

    void getConversationsCount(@NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<TotalCountResponse> completionHandler);

    void getTotalMessageCount(@NonNull String conversationId, @NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<TotalCountResponse> completionHandler);

    void getMembersList(@NonNull String conversationId, @NonNull CompletionHandler<ListMemberResponse> completionHandler);

    void getOneToOneConversation(@NonNull String recipientId, @NonNull CompletionHandler<ListConversationResponse> completionHandler);

    void getConversation(@NonNull String conversationId, @NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<Conversation> completionHandler);

    void getConversationsList(@NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<ListConversationResponse> completionHandler);

    void getMessages(@NonNull String conversationId, @NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<ListMessageResponse> completionHandler);


    //============================================================
    //                      Module related API
    //============================================================

    void getModules(CompletionHandler<ListModuleResponse> completionHandler);


    //============================================================
    //                  Message Action related API
    //============================================================

    void forwardMessages(JSONArray messageIdsArray, JSONArray userIdsArray, JSONArray conversationIdsArray, CompletionHandler<RequestResponse> completionHandler);

    void sendMessage(@NonNull Set<String> conversationMembers, @NonNull MessageQuery messageQuery, CompletionHandler<Message> completionHandler);

    void sendFileMessage(@NonNull String filePath, @Nullable String thumbnailFilePath, @NonNull Set<String> conversationMembers, @NonNull MessageQuery messageQuery, CompletionHandler<Message> completionHandler);

    void quoteMessage(@NonNull Set<String> conversationMembers, Message quotedMessage, @NonNull MessageQuery messageQuery, CompletionHandler<Message> completionHandler);

    void markMessageRead(@NonNull String conversationId, @NonNull String messageId, @NonNull String messageOwnerId, CompletionHandler<RequestResponse> completionHandler);


    //============================================================
    //                  User Action related API
    //============================================================

    void addFriend(String userId, CompletionHandler<RequestResponse> completionHandler);

    void removeFriend(String userId, CompletionHandler<RequestResponse> completionHandler);

    void blockUser(@NonNull String userId, CompletionHandler<RequestResponse> completionHandler);

    void unBlockUser(@NonNull String userId, CompletionHandler<RequestResponse> completionHandler);

    void setUserOnline(CompletionHandler<User> completionHandler);

    void setUserOffline(CompletionHandler<User> completionHandler);

    void updateUserInfo(@NonNull UserQuery userQuery, CompletionHandler<User> completionHandler);


    //============================================================
    //                  Conversation Action related API
    //============================================================


    void setTyping(@NonNull Set<String> conversationMembers, @NonNull String conversationId, boolean isTyping);

    void setTyping(@NonNull String conversationId, boolean isTyping);

    void markAllMessageRead(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler);

    void deleteMessages(@NonNull JSONArray messageIdsArray, CompletionHandler<RequestResponse> completionHandler);

    void deleteMessageForEveryone(@NonNull JSONArray messageIdsArray, CompletionHandler<RequestResponse> completionHandler);

    void muteConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler);

    void unmuteConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler);

    void deleteConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler);

    void clearConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler);

    void createConversation(@NonNull String title, @NonNull JSONArray membersArray, CompletionHandler<Conversation> completionHandler);

    void createOneToOneConversation(@NonNull JSONArray membersArray, CompletionHandler<Conversation> completionHandler);

    void addMembers(@NonNull String conversationId, @NonNull JSONArray membersArray, CompletionHandler<RequestResponse> completionHandler);

    void removeMembers(@NonNull String conversationId, @NonNull JSONArray membersArray, CompletionHandler<RequestResponse> completionHandler);

    void addAdmin(@NonNull String conversationId, @NonNull String userId, CompletionHandler<RequestResponse> completionHandler);

    void leaveConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler);

    void updateConversationTitle(@NonNull String conversationId, @NonNull String title, CompletionHandler<RequestResponse> completionHandler);

    void updateConversationProfilePhoto(@NonNull String conversationId, @NonNull String imagePath, CompletionHandler<RequestResponse> completionHandler);

    void logoutUserFromChannelize(CompletionHandler<RequestResponse> completionHandler);

}
