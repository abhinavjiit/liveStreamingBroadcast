/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.services.query;



import androidx.annotation.NonNull;

import com.channelize.apisdk.ApiConstants;
import com.channelize.apisdk.model.Location;
import com.channelize.apisdk.model.Message;
import com.channelize.apisdk.model.User;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;


public class MessageQuery {

    // Must required variables to use MessageQuery.
    private final Map<String, Object> messageQueryParams;


    private MessageQuery(Map<String, Object> conversationQueryParams) {
        this.messageQueryParams = conversationQueryParams;
    }

    public Map<String, Object> getMessageQueryParams() {
        return messageQueryParams;
    }

    /**
     * Builder for creating Param instances.
     */
    public static class Builder {

        // Query param that will be passed when fetching conversation related apis.
        private volatile Map<String, Object> userQueryParams;

        /**
         * Start building a new {@link MessageQuery.Builder} instance.
         */
        public Builder() {
            this.userQueryParams = new HashMap<>();
        }

        /**
         * Method to set the userId of the receiver when sending a message in one to one conversation and conversation id is not known.
         *
         * @param userId UserId of the receiver user if sending a message in a one to one conversation
         *               and there is no conversation id is known between logged-in user and receiver user.
         * @return Returns the Builder instance.
         */
        public Builder setUserId(String userId) {
            userQueryParams.put("userId", userId);
            return this;
        }

        /**
         * Method to set the conversation id.
         *
         * @param conversationId If you know the conversation Id in which you're sending a message
         *                       (In this case do not send the "userId").
         * @return Returns the Builder instance.
         */
        public Builder setConversationId(String conversationId) {
            userQueryParams.put("conversationId", conversationId);
            return this;
        }

        /**
         * Method to set an unique id of the message that you're sending.
         * 32 digit unique message id, you can use **UUID.randomUUID().toString()**
         * to generate the random messageId while sending a new message.
         *
         * @param messageId Unique message id.
         * @return Returns the Builder instance.
         */
        public Builder setMessageId(String messageId) {
            userQueryParams.put("id", messageId);
            return this;
        }

        public Builder setType(String type) {
            userQueryParams.put("type", type);
            return this;
        }

        public Builder setCreatedAt(String createdTime) {
            userQueryParams.put("createdAt", createdTime);
            return this;
        }

        public Builder setOwnerId(String ownerId) {
            userQueryParams.put("ownerId", ownerId);
            return this;
        }

        public Builder setOwner(User user) {
            userQueryParams.put("owner", user);
            return this;
        }

        /**
         * Method to set attachment type of the message that you're sending.
         * Must be one of {@link ApiConstants.ATTACHMENT}. ({ATTACHMENT_TEXT, ATTACHMENT_IMAGE, ATTACHMENT_VIDEO, ATTACHMENT_AUDIO, ATTACHMENT_STICKER, ATTACHMENT_GIF, ATTACHMENT_LOCATION})
         *
         * @param attachmentType Attachment type of the message.
         * @return Returns the Builder instance.
         */
        public Builder setMessageAttachmentType(@ApiConstants.ATTACHMENT String attachmentType) {
            // For Example setMessageAttachmentType(ApiConstants.ATTACHMENT_AUDIO);

            userQueryParams.put("attachmentType", attachmentType);
            return this;
        }

        /**
         * Method to send text message.
         *
         * @param messageBody Message body which you need to send as text message.
         * @return Returns the Builder instance.
         */
        public Builder setMessageBody(String messageBody) {
            userQueryParams.put("body", messageBody);
            return this;
        }

        /**
         * Method to set the tagged members info when tagging the users in a message.
         *
         * @param memberTagsArray JSONArray of members which are tagged in the message body.
         * @return Returns the Builder instance.
         */
        public Builder setTaggedMemberArray(@NonNull JSONArray memberTagsArray) {

            userQueryParams.put("tags", memberTagsArray);
            return this;
        }

        /**
         * Method to set the location param.
         *
         * @param location Location instance which you want to send in a message.
         * @return Returns the Builder instance.
         */
        public Builder setLocation(@NonNull Location location) {
            userQueryParams.put("location", location);
            return this;
        }

        /**
         * Method to set the quoted message param.
         *
         * @param message Message instance which you want to quote.
         * @return Returns the Builder instance.
         */
        public Builder setQuoteMessage(@NonNull Message message) {

            userQueryParams.put("parentMessage", message);
            return this;
        }

        /**
         * Method to set the data for the gif/sticker type messages.
         *
         * @param originalUrl    Url of the gif/sticker image in animated format.
         * @param stillUrl       Url of the gif/sticker image in non-animated format (That's why its stillUrl).
         * @param downsampledUrl Url of the gif/sticker image in animated format(DownSampled).
         * @return Returns the Builder instance.
         */
        public Builder setGifSticker(@NonNull String originalUrl, @NonNull String stillUrl, @NonNull String downsampledUrl) {

            userQueryParams.put("originalUrl", originalUrl);
            userQueryParams.put("stillUrl", stillUrl);
            userQueryParams.put("downsampledUrl", downsampledUrl);
            return this;
        }

        /**
         * Build the {@link MessageQuery} instance
         */
        public MessageQuery build() {
            return new MessageQuery(userQueryParams);
        }
    }
}
