/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.api;


import android.content.Context;
import androidx.annotation.NonNull;

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
import com.channelize.apisdk.network.services.MessageService;
import com.channelize.apisdk.network.services.query.ConversationQuery;
import com.channelize.apisdk.network.services.ConversationService;
import com.channelize.apisdk.network.services.query.MessageQuery;
import com.channelize.apisdk.network.services.query.UserQuery;
import com.channelize.apisdk.network.services.UserService;
import com.channelize.apisdk.network.services.ModuleService;


import org.json.JSONArray;
import java.util.Set;


public class ChannelizeApiClient implements ChannelizeApi {

    // Member variables.
    private UserService userService;
    private ConversationService conversationService;
    private MessageService messageService;
    private ModuleService moduleService;


    public ChannelizeApiClient(Context context) {
        userService = new UserService(context);
        conversationService = new ConversationService(context);
        messageService = new MessageService(context);
        moduleService = new ModuleService(context);
    }

    @Override
    public void getUser(@NonNull String id, @NonNull CompletionHandler<User> completionHandler) {
        userService.getUser(id, completionHandler);
    }

    @Override
    public void getFriendsCount(@NonNull UserQuery userQuery, @NonNull CompletionHandler<TotalCountResponse> completionHandler) {
        userService.getFriendsCount(userQuery, completionHandler);
    }

    @Override
    public void getFriendsList(@NonNull UserQuery userQuery, @NonNull CompletionHandler<ListUserResponse> completionHandler) {
        userService.getFriendsList(userQuery, completionHandler);
    }

    @Override
    public void getSearchAllUsersSettings(@NonNull CompletionHandler<SearchUserSetting> completionHandler) {
        userService.getSearchAllUsersSettings(completionHandler);
    }

    @Override
    public void getAllUserList(@NonNull UserQuery userQuery, @NonNull CompletionHandler<ListUserResponse> completionHandler) {
        userService.getAllUserList(userQuery, completionHandler);
    }

    @Override
    public void getBlocksCount(@NonNull UserQuery userQuery, @NonNull CompletionHandler<TotalCountResponse> completionHandler) {
        userService.getBlocksCount(userQuery, completionHandler);
    }

    @Override
    public void getBlockedUsersList(@NonNull UserQuery userQuery, @NonNull CompletionHandler<ListUserResponse> completionHandler) {
        userService.getBlockedList(userQuery, completionHandler);
    }

    @Override
    public void checkBlockStatus(@NonNull String recipientId, CompletionHandler<UserBlockStatusResponse> completionHandler) {
        userService.checkBlockStatus(recipientId, completionHandler);
    }

    @Override
    public void getTotalUnReadMessageCount(@NonNull CompletionHandler<TotalCountResponse> completionHandler) {
        messageService.getTotalUnReadMessageCount(completionHandler);
    }

    @Override
    public void getConversationsCount(@NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<TotalCountResponse> completionHandler) {
        conversationService.getConversationsCount(conversationQuery, completionHandler);
    }

    @Override
    public void getTotalMessageCount(@NonNull String conversationId, @NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<TotalCountResponse> completionHandler) {
        conversationService.getTotalMessageCount(conversationId, conversationQuery, completionHandler);
    }

    @Override
    public void getMembersList(@NonNull String conversationId, @NonNull CompletionHandler<ListMemberResponse> completionHandler) {
        conversationService.getMembersList(conversationId, completionHandler);
    }

    @Override
    public void getOneToOneConversation(@NonNull String recipientId, @NonNull CompletionHandler<ListConversationResponse> completionHandler) {
        conversationService.getOneToOneConversation(recipientId, completionHandler);
    }

    @Override
    public void getConversation(@NonNull String conversationId, @NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<Conversation> completionHandler) {
        conversationService.getConversation(conversationId, conversationQuery, completionHandler);
    }

    @Override
    public void getConversationsList(@NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<ListConversationResponse> completionHandler) {
        conversationService.getConversationList(conversationQuery, completionHandler);
    }

    @Override
    public void getMessages(@NonNull String conversationId, @NonNull ConversationQuery conversationQuery, @NonNull CompletionHandler<ListMessageResponse> completionHandler) {
        conversationService.getMessages(conversationId, conversationQuery, completionHandler);
    }

    @Override
    public void getModules(CompletionHandler<ListModuleResponse> completionHandler) {
        moduleService.getModulesInfo(completionHandler);
    }

    @Override
    public void forwardMessages(JSONArray messageIdsArray, JSONArray userIdsArray,
                                JSONArray conversationIdsArray, CompletionHandler<RequestResponse> completionHandler) {
        messageService.forwardMessages(messageIdsArray, userIdsArray, conversationIdsArray, completionHandler);
    }

    @Override
    public void sendMessage(@NonNull Set<String> conversationMembers, @NonNull MessageQuery messageQuery, CompletionHandler<Message> completionHandler) {
        messageService.sendMessage(conversationMembers, null, messageQuery, completionHandler);
    }

    @Override
    public void sendFileMessage(@NonNull String filePath, String thumbnailFilePath, @NonNull Set<String> conversationMembers, @NonNull MessageQuery messageQuery, CompletionHandler<Message> completionHandler) {
        messageService.sendFileMessage(filePath, thumbnailFilePath, conversationMembers, messageQuery, completionHandler);
    }

    @Override
    public void quoteMessage(@NonNull Set<String> conversationMembers, Message quotedMessage, @NonNull MessageQuery messageQuery, CompletionHandler<Message> completionHandler) {
        messageService.sendMessage(conversationMembers, quotedMessage, messageQuery, completionHandler);
    }

    @Override
    public void markMessageRead(@NonNull String conversationId, @NonNull String messageId, @NonNull String messageOwnerId, CompletionHandler<RequestResponse> completionHandler) {
        messageService.markMessageRead(conversationId, messageId, messageOwnerId, completionHandler);
    }

    @Override
    public void addFriend(String userId, CompletionHandler<RequestResponse> completionHandler) {
        userService.addFriend(userId, completionHandler);
    }

    @Override
    public void logoutUserFromChannelize(CompletionHandler<RequestResponse> completionHandler) {
        userService.logoutFromChannelize(completionHandler);
    }

    @Override
    public void removeFriend(String userId, CompletionHandler<RequestResponse> completionHandler) {
        userService.removeFriend(userId, completionHandler);
    }

    @Override
    public void blockUser(@NonNull String userId, CompletionHandler<RequestResponse> completionHandler) {
        userService.blockUnBlockUser(userId, true, completionHandler);
    }

    @Override
    public void unBlockUser(@NonNull String userId, CompletionHandler<RequestResponse> completionHandler) {
        userService.blockUnBlockUser(userId, false, completionHandler);
    }

    @Override
    public void setUserOnline(CompletionHandler<User> completionHandler) {
        userService.setUserOnline(completionHandler);
    }

    @Override
    public void setUserOffline(CompletionHandler<User> completionHandler) {
        userService.setUserOffline(completionHandler);
    }

    @Override
    public void updateUserInfo(@NonNull UserQuery userQuery, CompletionHandler<User> completionHandler) {
        userService.updateUserInfo(userQuery, completionHandler);
    }

    @Override
    public void setTyping(@NonNull Set<String> conversationMembers, @NonNull String conversationId, boolean isTyping) {
        conversationService.setTyping(conversationMembers, conversationId, isTyping);
    }

    @Override
    public void setTyping(@NonNull String conversationId, boolean isTyping) {
        conversationService.setTyping(conversationId, isTyping);
    }

    @Override
    public void markAllMessageRead(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.markAllMessageRead(conversationId, completionHandler);
    }

    @Override
    public void deleteMessages(@NonNull JSONArray messageIdsArray, CompletionHandler<RequestResponse> completionHandler) {
        messageService.deleteMessages(messageIdsArray, completionHandler);
    }

    @Override
    public void deleteMessageForEveryone(@NonNull JSONArray messageIdsArray, CompletionHandler<RequestResponse> completionHandler) {
        messageService.deleteMessageForEveryone(messageIdsArray,completionHandler);
    }

    @Override
    public void muteConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.muteUnmuteConversation(conversationId, true, completionHandler);
    }

    @Override
    public void unmuteConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.muteUnmuteConversation(conversationId, false, completionHandler);
    }

    @Override
    public void deleteConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.deleteConversation(conversationId, completionHandler);
    }

    @Override
    public void clearConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.clearConversation(conversationId, completionHandler);
    }

    @Override
    public void createConversation(@NonNull String title, @NonNull JSONArray membersArray, CompletionHandler<Conversation> completionHandler) {
        conversationService.createGroup(title, membersArray, completionHandler);
    }

    @Override
    public void createOneToOneConversation(@NonNull JSONArray membersArray, CompletionHandler<Conversation> completionHandler) {
        conversationService.createOneToOneChat(membersArray, completionHandler);
    }

    @Override
    public void addMembers(@NonNull String conversationId, @NonNull JSONArray membersArray, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.addMembersToGroup(conversationId, membersArray, completionHandler);
    }

    @Override
    public void removeMembers(@NonNull String conversationId, @NonNull JSONArray membersArray, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.removeMemberFromGroup(conversationId, membersArray, completionHandler);
    }

    @Override
    public void addAdmin(@NonNull String conversationId, @NonNull String userId, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.makeAdmin(conversationId, userId, completionHandler);
    }

    @Override
    public void leaveConversation(@NonNull String conversationId, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.leaveGroup(conversationId, completionHandler);
    }

    @Override
    public void updateConversationTitle(@NonNull String conversationId, @NonNull String title, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.updateGroupTitle(conversationId, title, completionHandler);
    }

    @Override
    public void updateConversationProfilePhoto(@NonNull String conversationId, @NonNull String imagePath, CompletionHandler<RequestResponse> completionHandler) {
        conversationService.updateGroupProfilePhoto(conversationId, imagePath, completionHandler);
    }

}
