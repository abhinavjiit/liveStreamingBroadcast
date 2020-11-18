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


public class ConversationQuery {

    // Must required variables to use ConversationQuery.
    private final Map<String, String> conversationQueryParams;


    private ConversationQuery(Map<String, String> conversationQueryParams) {
        this.conversationQueryParams = conversationQueryParams;
    }

    public Map<String, String> getConversationQueryParams() {
        return conversationQueryParams;
    }

    /**
     * Builder for creating Param instances.
     */
    public static class Builder {

        // Query param that will be passed when fetching conversation related apis.
        private volatile Map<String, String> conversationQueryParams;

        /**
         * Start building a new {@link ConversationQuery.Builder} instance.
         */
        public Builder() {
            this.conversationQueryParams = new HashMap<>();
        }

        /**
         * If you want to fetch conversation between logged-in user and some another user then you've pass the user id of the another user.
         *
         * @param memberId UserId of the another user with whom you want to fetch the conversation.
         * @return Returns the Builder instance.
         */
        public Builder setMemberId(String memberId) {
            // For Example setMemberId("USER_ID"); where USER_ID is the userId of some another user.
            conversationQueryParams.put("memberId", memberId);
            return this;
        }

        /**
         * If you want to filter conversation according to one to one or group conversations.
         *
         * @param isGroup True, if you want to fetch the group conversation only.
         *                And False if you want to fetch only one to one conversation.
         *                Leave this param if you want to fetch both type of conversations.
         * @return Returns the Builder instance.
         */
        public Builder isGroup(boolean isGroup) {
            // For Example isGroup(true); for group conversations only.
            // For Example isGroup(false); for one to one conversations only.
            // For Example, Leave this field if you want to fetch both types.

            conversationQueryParams.put("isGroup", String.valueOf(isGroup));
            return this;
        }

        /**
         * If you want to include some data in conversations response then use this query.
         *
         * @param include Name of the data that you want to include in conversation response.
         * @return Returns the Builder instance.
         */
        public Builder setInclude(@INCLUDE String include) {
            // For Example setInclude(ConversationQuery.MEMBERS_LIST); If you want to include membersList in conversation response.
            // For Example setInclude(ConversationQuery.MESSAGES); If you want to include messages in conversation response.

            conversationQueryParams.put("include", include);
            return this;
        }

        /**
         * If you want to include the deleted conversation in response then add true in the query otherwise false.
         *
         * @param includeDeleted True, if you want to include the deleted conversation.
         * @return Returns the Builder instance.
         */
        public Builder includeDeletedConversations(boolean includeDeleted) {
            // For Example includeDeletedConversations(true);

            conversationQueryParams.put("includeDeleted", String.valueOf(includeDeleted));
            return this;
        }

        /**
         * If you want to include the conversations in response in which logged-in user is active then add true in the query otherwise false.
         *
         * @param includeOnlyActive True, if you want to include the active conversation only.
         * @return Returns the Builder instance.
         */
        public Builder includeActiveConversations(boolean includeOnlyActive) {
            // For Example includeActiveConversations(true);

            conversationQueryParams.put("includeOnlyActive", String.valueOf(includeOnlyActive));
            return this;
        }

        /**
         * If you want to search the conversation in which the searched text has been included in group title or in group members list.
         *
         * @param query Query which you want to search.
         * @return Returns the Builder instance.
         */
        public Builder setSearchQuery(String query) {
            // For Example setSearchQuery("channelize");

            conversationQueryParams.put("search", query);
            return this;
        }

        /**
         * If you want to sort the conversation response then pass the sorting order in this query.
         *
         * @param sortOrder Sorting order according to which you want to sort the response.
         * @return Returns the Builder instance.
         */
        public Builder setSorting(@SORT String sortOrder) {
            // For Example setSorting(ConversationQuery.SORT_DESCENDING); results will be in descending order.
            // For Example setSorting(ConversationQuery.SORT_ASCENDING); results will be in ascending order.

            conversationQueryParams.put("sort", sortOrder);
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

            conversationQueryParams.put("limit", String.valueOf(limit));
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

            conversationQueryParams.put("skip", String.valueOf(offset));
            return this;
        }

        /**
         * Method to set owner id if you want to fetch conversation messages whose owner is the given user.
         *
         * @param ownerId OwnerId of the user whose messages you want to fetch.
         * @return Returns the Builder instance.
         */
        public Builder setMessageOwnerId(String ownerId) {
            // For Example ownerId("1");

            conversationQueryParams.put("ownerId", ownerId);
            return this;
        }

        /**
         * Method to set the content type of message, so that only those messages will come in response
         * in which contentType is equal to the contentType that you've passed in query.
         *
         * @param contentType Content type of the message.
         * @return Returns the Builder instance.
         */
        public Builder setMessageContentType(int contentType) {
            // For Example setMessageContentType(1);

            conversationQueryParams.put("contentType", String.valueOf(contentType));
            return this;
        }

        /**
         * Method to set the attachment type of messages, so that only those messages will come in response
         * in which attachmentType is equal to the attachmentType that you've passed in query.
         *
         * @param attachmentType AttachmentType of the message
         *                      (For multiple: pass comma separated values. NO SPACE)
         * @return Returns the Builder instance.
         */
        public Builder setMessageAttachmentType(String attachmentType) {
            // For Example setMessageAttachmentType("text,audio,video");

            attachmentType = attachmentType.replaceAll("\\s+", "");
            conversationQueryParams.put("attachmentType", attachmentType);
            return this;
        }

        /**
         * Build the {@link ConversationQuery} instance
         */
        public ConversationQuery build() {
            return new ConversationQuery(conversationQueryParams);
        }

    }

    @Retention(SOURCE)
    @StringDef({MEMBERS_LIST, MESSAGES})
    public @interface INCLUDE {
    }

    @Retention(SOURCE)
    @StringDef({SORT_ASCENDING, SORT_DESCENDING})
    public @interface SORT {
    }

    public static final String MEMBERS_LIST = "members";
    public static final String MESSAGES = "messages";
    public static final String SORT_ASCENDING = "updatedAt ASC";
    public static final String SORT_DESCENDING = "updatedAt DESC";

}
