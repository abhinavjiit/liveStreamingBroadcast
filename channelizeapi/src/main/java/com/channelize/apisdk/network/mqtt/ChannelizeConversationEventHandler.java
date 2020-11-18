/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.mqtt;


import com.channelize.apisdk.model.Conversation;
import com.channelize.apisdk.model.Member;
import com.channelize.apisdk.model.Message;
import com.channelize.apisdk.model.User;

import java.util.List;

/**
 *  The {@link ChannelizeConversationEventHandler} class contains all the real time events related to conversation which can be arrived when app is active.
 *  But all the methods are optional and they need to be added according to the need,
 *  For eg. at RecentConversation/Conversation screen you can only override the conversation related methods.
 */
public interface ChannelizeConversationEventHandler {

    /**
     * Method gets invoked when there is any changes occur to a conversation.
     *
     * @param conversation Conversation model with updated info.
     */
    default void onConversationUpdated(Conversation conversation) {}

    /**
     * Method gets invoked when a whole conversation has been deleted by the logged-in user.
     *
     * @param conversation Conversation model with updated info.
     */
    default void onConversationDeleted(Conversation conversation) {}

    /**
     * Method gets invoked when a whole conversation has been cleared by the logged-in user.
     *
     * @param conversation Conversation model with updated info.
     */
    default void onConversationCleared(Conversation conversation) {}

    /**
     * Method gets invoked when logged-in user joined a group conversation.
     *
     * @param conversation Conversation model with updated info.************************************
     */
    default void onUserJoined(Conversation conversation) {}

    /**
     * Method gets invoked when logged-in user joined a group conversation.
     *
     * @param conversation Conversation model.
     * @param timeStamp TimeStamp when the user is being joined into the group.
     */
    default void onUserJoined(Conversation conversation, String timeStamp) {}

    /**
     * Method gets invoked when logged-in user removed or left from a group conversation.
     *
     * @param conversation Conversation model.
     * @param timeStamp TimeStamp when the user is being removed/left from the group.
     */
    default void onUserLeft(Conversation conversation, String timeStamp) {}

    /**
     * Method gets invoked when the logged-in user removed from a group conversation.
     *
     * @param conversation Conversation model with updated info.************************************
     */
    default void onUserLeft(Conversation conversation) {}

    /**
     * Method gets invoked when a conversation is marked as mute or unmute.
     *
     * @param conversationId ConversationId in which action has been performed.
     * @param isMute         True, if the conversation is marked as mute otherwise false.
     */
    default void onUserMuteStatusUpdated(String conversationId, boolean isMute) {}


    /*
   This interface is called once admin is added in conversation
    */
    default void onAdminAdded(Conversation conversation, Member member) {}


    /**
     * Method gets invoked when a new message arrived.
     *
     * @param message Message model which contains the new message information.
     */
    default void onMessageReceived(Message message) {}

    /**
     * Method gets invoked when a single or multiple messages has been deleted in a conversation.
     *
     * @param List<Message> List of Message objects Deleted for self
     */
    default void onMessagesDeleted(List<Message> mMessageList) {}

    /**
     * Method gets invoked when a single or multiple messages has been deleted in a conversation for everyone.
     *
     * @param List<Message> List of Message objects Deleted for everyone
     */
    default void onMessagesDeletedForEveryOne(List<Message> mMessageList) {}


    // Once message is read by the user
    default void onReadMessageToOwner(Conversation conversation, User user, String timeStamp) {}

    /*
    This interface is called once member is added in conversation
     */
    default void onMemberAdded(Conversation conversation, List<Member> mMemberList) {}

    /*
    This interface is called once member is removed from conversation
     */
    default void onMemberRemoved(Conversation conversation, List<Member> mMemberList) {}


    ////Old
    /**
     * Method gets invoked when a group member is promoted to admin or removed from admin post.
     *
     * @param conversationId ConversationId in which the admin related changes occurred.
     * @param userId         UserId of the user who has been promoted to admin or removed from admin.
     * @param isAdmin        True, if the user is promoted to admin otherwise false.
     */
    default void onConversationAdminAdded(String conversationId, String userId, boolean isAdmin) {

    }

    /**
     * Method gets invoked when a user will starts/stop typing in a conversation
     *
     * @param conversation  Conversation in which the user start/stop typing.
     * @param typingUser    User who will start/stop typing in the conversation.
     * @param isTyping      True, if the user will start typing otherwise false.
     */
    default void onTypingStatusUpdated(Conversation conversation, User typingUser, boolean isTyping) {

    }










    /**
     * Method gets invoked when a message is marked as read by the logged-in user.
     *
     * @param conversation Conversation in which that message exist.
     */
    default void onReadMessageToSelf(Conversation conversation, User user, String timeStamp) {}
}
