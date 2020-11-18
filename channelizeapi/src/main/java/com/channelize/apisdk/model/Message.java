/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.model;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.TextUtils;

import com.channelize.apisdk.ApiConstants;
import com.channelize.apisdk.utils.ChannelizePreferences;
import com.channelize.apisdk.utils.CoreFunctionsUtil;
import com.google.gson.annotations.SerializedName;
import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.R;
import com.channelize.apisdk.Utils;
import com.channelize.apisdk.network.response.GenericResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class Message implements Parcelable, GenericResponse {

    // Member variables.
    @SerializedName("id")
    private String id;//Okay

    @SerializedName("conversationId")
    private String conversationId;//Okay

    @SerializedName("type")
    private String type;//Okay

    @SerializedName("createdAt")
    private String createdAt;//Okay

    @SerializedName("updatedAt")
    private String updatedAt;//Okay

    @SerializedName("body")
    private String body;//Okay

    @SerializedName("ownerId")
    private String ownerId;//Okay

    @SerializedName("attachments")
    private List<Attachment> attachments = null;

    @SerializedName("isDeleted")
    private Boolean isDeleted;//Okay

    @SerializedName("owner")
    private User messageOwnerModel;//Okay

    @SerializedName("parentId")
    private String parentId;//Okay

    @SerializedName("parentMessage")
    private Message parentMessage;  //Okay

    @SerializedName("originalMessageId")
    private String originalMessageId;

    @SerializedName("mentionedUsers")
    private List<MentionedUser> mentionedUsers = null;//Okay

    @SerializedName("recipients")
    private Object mRecipents;

    @SerializedName("filePath")
    private String filePath; //Okay This is for Image and Video when we create a message then for showing the image while response does not comes from MQTT we will show the image from filePath

    //This variable will check the status of message
    // 0 message has been send but not known whether it is send from api end or not
    // 1 message send from your end validated by the api side
    // 2 message is read by the end users
    private int messageSendStatus=0;  //Okay

    private boolean tempMessageStatus=false;


    @SerializedName("parentMsgId")
    private String parentMsgId;
    @SerializedName("attachmentType")
    private String attachmentType;

    @SerializedName("downsampledUrl")
    private String gifStickerUrl;
    @SerializedName("originalUrl")
    private String originalUrl;
    @SerializedName("metaMessageType")
    private String metaMessageType;
    @SerializedName("status")
    private String status;
    @SerializedName("contentType")
    private int contentType;
    @SerializedName("quoted")
    private boolean isQuotedMessage;
    @SerializedName("isRetry")
    private boolean isRetry;
    @SerializedName("_metaMessageData")
    private MetaMessage metaMessageModel;

    @SerializedName("data")
    private Location location;
    @SerializedName("quotedMessage")
    private Message quotedMessage;
    @SerializedName(value="attachment", alternate={"fileData"})
    private Attachment attachment;
    @SerializedName("tags")
    private List<Tag> tagsList;
    //@SerializedName("recipients")
    private List<MessageRecipient> recipientsList;

    private String locationTitle;
    private String locationDescription;

    private String metaMessage;
    private String tempCreatedAt;

    private String attachmentImageUrl, thumbnailUrl, savedFile, mapUrl,
            locationImageUrl, quotedMessageOwner, quotedMessageBody, qmAttachmentType,
            quotedMessageImage, typingText, fileSize;
    private boolean isShowProgressBar, isMessageSendingFailed, isSongPlaying, isMusicLoaded,
            isUnreadMessage, isMetaDataMessage, isDownloading, gifAnimationShowing,
            isQuotedMessageClicked = false, isTypingMessage = false, isBodyProcessed = false,
            isInstantMsg = false;
    private int messageStatus, downloadProgress;
    private long createdTimeStamp, duration;
    private double latitude = 0, longitude = 0;
    private HashMap<String, Integer> recipientsMap;
    private HashMap<String, Integer> msgRecipients = new HashMap<>();
    private List<Message> messageList;
    private HashMap<String, JSONObject> taggedMemberClickableMap = new HashMap<>();


    public Message() {
    }

    /**
     * Public constructor to initialize the Message with the List of models.
     *
     * @param messageList List of Message, which contains the list of chat room messages.
     */
    public Message(List<Message> messageList) {
        this.id = "";
        this.conversationId = "";
        this.messageList = messageList;
    }

    /**
     * Public constructor to initialize the Message with the messageId.
     *
     * @param messageId MessageId for which Message instance needs to be created.
     */
    public Message(String messageId) {
        this.id = messageId;
    }

    /**
     * Public constructor to initialize the Message with the info
     *
     * @param messageId      MessageId for which Message instance needs to be created.
     * @param createdAt      Time stamp of the message.
     * @param attachmentType Attachment type of the message.
     * @param body           Message body, if its text type message.
     * @param filePath       Local file path if its media type message.
     * @param location       Location instance which contains the info of that particular place.
     * @param quotedMessage  Message instance which contains the quoted message info.
     * @param tagsArray      JSONArray which contains the tagged member info.
     * @param mListOfMembers List of all User models.
     */

    public Message(String messageId, String createdAt, String attachmentType, String body,
                   String filePath, Location location, Message quotedMessage,
                   JSONArray tagsArray, List<Member> mListOfMembers) {
        //Set the message ID
        this.id = messageId;

        //Set the Type of Message i.e Normal, Admin
        this.type=ApiConstants.ATTACHMENT_NORMAL;

        //Set the Created Time of Message
        this.createdAt = createdAt;
        this.tempCreatedAt = createdAt;

        //Set the Body of Message
        if(body!=null){
            this.body = body;
        }

        //Set the OwnerId of Message
        this.ownerId = Channelize.getInstance().getCurrentUserId();

        //Set is Deleted
        this.isDeleted=false;

        //Set the Send Status of Message
        this.messageSendStatus=0;

        //Set the Owner Model
        if(mListOfMembers!=null && mListOfMembers.size()>0){
            User ownerOfMessage=new User();
            for(int i=0;i<mListOfMembers.size();i++){
                if(mListOfMembers.get(i).getUser().getId().equalsIgnoreCase(Channelize.getInstance().getCurrentUserId())){
                    ownerOfMessage.setId(mListOfMembers.get(i).getUser().getId());
                    ownerOfMessage.setProfileImageUrl(mListOfMembers.get(i).getUser().getProfileImageUrl());
                    ownerOfMessage.setDisplayName(mListOfMembers.get(i).getUser().getDisplayName());
                    break;
                }
            }

            this.messageOwnerModel=ownerOfMessage;
        }

        List<Attachment> mAttachmentList=new ArrayList<>();
        Attachment attachment=new Attachment();

        //Set the Attachment
        switch (attachmentType) {
            case ApiConstants.ATTACHMENT_TEXT:
                this.status = ApiConstants.STATUS_SUCCESS;
                break;
            case ApiConstants.ATTACHMENT_STICKER:
                attachment.setType(ApiConstants.ATTACHMENT_STICKER);
                attachment.setDownsampledUrl(filePath);
                attachment.setOriginalUrl(filePath);
                attachment.setStillUrl(filePath);
                this.status = ApiConstants.STATUS_SUCCESS;
                break;
            case ApiConstants.ATTACHMENT_GIF:
                attachment.setType(ApiConstants.ATTACHMENT_GIF);
                attachment.setDownsampledUrl(filePath);
                attachment.setOriginalUrl(filePath);
                attachment.setStillUrl(filePath);
                this.status = ApiConstants.STATUS_SUCCESS;
                break;
            case ApiConstants.ATTACHMENT_LOCATION:
                attachment.setType(ApiConstants.ATTACHMENT_LOCATION);
                attachment.setLatitude(location.getLatitude());
                attachment.setLongitude(location.getLongitude());
                attachment.setAddress(location.getLocationDescription());
                attachment.setTitle(location.getLocationTitle());
                attachment.setMapUrl("http://maps.google.com/maps/api/staticmap?maptype=roadmap"
                        + "&center=" + location.getLatitude() + "," + location.getLongitude()
                        + "&markers=" + location.getLatitude() + "," + location.getLongitude()
                        + "&zoom=16&size=720x720&sensor=false" + "&key=" + Channelize.getInstance().getGooglePlacesKey());
                this.status = ApiConstants.STATUS_SUCCESS;
                break;
            case ApiConstants.ATTACHMENT_IMAGE:
                this.status = ApiConstants.STATUS_PENDING;
                attachment.setType(ApiConstants.ATTACHMENT_IMAGE);
                attachment.setFilePath(filePath);
                this.status = ApiConstants.STATUS_PENDING;
                break;
            case ApiConstants.ATTACHMENT_VIDEO:
                this.status = ApiConstants.STATUS_PENDING;
                attachment.setType(ApiConstants.ATTACHMENT_VIDEO);
                attachment.setFilePath(filePath);
                this.status = ApiConstants.STATUS_PENDING;
                break;
            case ApiConstants.ATTACHMENT_AUDIO:
                this.status = ApiConstants.STATUS_PENDING;
                attachment.setType(ApiConstants.ATTACHMENT_AUDIO);
                attachment.setFilePath(filePath);
                this.status = ApiConstants.STATUS_PENDING;
                break;
            default:
                this.status = ApiConstants.STATUS_PENDING;
                break;
        }

        mAttachmentList.add(attachment);
        this.attachments=mAttachmentList;

        if (quotedMessage != null) {
            isQuotedMessage = true;
            parentMsgId = quotedMessage.getId();
            setQuotedMessage(quotedMessage);
        }

        //setCreatedAt();
        if (tagsArray != null && tagsArray.length() > 0) {
            tagsList = new ArrayList<>();
            for (int i = 0; i < tagsArray.length(); i++) {
                JSONObject jsonObject = tagsArray.optJSONObject(i);
                Tag tag = new Tag(jsonObject);
                tagsList.add(tag);
            }
        }

        this.conversationId = "";
    }

   /* public Message(String messageId, String createdAt, String attachmentType, String body,
                   String filePath, Location location, Message quotedMessage,
                   JSONArray tagsArray, Map<String, Member> chatMembersMap) {
        this.id = messageId;
        this.createdAt = createdAt;
        this.tempCreatedAt = createdAt;
        this.ownerId = Channelize.getInstance().getCurrentUserId();
        this.attachmentType = attachmentType;
        this.body = body;
        this.filePath = filePath;
        if (quotedMessage != null) {
            isQuotedMessage = true;
            parentMsgId = quotedMessage.getId();
            setQuotedMessage(quotedMessage);
        }
        switch (attachmentType) {
            case ApiConstants.ATTACHMENT_TEXT:
                this.status = ApiConstants.STATUS_SUCCESS;
                break;
            case ApiConstants.ATTACHMENT_STICKER:
            case ApiConstants.ATTACHMENT_GIF:
                this.gifStickerUrl = filePath;
                this.status = ApiConstants.STATUS_SUCCESS;
                break;
            case ApiConstants.ATTACHMENT_LOCATION:
                this.status = ApiConstants.STATUS_SUCCESS;
                buildLocationUrl(location);
                break;
            case ApiConstants.ATTACHMENT_IMAGE:
                this.status = ApiConstants.STATUS_PENDING;
                Attachment attachment=new Attachment();
                attachment.setType(ApiConstants.ATTACHMENT_IMAGE);
                attachment.setFilePath(filePath);
                List<Attachment> mAttachmentList=new ArrayList<>();
                mAttachmentList.add(attachment);
                this.attachments=mAttachmentList;
                break;
            default:
                this.status = ApiConstants.STATUS_PENDING;
                break;
        }
        setCreatedAt();
        if (tagsArray != null && tagsArray.length() > 0) {
            tagsList = new ArrayList<>();
            for (int i = 0; i < tagsArray.length(); i++) {
                JSONObject jsonObject = tagsArray.optJSONObject(i);
                Tag tag = new Tag(jsonObject);
                tagsList.add(tag);
            }
        }

        this.conversationId = "";
    }*/

    /**
     * Public constructor to initialize the Message with the info when the message sending gets failed.
     *
     * @param messageObject   JSON object, which contains all the message info.
     * @param isRetryResponse True, if the message sending gets failed.
     */
    public Message(JSONObject messageObject, boolean isRetryResponse) {
        this.createdAt = messageObject.optString("createdAt");
        this.tempCreatedAt = messageObject.optString("createdAt");
        this.ownerId = messageObject.optString("ownerId");
        this.attachmentType = messageObject.optString("attachmentType");
        this.body = messageObject.optString("body");
        this.filePath = messageObject.optString("filePath");
        this.id = messageObject.optString("id");
        this.conversationId = "";
        this.isMessageSendingFailed = true;
        //setCreatedAt();
    }

    /**
     * Public constructor to initialize the Message with the progress bar type message.
     *
     * @param isShowProgressBar True, if need to show the progress bar.
     */
    public Message(boolean isShowProgressBar) {
        this.isShowProgressBar = isShowProgressBar;
        this.messageStatus = -1;
    }

    protected Message(Parcel in) {
        id = in.readString();
        conversationId = in.readString();
        type = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        body = in.readString();
        ownerId = in.readString();
        attachments = in.createTypedArrayList(Attachment.CREATOR);
        byte tmpIsDeleted = in.readByte();
        isDeleted = tmpIsDeleted == 0 ? null : tmpIsDeleted == 1;
        messageOwnerModel = in.readParcelable(User.class.getClassLoader());
        parentId = in.readString();
        parentMessage = in.readParcelable(Message.class.getClassLoader());
        originalMessageId = in.readString();
        mentionedUsers = in.createTypedArrayList(MentionedUser.CREATOR);
        filePath = in.readString();
        messageSendStatus = in.readInt();
        tempMessageStatus = in.readByte() != 0;
        parentMsgId = in.readString();
        attachmentType = in.readString();
        gifStickerUrl = in.readString();
        originalUrl = in.readString();
        metaMessageType = in.readString();
        status = in.readString();
        contentType = in.readInt();
        isQuotedMessage = in.readByte() != 0;
        isRetry = in.readByte() != 0;
        metaMessageModel = in.readParcelable(MetaMessage.class.getClassLoader());
        location = in.readParcelable(Location.class.getClassLoader());
        quotedMessage = in.readParcelable(Message.class.getClassLoader());
        attachment = in.readParcelable(Attachment.class.getClassLoader());
        tagsList = in.createTypedArrayList(Tag.CREATOR);
        recipientsList = in.createTypedArrayList(MessageRecipient.CREATOR);
        locationTitle = in.readString();
        locationDescription = in.readString();
        metaMessage = in.readString();
        tempCreatedAt = in.readString();
        attachmentImageUrl = in.readString();
        thumbnailUrl = in.readString();
        savedFile = in.readString();
        mapUrl = in.readString();
        locationImageUrl = in.readString();
        quotedMessageOwner = in.readString();
        quotedMessageBody = in.readString();
        qmAttachmentType = in.readString();
        quotedMessageImage = in.readString();
        typingText = in.readString();
        fileSize = in.readString();
        isShowProgressBar = in.readByte() != 0;
        isMessageSendingFailed = in.readByte() != 0;
        isSongPlaying = in.readByte() != 0;
        isMusicLoaded = in.readByte() != 0;
        isUnreadMessage = in.readByte() != 0;
        isMetaDataMessage = in.readByte() != 0;
        isDownloading = in.readByte() != 0;
        gifAnimationShowing = in.readByte() != 0;
        isQuotedMessageClicked = in.readByte() != 0;
        isTypingMessage = in.readByte() != 0;
        isBodyProcessed = in.readByte() != 0;
        isInstantMsg = in.readByte() != 0;
        messageStatus = in.readInt();
        downloadProgress = in.readInt();
        createdTimeStamp = in.readLong();
        duration = in.readLong();
        latitude = in.readDouble();
        longitude = in.readDouble();
        messageList = in.createTypedArrayList(Message.CREATOR);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    /**
     * Method to perform post processing over member variables once the object has been created.
     */
    public void postProcess() {
        // Checking if the message contains the recipients or no (MQTT messages does not contains the recipients)
        String selfId = Channelize.getInstance().getCurrentUserId();
        if (recipientsList != null && recipientsList.size() > 0) {
            // Adding recipients info.
            recipientsMap = new HashMap<>();
            for (MessageRecipient messageRecipient : recipientsList) {
                recipientsMap.put(messageRecipient.getRecipientId(),
                        messageRecipient.getStatus());
                if (messageRecipient.getRecipientId().equals(selfId)) {
                    createdAt = messageRecipient.getCreatedAt();
                    if (createdAt == null || createdAt.isEmpty()) {
                        createdAt = CoreFunctionsUtil.getCurrentTimeStamp();
                    }
                    this.tempCreatedAt = createdAt;
                }
            }
            //At recent chat page it is updating for each member list.
            if (recipientsMap.containsKey(selfId)) {
                messageStatus = recipientsMap.get(selfId);
            }
            checkMsgRecipientsList();
        } else {
            // When the message is received through MQTT then setting the message time with current time.
            this.createdAt = CoreFunctionsUtil.getCurrentTimeStamp();
            this.tempCreatedAt = createdAt;
            this.messageStatus = ownerId.equals(selfId) ? 1 : 0;
        }
        this.gifStickerUrl = gifStickerUrl != null ? gifStickerUrl : originalUrl;

        // Fetching attachment values.
        if (attachment != null) {
            thumbnailUrl = attachment.getThumbnailUrl();
            attachmentImageUrl = attachment.getFileUrl();
            filePath = attachment.getFilePath();
            duration = (long) attachment.getDuration();
            fileSize = attachment.getSize();
        }

        // Checking if it is quoted message then getting value from it.
        if (quotedMessage != null) {
            isQuotedMessage = true;
            setQuotedMessage(quotedMessage);
        }

        // If it is meta message.
        if (metaMessageModel != null && metaMessageType != null) {
            isMetaDataMessage = true;
            attachmentType = "";
        }

        // If its location attachment then processing the data.
        if (attachmentType != null && attachmentType.equals(ApiConstants.ATTACHMENT_LOCATION)
                && location != null) {
            buildLocationUrl(location);
        }

        if (isMessageDeleted() != null
                && isMessageDeleted()) {
           setBody(Channelize.getInstance().getContext().getResources().getString(R.string.message_was_deleted));
        }

        //setUpdatedAt Time
        //setCreatedAt();
    }

    public void processBody(Map<String, Member> chatMembersMap) {
        if (!isBodyProcessed && chatMembersMap != null && !chatMembersMap.isEmpty()) {
            if (tagsList != null && tagsList.size() > 0) {
                getTaggedMemberInfo(chatMembersMap);
            }
            checkForMetaMessages(chatMembersMap);
            isBodyProcessed = true;
        }
    }

    private void setQuotedMessage(Message quotedMessage) {
        if (quotedMessage != null) {
            Channelize channelize = Channelize.getInstance();
            Context context = channelize.getContext();
            quotedMessageOwner = quotedMessage.getOwnerId().equals(channelize.getCurrentUserId())
                    ? context.getResources().getString(R.string.pm_self_member_text)
                    : CoreFunctionsUtil.capitalizeTitle(quotedMessage.getMessageOwnerModel().getDisplayName());

            if(quotedMessage.getBody()!=null){
                quotedMessageBody = quotedMessage.getBody();
            }

            if (quotedMessage.getAttachments() != null && quotedMessage.getAttachments().size() > 0) {
                qmAttachmentType = quotedMessage.getAttachments().get(0).getType();
                switch (qmAttachmentType) {
                    case ApiConstants.ATTACHMENT_IMAGE:
                        quotedMessageBody = context.getResources().getString(R.string.pm_image);
                        quotedMessageImage = quotedMessage.getAttachments().get(0).getFileUrl();
                        break;
                    case ApiConstants.ATTACHMENT_VIDEO:
                        quotedMessageBody = context.getResources().getString(R.string.pm_video);
                        quotedMessageImage = quotedMessage.getAttachments().get(0).getFileUrl();
                        break;
                    case ApiConstants.ATTACHMENT_AUDIO:
                        quotedMessageBody = context.getResources().getString(R.string.pm_audio);
                        quotedMessageImage = quotedMessage.getAttachments().get(0).getFileUrl();
                        break;
                    case ApiConstants.ATTACHMENT_FILE:
                        quotedMessageBody = context.getResources().getString(R.string.pm_document);
                        break;
                    case ApiConstants.ATTACHMENT_STICKER:
                        quotedMessageBody = context.getResources().getString(R.string.pm_sticker);
                        quotedMessageImage = quotedMessage.getAttachments().get(0).getOriginalUrl();
                        break;
                    case ApiConstants.ATTACHMENT_GIF:
                        quotedMessageBody = context.getResources().getString(R.string.pm_gif);
                        quotedMessageImage = quotedMessage.getAttachments().get(0).getOriginalUrl();
                        break;
                    case ApiConstants.ATTACHMENT_LOCATION:
                        quotedMessageBody = context.getResources().getString(R.string.pm_location);
                        if (quotedMessage.getLocation() != null) {
                            quotedMessageImage = "http://maps.google.com/maps/api/staticmap?maptype=roadmap"
                                    + "&center=" + quotedMessage.getAttachments().get(0).getLatitude() + "," + quotedMessage.getAttachments().get(0).getLongitude()
                                    + "&markers=" + quotedMessage.getAttachments().get(0).getLatitude() + ","
                                    + quotedMessage.getAttachments().get(0).getLongitude() + "&zoom=20&size=60x60&sensor=false"
                                    + "&key=" + Channelize.getInstance().getGooglePlacesKey();
                        }
                        break;
                }
            }
        }

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean isMessageDeleted() {
        return isDeleted;
    }

    public void setMessageDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public boolean isRetry() {
        return isRetry;
    }

    public void setRetry(boolean retry) {
        isRetry = retry;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public void getFileFromLocalStorage() {
        Channelize channelize = Channelize.getInstance();
        String root = Environment.getExternalStorageDirectory().toString();
        String pathDirectory = root + "/" + channelize.getContext().getResources().getString(R.string.app_name)
                + "/" + channelize.getContext().getResources().getString(R.string.pm_saved_images);
        File file = new File(pathDirectory + File.separator + id + ".jpg");
        if (file.exists()) {
            savedFile = file.getAbsolutePath();
        }
    }

    public void setSavedFile(String savedFile) {
        this.savedFile = savedFile;
    }

    public String getSavedFile() {
        return savedFile;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public String getAttachmentImageUrl() {
        return attachmentImageUrl;
    }

    public String getGifStickerUrl() {
        return gifStickerUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTempCreatedAt() {
        return tempCreatedAt;
    }

    public void setTempCreatedAt(String tempCreatedAt) {
        this.tempCreatedAt = tempCreatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        //setCreatedAt();
    }

    // Setting the time of the message in AM/PM and getting the timestamp(in long) also.
    /*public void setCreatedAt() {
        if (createdAt != null && !createdAt.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                        Locale.getDefault());
                Date date = format.parse(createdAt.replaceAll("Z$", "+0000"));

                createdTimeStamp = date.getTime();
                String time = Utils.getTimestamp(createdTimeStamp,
                        Channelize.getInstance().getContext());
                String msgDate = Utils.getDateFromTimeStamp(createdTimeStamp);
                String today = Utils.getCurrentTimestamp();

                String[] time1 = msgDate.split("/");
                String[] time2 = today.split("/");
                if ((time1[0] + time1[1] + time1[2]).equals(time2[0] + time2[1] + time2[2])) {
                    createdAt = time;
                } else {
                    createdAt = msgDate;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }*/

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public User getMessageOwnerModel() {
        return messageOwnerModel;
    }

    public void checkForMetaMessages(Map<String, Member> chatMembersMap) {
        Channelize channelize = Channelize.getInstance();
        Context context = channelize.getContext();
        if (metaMessageType != null && !metaMessageType.isEmpty() && metaMessageModel != null
                && chatMembersMap != null && !chatMembersMap.isEmpty()) {
            isMetaDataMessage = true;
            attachmentType = "";
            String ownerId = metaMessageModel.getOwnerId();
            String ownerName = chatMembersMap.get(ownerId).getUser().getDisplayName();
            if (ownerId.equals(channelize.getCurrentUserId())) {
                ownerName = context.getResources().getString(R.string.pm_self_member_text);
            }
            String objectValue = null;
            //TODO, check once the issue fixed(User id coming in integer)
            if (metaMessageModel.getObjValues() instanceof String) {
                objectValue = (String) metaMessageModel.getObjValues();
            } else if (metaMessageModel.getObjValues() instanceof Double) {
                Double obj = (Double) metaMessageModel.getObjValues();
                objectValue = String.valueOf(obj.intValue());
            }
            String objectName;

            switch (metaMessageType) {
                case ApiConstants.META_GROUP_CREATE:
                    metaMessage = String.format(context.getResources().getString(R.string.pm_meta_group_create),
                            ownerName, objectValue);
                    break;

                case ApiConstants.META_GROUP_LEAVE:
                    metaMessage = String.format(context.getResources().getString(R.string.pm_meta_group_leave),
                            ownerName);
                    break;

                case ApiConstants.META_GROUP_MAKE_ADMIN:
                    metaMessage = String.format(context.getResources().getString(R.string.pm_meta_group_make_admin),
                            ownerName);
                    break;

                case ApiConstants.META_GROUP_CHANGE_TITLE:
                    metaMessage = String.format(context.getResources().getString(R.string.pm_meta_group_change_title),
                            ownerName, objectValue);
                    break;

                case ApiConstants.META_GROUP_CHANGE_PHOTO:
                    metaMessage = String.format(context.getResources().getString(R.string.pm_meta_group_change_photo),
                            ownerName);
                    break;

                case ApiConstants.META_GROUP_ADD_MEMBERS:
                case ApiConstants.META_GROUP_REMOVE_MEMBERS:
                    List<String> members = new ArrayList<>();
                    List<String> membersArrayList = (List<String>) metaMessageModel.getObjValues();
                    if (membersArrayList != null && membersArrayList.size() > 0) {
                        for (String userId : membersArrayList) {
                            User user = null;
                            if (chatMembersMap.get(userId) != null) {
                                user = chatMembersMap.get(userId).getUser();
                            }
                            // TODO, remove the if condition code once issue resolved from api.
                            if (user == null) {
                                user = new User();
                                user.setId("");
                                user.setDeletedUser(true);
                                user.setDisplayName(context.getResources().getString(R.string.pm_deleted_user));
                            }
                            String userName = user.getDisplayName();
                            if (userName != null && !userName.isEmpty()) {
                                if (user.getId().equals(channelize.getCurrentUserId())) {
                                    userName = context.getResources().getString(R.string.pm_self_member_text);
                                }
                                members.add(" " + userName);
                            }
                        }
                        Collections.sort(members);
                        String participants = TextUtils.join(",", members).trim();
                        participants = participants.substring(0, participants.endsWith(",")
                                ? participants.length() - 1 : participants.length());
                        if (members.size() > 1) {
                            int lastIndex = participants.lastIndexOf(",");
                            participants = participants.substring(0, lastIndex)
                                    + " " + context.getResources().getString(R.string.pm_and_text)
                                    + participants.substring(lastIndex + 1, participants.length());
                        }

                        String format;
                        if (metaMessageType.equals(ApiConstants.META_GROUP_ADD_MEMBERS)) {
                            format = context.getResources().getString(R.string.pm_meta_group_add_members);
                        } else {
                            format = context.getResources().getString(R.string.pm_meta_group_remove_members);
                        }
                        metaMessage = String.format(format, ownerName, participants);
                    }
                    break;

                case ApiConstants.META_CALL_VOICE_MISSED:

                    objectName = chatMembersMap.get(objectValue).getUser().getDisplayName();
                    if (objectValue.equals(channelize.getCurrentUserId())) {
                        objectName = context.getResources().getString(R.string.pm_self_member_text);
                    }
                    metaMessage = String.format(context.getResources().getString(R.string.pm_user_missed_voice_call),
                            ownerName);
                    break;

                case ApiConstants.META_CALL_VIDEO_MISSED:
                    objectName = chatMembersMap.get(objectValue).getUser().getDisplayName();
                    if (objectValue.equals(channelize.getCurrentUserId())) {
                        objectName = context.getResources().getString(R.string.pm_self_member_text);
                    }
                    metaMessage = String.format(context.getResources().getString(R.string.pm_user_missed_video_call),
                            ownerName);
                    break;
            }
        }
    }

    public HashMap<String, Integer> getRecipientsMap() {
        return recipientsMap;
    }

    // Set message recipient list when message is fetched from api and message is not read yet.
    // Because we need to show the msg status change according to all members activity.
    private void checkMsgRecipientsList() {
        // Creating msg recipients list only when the message is not read.
        String selfId = Channelize.getInstance().getCurrentUserId();
        if (ownerId != null && ownerId.equals(selfId) && recipientsMap != null
                && recipientsMap.containsKey(selfId)
                && recipientsMap.get(selfId) != 3) {
            for (Map.Entry<String, Integer> recipient : recipientsMap.entrySet()) {
                String userId = recipient.getKey();
                if (!userId.equals(selfId)) {
                    msgRecipients.put(userId, recipient.getValue());
                }
            }
        }
    }

    // Set message recipient list when message is received at real time in conversation screen.
    public void setMsgRecipients(Map<String, String> chatMembers) {
        // Creating msg recipients list only when the message is not read.
        String selfId = Channelize.getInstance().getCurrentUserId();
        if (chatMembers != null && !chatMembers.isEmpty()) {
            for (Map.Entry<String, String> memberEntry : chatMembers.entrySet()) {
                String memberUserId = memberEntry.getKey();
                if (!memberUserId.equals(selfId)) {
                    msgRecipients.put(memberUserId, 1);
                }
            }
        }
    }

    // Set message recipient list when message is received at real time in recent conversation screen.
    public void setMsgRecipients(List<String> activeMembersList) {
        // Creating msg recipients list only when the message is not read.
        String selfId = Channelize.getInstance().getCurrentUserId();
        if (activeMembersList != null && !activeMembersList.isEmpty()) {
            for (String memberUserId : activeMembersList) {
                if (!memberUserId.equals(selfId)) {
                    msgRecipients.put(memberUserId, 1);
                }
            }
        }
    }

    /**
     * Method to check for all active group participants
     * and return the message status according to each member's status.
     *
     * @return Returns the message status value.
     */
    public int getMessageStatusValue() {
        if (getMsgRecipients() != null && !getMsgRecipients().isEmpty()) {
            return Collections.min(getMsgRecipients().values());
        } else {
            return 1;
        }
    }

    public HashMap<String, Integer> getMsgRecipients() {
        return msgRecipients;
    }

    public void setMsgRecipients(HashMap<String, Integer> msgRecipients) {
        this.msgRecipients = msgRecipients;
    }

    public long getCreatedTimeStamp() {
        return createdTimeStamp;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public boolean isShowProgressBar() {
        return isShowProgressBar;
    }

    public void setShowProgressBar(boolean showProgressBar) {
        isShowProgressBar = showProgressBar;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isMessageSendingFailed() {
        return isMessageSendingFailed;
    }

    public void setMessageSendingFailed(boolean messageSendingFailed) {
        isMessageSendingFailed = messageSendingFailed;
    }

    public boolean isSongPlaying() {
        return isSongPlaying;
    }

    public void setSongPlaying(boolean songPlaying) {
        isSongPlaying = songPlaying;
    }

    public boolean isMusicLoaded() {
        return isMusicLoaded;
    }

    public void setMusicLoaded(boolean musicLoaded) {
        isMusicLoaded = musicLoaded;
    }

    public boolean isUnreadMessage() {
        return isUnreadMessage;
    }

    public void setUnreadMessage(boolean unreadMessage) {
        isUnreadMessage = unreadMessage;
    }

    public boolean isMetaDataMessage() {
        return isMetaDataMessage;
    }

    public boolean isTypingMessage() {
        return isTypingMessage;
    }

    public void setTypingMessage(boolean typingMessage) {
        isTypingMessage = typingMessage;
    }

    public String getTypingText() {
        return typingText;
    }

    public void setTypingText(String typingText) {
        this.typingText = typingText;
    }

    public String getMetaMessage() {
        return metaMessage;
    }

    public String getMetaMessageType() {
        return metaMessageType;
    }

    public MetaMessage getMetaMessageModel() {
        return metaMessageModel;
    }

    public long getDuration() {
        return duration;
    }

    public String getFileSize() {
        return fileSize;
    }

    public boolean isGifAnimationShowing() {
        return gifAnimationShowing;
    }

    public void setGifAnimationShowing(boolean gifAnimationShowing) {
        this.gifAnimationShowing = gifAnimationShowing;
    }

    public boolean isInstantMsg() {
        return isInstantMsg;
    }

    public void setInstantMsg(boolean instantMsg) {
        isInstantMsg = instantMsg;
    }

    private void buildLocationUrl(Location location) {
        if (location != null) {
            this.locationImageUrl = location.getLocationImageUrl();
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            this.locationTitle = location.getLocationTitle();
            this.locationDescription = location.getLocationDescription();
            mapUrl = "http://maps.google.com/maps/api/staticmap?maptype=roadmap"
                    + "&center=" + location.getLatitude() + "," + location.getLongitude()
                    + "&markers=" + location.getLatitude() + "," + location.getLongitude()
                    + "&zoom=16&size=720x720&sensor=false" + "&key=" + Channelize.getInstance().getGooglePlacesKey();
        }
    }

    public Location getLocation() {
        return location;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationTitle() {
        return locationTitle;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public String getLocationImageUrl() {
        return locationImageUrl;
    }

    public String getParentMsgId() {
        return parentMsgId;
    }

    public void setParentMsgId(String parentMsgId) {
        this.parentMsgId = parentMsgId;
    }

    public boolean isQuotedMessage() {
        return isQuotedMessage;
    }

    public String getQuotedMessageOwner() {
        return quotedMessageOwner;
    }

    public String getQuotedMessageBody() {
        return quotedMessageBody;
    }

    public String getQuotedMessageAttachmentType() {
        return qmAttachmentType;
    }

    public void setQuotedMessage(boolean quotedMessage) {
        isQuotedMessage = quotedMessage;
    }

    public void setQuotedMessageOwner(String quotedMessageOwner) {
        this.quotedMessageOwner = quotedMessageOwner;
    }

    public void setQuotedMessageBody(String quotedMessageBody) {
        this.quotedMessageBody = quotedMessageBody;
    }

    public void setQuotedMessageAttachmentType(String qmAttachmentType) {
        this.qmAttachmentType = qmAttachmentType;
    }

    public String getQuotedMessageImage() {
        return quotedMessageImage;
    }

    public void setQuotedMessageImage(String quotedMessageImage) {
        this.quotedMessageImage = quotedMessageImage;
    }

    public void quotedMessageClicked(boolean isQuotedMessageClicked) {
        this.isQuotedMessageClicked = isQuotedMessageClicked;
    }

    public boolean isQuotedMessageClicked() {
        return isQuotedMessageClicked;
    }

    private String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }

    private void getTaggedMemberInfo(Map<String, Member> chatMembersMap) {
        List<String> member = new ArrayList<>();
        body = String.valueOf(Html.fromHtml(body.replaceAll("\n", "<br>")));
        int lastEnd = 0;
        int i = 0;
        for (Tag tag : tagsList) {
            User user = chatMembersMap.get(tag.getUserId()).getUser();
            String memberName = user.getDisplayName();
            int wordCount = tag.getWordCount();
            String[] memberSplitArray = memberName.split(" ");
            if (memberSplitArray.length > wordCount) {
                memberName = replaceLast(memberName, memberSplitArray[memberSplitArray.length - 1], "");
            }
            member.add(memberName);
            int start = ordinalIndexOf(body, "%s", i + 1) + lastEnd;
            int end = memberName.length() + start;
            lastEnd = lastEnd + memberName.length() - 2;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("start", start);
                jsonObject.put("end", end);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            taggedMemberClickableMap.put(user.getId(), jsonObject);
            i++;
        }
        body = String.format(body, (Object[]) member.toArray());
    }

    private int ordinalIndexOf(String mainString, String subString, int n) {
        int pos = mainString.indexOf(subString);
        while (--n > 0 && pos != -1)
            pos = mainString.indexOf(subString, pos + 1);
        return pos;
    }

    public HashMap<String, JSONObject> getTaggedMemberClickableMap() {
        return taggedMemberClickableMap;
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Message messageModel = (Message) object;
            if (this.id != null && !this.id.isEmpty() && messageModel.getId() != null
                    && !messageModel.getId().isEmpty()) {
                result = this.id.equals(messageModel.getId());
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.id.hashCode();
        return hash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedAtV2() {
        return createdAt;
    }

    public void setCreatedAtV2(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public User getOwner() {
        return messageOwnerModel;
    }

    public void setOwner(User messageOwnerModel) {
        this.messageOwnerModel = messageOwnerModel;
    }

    public List<MentionedUser> getMentionedUsers() {
        return mentionedUsers;
    }

    public void setMentionedUsers(List<MentionedUser> mentionedUsers) {
        this.mentionedUsers = mentionedUsers;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setGifStickerUrl(String gifStickerUrl) {
        this.gifStickerUrl = gifStickerUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public void setMetaMessageType(String metaMessageType) {
        this.metaMessageType = metaMessageType;
    }

    public void setMetaMessageModel(MetaMessage metaMessageModel) {
        this.metaMessageModel = metaMessageModel;
    }

    public void setMessageOwnerModel(User messageOwnerModel) {
        this.messageOwnerModel = messageOwnerModel;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Message getQuotedMessage() {
        return quotedMessage;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public List<Tag> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<Tag> tagsList) {
        this.tagsList = tagsList;
    }

    public List<MessageRecipient> getRecipientsList() {
        return recipientsList;
    }

    public void setRecipientsList(List<MessageRecipient> recipientsList) {
        this.recipientsList = recipientsList;
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public void setMetaMessage(String metaMessage) {
        this.metaMessage = metaMessage;
    }

    public void setAttachmentImageUrl(String attachmentImageUrl) {
        this.attachmentImageUrl = attachmentImageUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public void setLocationImageUrl(String locationImageUrl) {
        this.locationImageUrl = locationImageUrl;
    }

    public String getQmAttachmentType() {
        return qmAttachmentType;
    }

    public void setQmAttachmentType(String qmAttachmentType) {
        this.qmAttachmentType = qmAttachmentType;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setMetaDataMessage(boolean metaDataMessage) {
        isMetaDataMessage = metaDataMessage;
    }

    public void setQuotedMessageClicked(boolean quotedMessageClicked) {
        isQuotedMessageClicked = quotedMessageClicked;
    }

    public boolean isBodyProcessed() {
        return isBodyProcessed;
    }

    public void setBodyProcessed(boolean bodyProcessed) {
        isBodyProcessed = bodyProcessed;
    }

    public void setCreatedTimeStamp(long createdTimeStamp) {
        this.createdTimeStamp = createdTimeStamp;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setRecipientsMap(HashMap<String, Integer> recipientsMap) {
        this.recipientsMap = recipientsMap;
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    public void setTaggedMemberClickableMap(HashMap<String, JSONObject> taggedMemberClickableMap) {
        this.taggedMemberClickableMap = taggedMemberClickableMap;
    }

    public int getMessageSendStatus() {
        return messageSendStatus;
    }

    public void setMessageSendStatus(int messageSendStatus) {
        this.messageSendStatus = messageSendStatus;
    }

    public String getOriginalMessageId() {
        return originalMessageId;
    }

    public void setOriginalMessageId(String originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    public Message getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(Message parentMessage) {
        this.parentMessage = parentMessage;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Object getmRecipents() {
        return mRecipents;
    }

    public void setmRecipents(Object mRecipents) {
        this.mRecipents = mRecipents;
    }

    public boolean isTempMessageStatus() {
        return tempMessageStatus;
    }

    public void setTempMessageStatus(boolean tempMessageStatus) {
        this.tempMessageStatus = tempMessageStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(conversationId);
        parcel.writeString(type);
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
        parcel.writeString(body);
        parcel.writeString(ownerId);
        parcel.writeTypedList(attachments);
        parcel.writeByte((byte) (isDeleted == null ? 0 : isDeleted ? 1 : 2));
        parcel.writeParcelable(messageOwnerModel, i);
        parcel.writeString(parentId);
        parcel.writeParcelable(parentMessage, i);
        parcel.writeString(originalMessageId);
        parcel.writeTypedList(mentionedUsers);
        parcel.writeString(filePath);
        parcel.writeInt(messageSendStatus);
        parcel.writeByte((byte) (tempMessageStatus ? 1 : 0));
        parcel.writeString(parentMsgId);
        parcel.writeString(attachmentType);
        parcel.writeString(gifStickerUrl);
        parcel.writeString(originalUrl);
        parcel.writeString(metaMessageType);
        parcel.writeString(status);
        parcel.writeInt(contentType);
        parcel.writeByte((byte) (isQuotedMessage ? 1 : 0));
        parcel.writeByte((byte) (isRetry ? 1 : 0));
        parcel.writeParcelable(metaMessageModel, i);
        parcel.writeParcelable(location, i);
        parcel.writeParcelable(quotedMessage, i);
        parcel.writeParcelable(attachment, i);
        parcel.writeTypedList(tagsList);
        parcel.writeTypedList(recipientsList);
        parcel.writeString(locationTitle);
        parcel.writeString(locationDescription);
        parcel.writeString(metaMessage);
        parcel.writeString(tempCreatedAt);
        parcel.writeString(attachmentImageUrl);
        parcel.writeString(thumbnailUrl);
        parcel.writeString(savedFile);
        parcel.writeString(mapUrl);
        parcel.writeString(locationImageUrl);
        parcel.writeString(quotedMessageOwner);
        parcel.writeString(quotedMessageBody);
        parcel.writeString(qmAttachmentType);
        parcel.writeString(quotedMessageImage);
        parcel.writeString(typingText);
        parcel.writeString(fileSize);
        parcel.writeByte((byte) (isShowProgressBar ? 1 : 0));
        parcel.writeByte((byte) (isMessageSendingFailed ? 1 : 0));
        parcel.writeByte((byte) (isSongPlaying ? 1 : 0));
        parcel.writeByte((byte) (isMusicLoaded ? 1 : 0));
        parcel.writeByte((byte) (isUnreadMessage ? 1 : 0));
        parcel.writeByte((byte) (isMetaDataMessage ? 1 : 0));
        parcel.writeByte((byte) (isDownloading ? 1 : 0));
        parcel.writeByte((byte) (gifAnimationShowing ? 1 : 0));
        parcel.writeByte((byte) (isQuotedMessageClicked ? 1 : 0));
        parcel.writeByte((byte) (isTypingMessage ? 1 : 0));
        parcel.writeByte((byte) (isBodyProcessed ? 1 : 0));
        parcel.writeByte((byte) (isInstantMsg ? 1 : 0));
        parcel.writeInt(messageStatus);
        parcel.writeInt(downloadProgress);
        parcel.writeLong(createdTimeStamp);
        parcel.writeLong(duration);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeTypedList(messageList);
    }
}
