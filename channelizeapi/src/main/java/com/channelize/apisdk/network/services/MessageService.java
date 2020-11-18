/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.services;


import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;


import androidx.annotation.NonNull;

import com.channelize.apisdk.ApiConstants;
import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.model.Location;
import com.channelize.apisdk.model.Message;
import com.channelize.apisdk.network.api.ChannelizeOkHttpUtil;
import com.channelize.apisdk.network.response.ChannelizeError;
import com.channelize.apisdk.network.response.CompletionHandler;
import com.channelize.apisdk.network.response.GenericResponse;
import com.channelize.apisdk.network.response.RequestResponse;
import com.channelize.apisdk.network.response.TotalCountResponse;
import com.channelize.apisdk.network.services.query.MessageQuery;
import com.channelize.apisdk.utils.ChannelizePreferences;
import com.channelize.apisdk.utils.CoreFunctionsUtil;
import com.channelize.apisdk.utils.OkHttpUtils;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import static com.channelize.apisdk.network.api.ChannelizeOkHttpUtil.GSON_INSTANCE;


public final class MessageService {

    private String selfId, apiDefaultUrl;
    private ChannelizeOkHttpUtil channelizeOkHttpUtil;

    public MessageService(Context context) {
        Channelize channelize = Channelize.getInstance();
        selfId = channelize.getCurrentUserId();
        apiDefaultUrl = channelize.getApiDefaultUrl();
        channelizeOkHttpUtil = ChannelizeOkHttpUtil.getInstance(context);
    }


    /**
     * Method to get the unread message count for logged-in user.
     *
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getTotalUnReadMessageCount(final CompletionHandler<TotalCountResponse> completionHandler) {
        channelizeOkHttpUtil.getResponse(apiDefaultUrl + "messages/unread/count",
                TotalCountResponse.class, completionHandler);
    }

    /**
     * Method to forward one or more messages to single(or multiple) user or in a conversation(or multiple).
     *
     * @param messageIdsArray      Ids of messages which needs to be forward.
     * @param userIdsArray         Ids of users with whom message needs to be forwarded.
     * @param conversationIdsArray Ids of conversation with whom message needs to be forwarded.
     * @param completionHandler    Completion handler.
     */
    public void forwardMessages(JSONArray messageIdsArray, JSONArray userIdsArray,
                                JSONArray conversationIdsArray, CompletionHandler<RequestResponse> completionHandler) {
        Map<String, Object> postParam = new HashMap<>();
        //postParam.put("ownerId", Channelize.getInstance().getCurrentUserId());
        postParam.put("userIds", userIdsArray);
        postParam.put("conversationIds", conversationIdsArray);
        postParam.put("messageIds", messageIdsArray);
        channelizeOkHttpUtil.sendPostRequest(apiDefaultUrl + "messages/forward", postParam,
                RequestResponse.class, completionHandler);
    }

    /**
     * Method to send a message in a conversation or to a user.
     *
     * @param conversationMembers List of user ids which are the member of the conversation.
     * @param quotedMessage       Message instance of the message that needs to be quote.
     * @param messageQuery        PostParam which contains the message info that needs to be send.
     * @param completionHandler   Completion handler.
     */
    public void sendMessage(Set<String> conversationMembers, Message quotedMessage,
                            MessageQuery messageQuery, CompletionHandler<Message> completionHandler) {
        if (!isMessageParamValid(null, messageQuery.getMessageQueryParams(), completionHandler)) {
            return;
        }
        messageQuery.getMessageQueryParams().put("ownerId", Channelize.getInstance().getCurrentUserId());
        String attachmentType = (String) messageQuery.getMessageQueryParams().get("attachmentType");

        switch (attachmentType) {
            case ApiConstants.ATTACHMENT_LOCATION: {
                messageQuery.getMessageQueryParams().put("contentType", ApiConstants.LOCATION_CONTENT_TYPE);
                Location location = (Location) messageQuery.getMessageQueryParams().get("location");
                messageQuery.getMessageQueryParams().remove("location");
                JSONObject data = new JSONObject();
                try {
                    data.put("type", "location");
                    data.put("latitude", location.getLatitude());
                    data.put("longitude", location.getLongitude());
                    data.put("title", location.getLocationTitle());
                    data.put("address", location.getLocationDescription());

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(data);
                    messageQuery.getMessageQueryParams().put("attachments", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;

            case ApiConstants.ATTACHMENT_GIF: {
                JSONObject data = new JSONObject();
                try {
                    data.put("downsampledUrl", messageQuery.getMessageQueryParams().get("downsampledUrl"));
                    data.put("stillUrl", messageQuery.getMessageQueryParams().get("stillUrl"));
                    data.put("originalUrl", messageQuery.getMessageQueryParams().get("originalUrl"));
                    data.put("type", "gif");
                    messageQuery.getMessageQueryParams().remove("originalUrl");
                    messageQuery.getMessageQueryParams().remove("stillUrl");
                    messageQuery.getMessageQueryParams().remove("downsampledUrl");

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(data);
                    messageQuery.getMessageQueryParams().put("attachments", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case ApiConstants.ATTACHMENT_STICKER: {
                JSONObject data = new JSONObject();
                try {
                    data.put("downsampledUrl", messageQuery.getMessageQueryParams().get("downsampledUrl"));
                    data.put("stillUrl", messageQuery.getMessageQueryParams().get("stillUrl"));
                    data.put("originalUrl", messageQuery.getMessageQueryParams().get("originalUrl"));
                    data.put("type", "sticker");
                    messageQuery.getMessageQueryParams().remove("originalUrl");
                    messageQuery.getMessageQueryParams().remove("stillUrl");
                    messageQuery.getMessageQueryParams().remove("downsampledUrl");

                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(data);
                    messageQuery.getMessageQueryParams().put("attachments", jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }

        if (quotedMessage == null && messageQuery.getMessageQueryParams().containsKey("parentMessage")) {
            quotedMessage = (Message) messageQuery.getMessageQueryParams().get("parentMessage");
            messageQuery.getMessageQueryParams().remove("parentMessage");
        }

        JSONObject quotedMessageObject = new JSONObject();
        if (quotedMessage == null && messageQuery.getMessageQueryParams().containsKey("parentMessage")) {
            quotedMessage = (Message) messageQuery.getMessageQueryParams().get("parentMessage");
            messageQuery.getMessageQueryParams().remove("parentMessage");
        }

        if (quotedMessage != null) {
            try {
                quotedMessageObject.put("id", quotedMessage.getId());
                quotedMessageObject.put("conversationId", quotedMessage.getConversationId());
                quotedMessageObject.put("type", quotedMessage.getType());
                quotedMessageObject.put("createdAt", quotedMessage.getCreatedAt());
                quotedMessageObject.put("updatedAt", quotedMessage.getUpdatedAt());
                quotedMessageObject.put("ownerId", quotedMessage.getOwnerId());
                quotedMessageObject.put("isDeleted", quotedMessage.getDeleted());
                quotedMessageObject.put("owner", quotedMessage.getOwner());

                if (quotedMessage.getBody() != null) {
                    quotedMessageObject.put("body", quotedMessage.getBody());
                } else if (quotedMessage.getAttachments() != null && quotedMessage.getAttachments().size() > 0) {
                    switch (quotedMessage.getAttachments().get(0).getType()) {
                        case ApiConstants.ATTACHMENT_IMAGE: {
                            JSONObject attachment = new JSONObject();
                            attachment.put("type", ApiConstants.ATTACHMENT_IMAGE);
                            attachment.put("fileUrl", quotedMessage.getAttachments().get(0).getFileUrl());
                            attachment.put("thumbnailUrl", quotedMessage.getAttachments().get(0).getFileUrl());
                            quotedMessageObject.put("attachments", attachment);
                        }
                        break;
                        case ApiConstants.ATTACHMENT_VIDEO: {
                            JSONObject attachment = new JSONObject();
                            attachment.put("type", ApiConstants.ATTACHMENT_VIDEO);
                            attachment.put("fileUrl", quotedMessage.getAttachments().get(0).getFileUrl());
                            attachment.put("thumbnailUrl", quotedMessage.getAttachments().get(0).getFileUrl());
                            quotedMessageObject.put("attachments", attachment);
                        }
                        break;
                        case ApiConstants.ATTACHMENT_GIF: {
                            JSONObject attachment = new JSONObject();
                            attachment.put("type", ApiConstants.ATTACHMENT_GIF);
                            attachment.put("originalUrl", quotedMessage.getAttachments().get(0).getOriginalUrl());
                            quotedMessageObject.put("attachments", attachment);
                        }
                        break;
                        case ApiConstants.ATTACHMENT_STICKER: {
                            JSONObject attachment = new JSONObject();
                            attachment.put("type", ApiConstants.ATTACHMENT_STICKER);
                            attachment.put("originalUrl", quotedMessage.getAttachments().get(0).getOriginalUrl());
                            quotedMessageObject.put("attachments", attachment);
                        }
                        break;
                        case ApiConstants.ATTACHMENT_LOCATION:{
                            JSONObject attachment = new JSONObject();
                            attachment.put("type", ApiConstants.ATTACHMENT_LOCATION);
                            attachment.put("latitude", quotedMessage.getAttachments().get(0).getLatitude());
                            attachment.put("longitude", quotedMessage.getAttachments().get(0).getLongitude());
                            quotedMessageObject.put("attachments", attachment);
                        }
                        break;
                    }

                }

                messageQuery.getMessageQueryParams().put("parentMessage", quotedMessageObject);
                messageQuery.getMessageQueryParams().put("parentId", quotedMessage.getId());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        publishMessage(messageQuery.getMessageQueryParams(), conversationMembers, completionHandler);
    }

    /**
     * Method used to send a file type message (Image/Audio/Video etc)
     *
     * @param filePath            Local file path of the file which needs to be shared.
     * @param thumbnailFilePath   Local file path of the thumbnail of the main file.
     * @param conversationMembers List of user ids which are the member of the conversation.
     * @param messageQuery        PostParam which contains the message info that needs to be send.
     * @param completionHandler   Completion handler.
     */
    public void sendFileMessage(String filePath, String thumbnailFilePath, Set<String> conversationMembers,
                                MessageQuery messageQuery,
                                CompletionHandler<Message> completionHandler) {
        if (!isMessageParamValid(filePath, messageQuery.getMessageQueryParams(), completionHandler)) {
            return;
        }

        messageQuery.getMessageQueryParams().put("ownerId", Channelize.getInstance().getCurrentUserId());
        String messageId = (String) messageQuery.getMessageQueryParams().get("id");
        String attachmentType = (String) messageQuery.getMessageQueryParams().get("attachmentType");

        Map<String, Object> params = new HashMap<>();
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        params.put("name", fileName);
        if (thumbnailFilePath != null && (attachmentType.equals(ApiConstants.ATTACHMENT_IMAGE)
                || attachmentType.equals(ApiConstants.ATTACHMENT_VIDEO))) {
            JSONObject thumbnailObject = new JSONObject();
            try {
                String thumbnailName = fileName.substring(0, fileName.lastIndexOf("."));
                thumbnailObject.put("name", thumbnailName + "-thumbnail.png");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            params.put("thumbnail", thumbnailObject);
        }
        Map<String, Object> requestParam = new HashMap<>(messageQuery.getMessageQueryParams());

        channelizeOkHttpUtil.sendPostRequest(apiDefaultUrl + "files", params, null, new CompletionHandler<JSONObject>() {
            @Override
            public void onComplete(JSONObject response, ChannelizeError error) {
                if (response != null && !OkHttpUtils.getCancelledRequest().contains(messageId)) {
                    final boolean[] isFileUploaded = {false};
                    final boolean[] isThumbnailUploaded = {false};
                    String uploadUrl = response.optString("uploadUrl");
                    String fileUrl = response.optString("fileUrl");
                    String uploadThumbnailUrl = response.optString("uploadThumbnailUrl");
                    String thumbnailUrl = response.optString("thumbnailUrl");
                    channelizeOkHttpUtil.uploadFileAwsS3(uploadUrl, messageId, filePath, new CompletionHandler<RequestResponse>() {
                        @Override
                        public void onComplete(RequestResponse result, ChannelizeError error) {
                            if (result != null && result.isSuccessful()) {
                                isFileUploaded[0] = true;
                                if (isThumbnailUploaded[0]) {
                                    publishFileMessage(messageId, attachmentType, fileUrl, thumbnailUrl,
                                            filePath, conversationMembers, messageQuery.getMessageQueryParams(),
                                            completionHandler);
                                }
                            } else {
                                error.setRequestData(requestParam);
                                completionHandler.onComplete(null, error);
                            }
                        }
                    });

                    if (uploadThumbnailUrl != null && !uploadThumbnailUrl.isEmpty()
                            && thumbnailFilePath != null && new File(thumbnailFilePath).exists()) {
                        channelizeOkHttpUtil.uploadFileAwsS3(uploadThumbnailUrl, messageId, thumbnailFilePath, new CompletionHandler<RequestResponse>() {
                            @Override
                            public void onComplete(RequestResponse result, ChannelizeError error) {
                                if (result != null && result.isSuccessful()) {
                                    isThumbnailUploaded[0] = true;
                                    if (isFileUploaded[0]) {
                                        publishFileMessage(messageId, attachmentType, fileUrl, thumbnailUrl,
                                                filePath, conversationMembers, messageQuery.getMessageQueryParams(),
                                                completionHandler);
                                    }
                                } else {
                                    error.setRequestData(requestParam);
                                    completionHandler.onComplete(null, error);
                                }
                            }
                        });

                    } else {
                        isThumbnailUploaded[0] = true;
                    }
                } else if (error != null) {
                    error.setRequestData(requestParam);
                    completionHandler.onComplete(null, error);
                }
            }
        });
    }

    /**
     * Method to verify all the passed param.
     *
     * @param filePath          File path of local storage in case of media file message.
     * @param postParams        Param sent by the end user.
     * @param completionHandler Completion handler.
     */
    private boolean isMessageParamValid(String filePath, Map<String, Object> postParams, CompletionHandler<Message> completionHandler) {
        if (completionHandler == null) {
            return true;
        }
        if (postParams == null || postParams.isEmpty()) {
            completionHandler.onComplete(null, new ChannelizeError("MESSAGE_PARAM are required, Please check SDK documentation!"));
            return false;
        }

        Object objAttachmentType = postParams.get("attachmentType");
        if (objAttachmentType == null) {
            completionHandler.onComplete(null, new ChannelizeError("\"attachmentType\" is required!"));
            return false;
        }

        if (postParams.get("userId") == null && postParams.get("conversationId") == null) {
            completionHandler.onComplete(null, new ChannelizeError("\"userId\" or \"conversationId\" is required!"));
            return false;
        }
        if (postParams.get("id") == null) {
            completionHandler.onComplete(null, new ChannelizeError("\"id\" is required!"));
            return false;
        }

        String attachmentType = (String) objAttachmentType;
        switch (attachmentType) {
            case ApiConstants.ATTACHMENT_TEXT:
                if (postParams.get("body") == null) {
                    completionHandler.onComplete(null, new ChannelizeError("\"body\" is required!"));
                    return false;
                }
                break;

            case ApiConstants.ATTACHMENT_LOCATION:
                if (postParams.get("location") == null) {
                    completionHandler.onComplete(null, new ChannelizeError("\"location\" is required!"));
                    return false;
                }
                break;

            case ApiConstants.ATTACHMENT_GIF:
            case ApiConstants.ATTACHMENT_STICKER:
                if (postParams.get("originalUrl") == null || postParams.get("stillUrl") == null
                        || postParams.get("downsampledUrl") == null) {
                    completionHandler.onComplete(null, new ChannelizeError("\"originalUrl\", \"stillUrl\", and \"downsampledUrl\" are required!"));
                    return false;
                }
                break;

            case ApiConstants.ATTACHMENT_IMAGE:
            case ApiConstants.ATTACHMENT_AUDIO:
            case ApiConstants.ATTACHMENT_VIDEO:
                if (filePath == null || filePath.isEmpty()) {
                    completionHandler.onComplete(null, new ChannelizeError("\"FILE_PATH\" is required!"));
                    return false;
                }
                break;
        }
        return true;
    }

    /**
     * Method to publish the message via mqtt or send it through API if no conversation.
     *
     * @param messageId           Id of the message that need to be send.
     * @param attachmentType      Type of the message attachment.
     * @param fileUrl             S3 url of the file that has been uploaded in previous step.
     * @param thumbnailUrl        S3 url of the thumbnail image that has been uploaded in previous step.
     * @param filePath            Local file path of the original file that has been uploaded on S3.
     * @param conversationMembers List of user ids which are the member of the conversation.
     * @param postParams          PostParam which contains the message info that needs to be send.
     * @param completionHandler   Completion handler.
     */
    private void publishFileMessage(String messageId, String attachmentType, String fileUrl, String thumbnailUrl,
                                    String filePath, Set<String> conversationMembers,
                                    Map<String, Object> postParams, CompletionHandler<Message> completionHandler) {
        try {
            postParams.put("attachmentType", attachmentType);

            //Set Id
            postParams.put("id", messageId);

            //Set Type
            postParams.put("type", "normal");

            //Set ownerId
            postParams.put("ownerId", Channelize.getInstance().getCurrentUserId());

            postParams.put("ownerName", ChannelizePreferences.getCurrentUserName(Channelize.getInstance().getContext()));
            //postParams.put("status", "success");

            //Set Attachment JSON Array to postParam
            // Add file data.
            JSONObject fileData = new JSONObject();
            fileData.put("name", filePath.substring(filePath.lastIndexOf("/") + 1));
            fileData.put("mimeType", CoreFunctionsUtil.getMimeType(filePath));
            fileData.put("extension", filePath.substring(filePath.lastIndexOf(".") + 1));
            fileData.put("size", new File(filePath).length());
            fileData.put("type", attachmentType);

            if (thumbnailUrl != null) {
                fileData.put("thumbnailUrl", thumbnailUrl);
            }
            fileData.put("fileUrl", fileUrl);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(fileData);

            postParams.put("attachments", jsonArray);

            if (attachmentType.equals(ApiConstants.ATTACHMENT_AUDIO)) {
                Uri uri = Uri.parse(filePath);
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(Channelize.getInstance().getContext(), uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                fileData.put("duration", String.valueOf(Long.parseLong(durationStr)));
            }
            //postParams.put("fileData", fileData);

            if (!OkHttpUtils.getCancelledRequest().contains(messageId)) {
                publishMessage(postParams, conversationMembers, completionHandler);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to publish the message data through mqtt or api call.
     *
     * @param postParams          PostParam which contains the message info that needs to be send.
     * @param conversationMembers List of user ids which are the member of the conversation.
     * @param completionHandler   Completion handler.
     */
    private void publishMessage(Map<String, Object> postParams, Set<String> conversationMembers, CompletionHandler<Message> completionHandler) {

        channelizeOkHttpUtil.sendMessage(postParams, Message.class, completionHandler);

        /*Channelize channelize = Channelize.getInstance();
        // Sending quick message if the mqtt is connected.
        if (channelize.isConnected() && conversationMembers != null
                && !conversationMembers.isEmpty() && postParams.containsKey("chatId")
                && postParams.get("chatId") != null) {
            postParams = getParamWithOwnerObject(postParams);
            String chatRoomId = (String) postParams.get("chatId");
            JSONObject parameter = new JSONObject(

            );

            Channelize.publishMqttData("users/server/conversation/messages", parameter);
            new Thread(() -> {
                try {
                    for (String userId : conversationMembers) {
                        Channelize.publishMqttData("users/" + userId + "/conversation/messages", parameter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();


            //TODO, when sending message through the mqtt then instantly sending a message object.
            //TODO, in future need to manage this via mqtt callbacks.
            try {
                if (completionHandler != null) {
                    Message message = readJsonResponse(new JSONObject(postParams).toString(), Message.class);
//                message.setCreatedAt(CoreFunctionsUtil.getCurrentTimeStamp());
                    message.setInstantMsg(true);
                    completionHandler.onComplete(message, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            channelizeOkHttpUtil.sendMessage(postParams, Message.class, completionHandler);
        }*/
    }

    /**
     * Method used to append the owner object with message.
     *
     * @param postParams PostParam which contains the message info that needs to be send.
     * @return Returns the updated map.
     */
    private Map<String, Object> getParamWithOwnerObject(Map<String, Object> postParams) {
        JSONObject ownerObject = new JSONObject();
        try {
            //ownerObject.put("id", selfId);
            ownerObject.put("id", Channelize.getInstance().getCurrentUserId());
            ownerObject.put("displayName", ChannelizePreferences.getCurrentUser(Channelize.getInstance().getContext()).getDisplayName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postParams.put("owner", ownerObject);
        return postParams;
    }

    /**
     * Method to mark received message as read.
     *
     * @param conversationId    ConversationId of the conversation in which that message exist.
     * @param messageId         MessageId of that message which needs to be marked as read.
     * @param messageOwnerId    Owner Id of the message.
     * @param completionHandler Completion handler which will invoked when the operation performed successfully.
     */
    public void markMessageRead(String conversationId, String messageId, String messageOwnerId, CompletionHandler<RequestResponse> completionHandler) {
        Map<String, Object> postParam = new HashMap<>();
        postParam.put("messageId", messageId);
        //postParam.put("userId", selfId);
        postParam.put("userId", Channelize.getInstance().getCurrentUserId());
        postParam.put("chatId", conversationId);
        postParam.put("userMessageStatus", 3);

        Channelize channelize = Channelize.getInstance();
        // Sending quick alert of read message if the mqtt is connected.
       /* if (channelize.isConnected()) {
            JSONObject parameter = new JSONObject(postParam);

            Channelize.publishMqttData("server/conversation/mark-as-read", parameter);
            Channelize.publishMqttData("message-owner/" + messageOwnerId + "/conversation/mark-as-read", parameter);
            //Channelize.publishMqttData("self/" + selfId + "/conversation/mark-as-read", parameter);
            Channelize.publishMqttData("self/" + Channelize.getInstance().getCurrentUserId() + "/conversation/mark-as-read", parameter);

            if (completionHandler != null) {
                completionHandler.onComplete(new RequestResponse(true), null);
            }
        } else {
//            postParam.put("messageStatus", 3);
            String messageReadUrl = apiDefaultUrl + "messages/" + messageId + "/mark_as_read";
            channelizeOkHttpUtil.sendPutRequest(messageReadUrl, null, RequestResponse.class, completionHandler);
        }*/

        String messageReadUrl = apiDefaultUrl + "messages/" + messageId + "/mark_as_read";
        channelizeOkHttpUtil.sendPutRequest(messageReadUrl, null, RequestResponse.class, completionHandler);
    }

    /**
     * Method to delete the messages.
     *
     * @param messageIdsArray   Ids of messages that you want to delete.
     * @param completionHandler Completion handler that will return the response after the completion of request.
     */
    public void deleteMessages(JSONArray messageIdsArray, CompletionHandler<RequestResponse> completionHandler) {
        Map<String, Object> param = new HashMap<>();
        param.put("messageIds", messageIdsArray);
        String actionUrl = apiDefaultUrl + "messages/delete";
        channelizeOkHttpUtil.sendPostRequest(actionUrl, param, RequestResponse.class, completionHandler);
    }


    public void deleteMessageForEveryone(JSONArray messageIdsArray, CompletionHandler<RequestResponse> completionHandler) {
        Map<String, Object> param = new HashMap<>();
        param.put("messageIds", messageIdsArray);
        param.put("publish", true);
        String actionUrl = apiDefaultUrl + "messages/delete_for_everyone";
        channelizeOkHttpUtil.sendPostRequest(actionUrl, param, RequestResponse.class, completionHandler);
    }

    /**
     * Method to get the generic response from the response body.
     *
     * @param responseBody  ResponseBody which contains the response.
     * @param responseClass Class in which data needs to be parsed.
     * @return Returns the model.
     * @throws IOException Throws IOException if any error occurred during parsing.
     */
    private <T extends GenericResponse> T readJsonResponse(String responseBody, @NonNull Class<T> responseClass) throws IOException {
        return GSON_INSTANCE.fromJson(responseBody, responseClass);
    }
}
