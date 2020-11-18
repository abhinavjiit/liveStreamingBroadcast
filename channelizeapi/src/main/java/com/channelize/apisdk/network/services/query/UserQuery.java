/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.services.query;



import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.util.HashMap;
import java.util.Map;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class UserQuery {

    // Must required variables to use UserQuery.
    private final Map<String, String> userQueryParams;


    private UserQuery(Map<String, String> conversationQueryParams) {
        this.userQueryParams = conversationQueryParams;
    }

    public Map<String, String> getUserQueryParams() {
        return userQueryParams;
    }

    /**
     * Builder for creating Param instances.
     */
    public static class Builder {

        // Query param that will be passed when fetching conversation related apis.
        private volatile Map<String, String> userQueryParams;

        /**
         * Start building a new {@link UserQuery.Builder} instance.
         */
        public Builder() {
            this.userQueryParams = new HashMap<>();
        }

        /**
         * If you want to filter users according to online or offline users.
         *
         * @param isOnline True, if you want to fetch the online users only.
         *                 And False if you want to fetch only offline users.
         *                 Leave this param if you want to fetch both type of users.
         * @return Returns the Builder instance.
         */
        public Builder isOnline(boolean isOnline) {
            // For Example isOnline(true); for online users only.
            // For Example isOnline(false); for offline users only.
            // For Example, Leave this field if you want to fetch both types.

            userQueryParams.put("online", String.valueOf(isOnline));
            return this;
        }

        /**
         * If you want to search the user in which the searched text has been included in user title.
         *
         * @param query Query which you want to search.
         * @return Returns the Builder instance.
         */
        public Builder setSearchQuery(String query) {
            // For Example setSearchQuery("channelize");

            userQueryParams.put("search", query);
            return this;
        }

        /**
         * If you want to include the users which are blocked by the logged-in user.
         *
         * @param includeBlocked True, if you want to include the blocked users in response.
         * @return Returns the Builder instance.
         */
        public Builder includeBlocked(boolean includeBlocked) {
            // For Example includeBlocked(true);

            userQueryParams.put("includeBlocked", String.valueOf(includeBlocked));
            return this;
        }

        /**
         * If you want to sort the conversation response then pass the sorting order in this query.
         *
         * @param sortOrder Sorting order according to which you want to sort the response.
         * @return Returns the Builder instance.
         */
        public Builder setSorting(@SORT String sortOrder) {
            // For Example setSorting(UserQuery.SORT_DESCENDING); results will be in descending order.
            // For Example setSorting(UserQuery.SORT_ASCENDING); results will be in ascending order.

            userQueryParams.put("sort", sortOrder);
            return this;
        }

        /**
         * Method to set the limit (Number of result) that you want to receive in single request.
         *
         * @param limit Limit of the conversations.
         * @return Returns the Builder instance.
         */
        public Builder setLimit(int limit) {
            // For Example setLimit(30);

            userQueryParams.put("limit", String.valueOf(limit));
            return this;
        }

        /**
         * Method to set the offset (In Pagination number of results that you want to exclude for next page's data) that you want to exclude in single request.
         *
         * @param offset Offset of the conversations.
         * @return Returns the Builder instance.
         */
        public Builder setOffset(int offset) {
            // For Example if you're fetching 2nd page's data and your limit is 30
            // then for the next page's data your offset will be:
            // setOffset(30);

            userQueryParams.put("skip", String.valueOf(offset));
            return this;
        }

        /**
         * If you want to skip few users from the response then you can pass those user's id in query.
         *
         * @param skipUserIds UserIds of the user whom you want to skip from the response (For multiple: pass comma separated values)
         * @return Returns the Builder instance.
         */
        public Builder skipUsers(String skipUserIds) {
            // For Example skipUsers("1, 2, 3");

            userQueryParams.put("skipUserIds", skipUserIds);
            return this;
        }


        //============================================================
        //                  User info update related query
        //============================================================

        /**
         * Method to update the user name.
         *
         * @param title New user name of the user.
         * @return Returns the Builder instance.
         */
        public Builder setTitle(String title) {

            userQueryParams.put("displayName", title);
            return this;
        }

        /**
         * Method to update the user's profile picture.
         *
         * @param profileImageUrl New profile picture url of the user that need to be update.
         * @return Returns the Builder instance.
         */
        public Builder setProfileImageUrl(String profileImageUrl) {

            userQueryParams.put("profileImageUrl", profileImageUrl);
            return this;
        }

        /**
         * If you want to update the notification setting of logged-in user.
         *
         * @param notification True if you want to enable notifications otherwise false.
         * @return Returns the Builder instance.
         */
        public Builder setNotification(boolean notification) {

            userQueryParams.put("notification", String.valueOf(notification));
            return this;
        }

        /**
         * If you want to update the visibility of logged-in user.
         *
         * @param visibility True, if you want to make user visible otherwise false.
         * @return Returns the Builder instance.
         */
        public Builder setVisibility(boolean visibility) {

            userQueryParams.put("visibility", String.valueOf(visibility));
            return this;
        }

        /**
         * Build the {@link UserQuery} instance
         */
        public UserQuery build() {
            return new UserQuery(userQueryParams);
        }
    }

    @Retention(SOURCE)
    @StringDef({SORT_ASCENDING, SORT_DESCENDING})
    public @interface SORT {
    }

    public static final String SORT_ASCENDING = "updatedAt ASC";
    public static final String SORT_DESCENDING = "updatedAt DESC";

}
