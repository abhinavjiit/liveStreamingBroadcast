/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.mqtt;


import com.channelize.apisdk.model.User;

/**
 *  The {@link ChannelizeUserEventHandler} class contains all the real time events related to user which can be arrived when app is active.
 *  But all the methods are optional and they need to be added according to the need,
 *  For eg. at User/Friends screen you can only override the user related methods.
 */
public interface ChannelizeUserEventHandler {


    /**
     * This function will invoke once the user status is updated i.e offline to online and online to offline
     *
     * @param user
     */
    default void onUserStatusUpdated(User user) {
    }


    /**
     * This function will invoke once the user is Blocked from conversation
     *
     * @param user
     */
    default void onUserBlocked(User user) {
    }


    //Old.........

    /**
     * Method gets invoked when an user is added as a friend.
     *
     * @param user User model that has been added as friend.
     */
    default void onFriendAdded(User user) {

    }

    /**
     * Method gets invoked when an user is removed from friend list.
     *
     * @param userId UserId of that user who has been removed from friend.
     */
    default void onFriendRemoved(String userId) {

    }

    /**
     * Method gets invoked when an user is blocked by the logged-in user or logged-in user is blocked by some another user.
     *
     * @param isSelfBlock   True, if the logged-in user is blocked by some another user.
     * @param blockedUserId UserId of the user who is blocked by the logged-in user(When isSelfBlock=false)
     *                      or UserId of the user who has blocked the logged-in user(When isSelfBlock=true).
     */
    default void onUserBlocked(boolean isSelfBlock, String blockedUserId) {

    }

    /**
     * Method gets invoked when an user is blocked by the logged-in user or logged-in user is blocked by some another user.
     *
     * @param blockerUser The user who has blocked the another user.
     * @param blockeeUser The user who is blocked by the blocker user.
     */
    default void onUserBlocked(User blockerUser, User blockeeUser) {

    }

    /**
     * Method gets invoked when an user is unblocked by the logged-in user or logged-in user is unblocked by some another user.
     *
     * @param unBlockerUser The user who has unblocked the another user.
     * @param unBlockeeUser The user who is unblocked by the unBlockerUser user.
     */
    default void onUserUnBlocked(User unBlockerUser, User unBlockeeUser) {

    }

    /**
     * Method gets invoked when an user is un-blocked by the logged-in user or then logged-in user is un-blocked by some another user.
     *
     * @param isSelfUnBlock   True, if the logged-in user is un-blocked by some another user.
     * @param unBlockedUserId UserId of the user who is un-blocked by the logged-in user(When isSelfUnBlock=false)
     *                        or UserId of the user who has unblocked the logged-in user(When isSelfUnBlock=true).
     */
    default void onUserUnblocked(boolean isSelfUnBlock, String unBlockedUserId) {

    }

    default void onUserUnblocked(User user) {

    }

    /**
     * Method gets invoked when an user comes online.
     *
     * @param user User model of the user who has came online.
     */
    default void onOnline(User user) {

    }

    /**
     * Method gets invoked when an user goes offline.
     *
     * @param user User model of the user who has gone offline.
     */
    default void onOffline(User user) {

    }


    /**
     * Method gets invoked when there is any changes to an user information.
     *
     * @param user User model of the user with updated info.
     */
    default void onUserUpdated(User user) {

    }

}
