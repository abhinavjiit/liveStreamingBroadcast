/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.model;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.channelize.apisdk.ApiConstants;
import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.R;
import com.channelize.apisdk.Utils;
import com.channelize.apisdk.network.response.GenericResponse;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Conversation implements Parcelable, GenericResponse {

    // Which are Checked
    // Member variables.
    @SerializedName("id")
    private String conversationId;//Okay

    @SerializedName("title")
    private String title;//Okay

    @SerializedName("memberCount")
    private int memberCount;//Okay

    @SerializedName("isGroup")
    private boolean isGroupChat;//Okay

    @SerializedName("ownerId")
    private String chatOwnerId;//Okay

    @SerializedName("profileImageUrl")
    private String profileImage;//Okay

    @SerializedName("createdAt")
    private String createdAt;//Okay

    @SerializedName("type")
    private String type;//Okay

    @SerializedName("lastReadAt")
    private HashMap<String,String> lastReadAt;//Okay

    @SerializedName("unreadMessageCount")
    private Integer unreadMessageCount;//Okay

    @SerializedName("mute")
    private Boolean mute;//Okay

    @SerializedName("isActive")
    private Boolean isActive;//Okay

    @SerializedName("isAdmin")
    private Boolean isAdmin;//Okay

    @SerializedName("isDeleted")
    private Boolean isDeleted;//Okay

    @SerializedName("updatedAt")
    private String updatedAt;//Okay

    @SerializedName("lastMessage")
    private Message message;//Okay

    @SerializedName("user")
    private User user;//Okay

    @SerializedName("owner")
    private User owner;//Okay

    @SerializedName("members")
    private List<Member> membersList;//Okay


    private String selfUid;
    private int profileColor;

    //Which are not checked
    private String attachmentType = "";
    private String lastMessageId;
    private String latestMessage, lastMessageOwner;
    private String typingText;
    private String groupUpdatedTime;
    private String lastUpdatedTime;

    private Map<String, Member> membersListMap = new HashMap<>();
    private boolean isDeletedUser = false, isLastMessageMeta = false;
    private boolean isOnline, isChatActive = true;
    private boolean blockedByReceiver, blockedBySender, isChatMuted;
    private int lastMessageStatus, newMessageCount;
    private long timeStamp;
    private User receiverUserModel;
    private List<String> activeMembersList = new ArrayList<>();
    private List<Conversation> conversationList;


    //Newly Created Variables for V2
    private String finalTitle;

    /**
     * Public constructor to initialize the Conversation with the List of models.
     *
     * @param conversationList List of Conversation, which contains the list of chats.
     */
    public Conversation(List<Conversation> conversationList) {
        this.conversationId = "";
        this.conversationList = conversationList;
    }

    /**
     * Default constructor.
     */
    public Conversation() {
        setProfileColor();
    }

    /**
     * Public constructor to initialize the Conversation with the conversationId.
     *
     * @param conversationId ConversationId for which Conversation instance needs to be created.
     */
    public Conversation(String conversationId) {
        this.conversationId = conversationId;
    }

    protected Conversation(Parcel in) {
        conversationId = in.readString();
        title = in.readString();
        memberCount = in.readInt();
        isGroupChat = in.readByte() != 0;
        chatOwnerId = in.readString();
        profileImage = in.readString();
        createdAt = in.readString();
        type = in.readString();
        if (in.readByte() == 0) {
            unreadMessageCount = null;
        } else {
            unreadMessageCount = in.readInt();
        }
        byte tmpMute = in.readByte();
        mute = tmpMute == 0 ? null : tmpMute == 1;
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        byte tmpIsAdmin = in.readByte();
        isAdmin = tmpIsAdmin == 0 ? null : tmpIsAdmin == 1;
        byte tmpIsDeleted = in.readByte();
        isDeleted = tmpIsDeleted == 0 ? null : tmpIsDeleted == 1;
        updatedAt = in.readString();
        message = in.readParcelable(Message.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
        owner = in.readParcelable(User.class.getClassLoader());
        membersList = in.createTypedArrayList(Member.CREATOR);
        selfUid = in.readString();
        profileColor = in.readInt();
        attachmentType = in.readString();
        lastMessageId = in.readString();
        latestMessage = in.readString();
        lastMessageOwner = in.readString();
        typingText = in.readString();
        groupUpdatedTime = in.readString();
        lastUpdatedTime = in.readString();
        isDeletedUser = in.readByte() != 0;
        isLastMessageMeta = in.readByte() != 0;
        isOnline = in.readByte() != 0;
        isChatActive = in.readByte() != 0;
        blockedByReceiver = in.readByte() != 0;
        blockedBySender = in.readByte() != 0;
        isChatMuted = in.readByte() != 0;
        lastMessageStatus = in.readInt();
        newMessageCount = in.readInt();
        timeStamp = in.readLong();
        receiverUserModel = in.readParcelable(User.class.getClassLoader());
        activeMembersList = in.createStringArrayList();
        conversationList = in.createTypedArrayList(Conversation.CREATOR);
        finalTitle = in.readString();
        lastReadAt = (HashMap<String, String>) in.readSerializable();
    }

    public static final Creator<Conversation> CREATOR = new Creator<Conversation>() {
        @Override
        public Conversation createFromParcel(Parcel in) {
            return new Conversation(in);
        }

        @Override
        public Conversation[] newArray(int size) {
            return new Conversation[size];
        }
    };

    /**
     * Method to perform post processing over member variables once the object has been created.
     */
    public void postProcess() {
        selfUid = Channelize.getInstance().getCurrentUserId();
        Message message = null;
        if (membersList != null) {
            for (Member member : membersList) {
                String userId = member.getUserId();
                membersListMap.put(userId, member);
                if (member.isActive()) {
                    activeMembersList.add(userId);
                }
                // When its one to one chat then getting the receiver user's model and checking the block status also.
                if (!isGroupChat) {
                    if (userId.equals(selfUid)) {
                        checkOneToOneChatActive(member);
                        // Checking if there is only 1 member response is coming in member list.
                        if (membersList.size() == 1) {
                            setReceiverUserModel(null);
                        }
                    } else {
                        setReceiverUserModel(member.getUser());
                        // Checking in one to one chat, if the another user has blocked the current user,
                        // then "isActive" of another user will be false, and in this case
                        // current user will not be able to see the profile pic of another user.
                        blockedByReceiver = !member.isActive();
                    }
                }
                // Common information for one to one and group chat.
                if (userId.equals(selfUid)) {
                    member.setChatTime();
                    groupUpdatedTime = member.getGroupUpdatedTime();
                    lastUpdatedTime = member.getLastUpdatedTime();
                    timeStamp = member.getTimeStamp();
                    message = member.getMessage();
                    if (message != null) {
                        lastMessageId = message.getId();
                    }
                    newMessageCount = member.getNewMessageCount();
                    if (isGroupChat) {
                        isChatActive = member.isActive();
                    }
                    isChatMuted = member.isMuted();
                }
            }
        }
        // Changing one to one chat variable's value in group chat, as there is no deleted user's thing in group chat.
        if (isGroupChat) {
            isDeletedUser = false;
            blockedByReceiver = false;
            title = title != null ? title.trim() : "";
        }
        setLastMessage(message);
        setProfileColor();
    }

    public void postProcessV3() {
        selfUid = Channelize.getInstance().getCurrentUserId();
        if (isGroupChat()) {
            finalTitle = getTitle();
        } else {
            checkForTheOtherUserName();
        }


        for (Member member : membersList) {
            String userId = member.getUserId();
            membersListMap.put(userId, member);
            activeMembersList.add(userId);
        }

        if (!isGroupChat) {
                /*
                Now in List only active members are coming so if the size if list is 2
                it means both members are active and none of them has blocked each other
                 */
            if (activeMembersList != null && activeMembersList.size() == 2) {
                blockedBySender = false;
                blockedByReceiver = false;
                isChatActive = true;

                for (int i = 0; i < activeMembersList.size(); i++) {
                    if (!activeMembersList.get(i).equals(selfUid)) {
                        setReceiverUserModel(membersListMap.get(activeMembersList.get(i)).getUser());
                        break;
                    }
                }
            }
                /*
                Now in List only active members are coming so if the size if list is 1
                it means any member has blocked one member so we need to check in this case who has blocked who.
                 */
            else if (activeMembersList != null && activeMembersList.size() == 1) {
                    /*
                    Check whether the current logged in user is coming or not
                    if current logged in user is coming then current user is active
                    else other user is active
                     */
                if (activeMembersList.contains(selfUid)) {
                    blockedBySender = false;
                    isChatActive = true;
                    blockedByReceiver = true;
                    setReceiverUserModel(null);
                } else {
                    blockedBySender = true;
                    isChatActive = false;
                    blockedByReceiver = false;
                    setReceiverUserModel(membersListMap.get(activeMembersList.get(0)).getUser());
                }
            }
                 /*
                Now in List only active members are coming so if the size if list is 0
                it means both members has blocked each other.
                 */
            else if (activeMembersList != null && activeMembersList.size() == 0) {
                blockedBySender = true;
                blockedByReceiver = true;
                isChatActive = false;
                setReceiverUserModel(null);
            }


        } else if (isGroupChat) {
            isDeletedUser = false;
            blockedByReceiver = false;
            title = title != null ? title.trim() : "";
        }

        setLastMessage(message);
        setProfileColor();

    }

    private void checkForTheOtherUserName() {
        if (membersList != null) {
            for (Member member : membersList) {
                if (member!=null && member.getUser()!=null && member.getUser().getId()!=null && member.getUser().getId() != selfUid) {
                    finalTitle = member.getUser().getDisplayName();
                    break;
                }
            }
        }
    }


    /**
     * Method to check if the chat is active when its between one to one user.
     * Chat will be active when the both users are active in the chat (None of them have blocked each other).
     */
    private void checkOneToOneChatActive(Member member) {
        // Checking in one to one chat, if the current user has blocked the another user,
        // then "isActive" of current user will be false, and in this case
        // current user will not be able to send messages.
        if (member.getUserId().equals(selfUid)) {
            blockedBySender = !member.isActive();
        }
        // Checking in one to one chat, if there is any user who is not active in the chat,
        // then "isActive" of that user will be false.
        // And if there is any such case then chat will be not active,
        // and in this case no user will be able to see online/offline status of another user.
        if (!member.isActive()) {
            isChatActive = false;
        }
    }

    /**
     * Method to get the typing text if any user in conversation is typing.
     */
    public void fetchTypingText() {
        typingText = "";
        Context context = Channelize.getInstance().getContext();
        for (Map.Entry<String, Member> entry : membersListMap.entrySet()) {
            Member member = entry.getValue();
            User userModel = member.getUser();
            if (isChatActive() && member.isTyping() && userModel != null
                    && !member.getUserId().equals(selfUid)
                    && userModel.getDisplayName() != null) {
                if (isGroupChat) {
                    typingText = String.format(context.getResources().getString(R.string.pm_user_typing),
                            userModel.getDisplayName().trim().split("\\s+")[0]);
                } else {
                    typingText = context.getResources().getString(R.string.pm_typing);
                }
                break;
            }
        }
    }

    /**
     * Method to set the Last message of the chat room.
     *
     * @param message Message instance which is the last message of the chat room.
     */
    public void setLastMessage(Message message) {
        if (message != null) {
            message.processBody(membersListMap);
            isLastMessageMeta = false;
            User msgOwnerModel = message.getMessageOwnerModel();
            // Checking if the owner exist or not of the message.
            boolean isLastMessageOwnerExist = (msgOwnerModel != null && msgOwnerModel.getId() != null
                    && !msgOwnerModel.getId().isEmpty() && !msgOwnerModel.getId().equals("null"));

            // Getting status of the message when the message owner exist.
            lastMessageStatus = ((isLastMessageOwnerExist
                    && msgOwnerModel.getId().equals(selfUid) && message.getRecipientsMap() != null
                    && message.getRecipientsMap().size() > 0
                    && message.getRecipientsMap().containsKey(selfUid))
                    ? message.getRecipientsMap().get(selfUid)
                    : isLastMessageOwnerExist && msgOwnerModel.getId().equals(selfUid)
                    ? message.getMessageStatus() : 0);

            attachmentType = message.getAttachmentType() != null
                    ? message.getAttachmentType() : "";
            String messageBody = message.getBody();

            Context context = Channelize.getInstance().getContext();

            if (message.isMessageDeleted() != null && message.isMessageDeleted()) {
                messageBody = context.getResources().getString(R.string.message_was_deleted);
            }


            // Setting the owner name with the message if the chat is of group type and message owner exist.
            lastMessageOwner = isLastMessageOwnerExist
                    && !msgOwnerModel.getId().equals(selfUid)
                    && msgOwnerModel.getDisplayName() != null
                    ? msgOwnerModel.getDisplayName() : null;
            lastMessageOwner = lastMessageOwner != null ? lastMessageOwner.trim().split("\\s+")[0] + ": " : "";

            // Setting the last message according attachment type.
            switch (attachmentType) {
                case ApiConstants.ATTACHMENT_TEXT:
                    // Text Message
                    latestMessage = messageBody;
                    if (message.getMetaMessage() != null &&
                            !message.getMetaMessage().isEmpty()) {
                        isLastMessageMeta = true;
                        latestMessage = message.getMetaMessage();
                    }
                    break;

                case ApiConstants.ATTACHMENT_IMAGE:
                    // Photo Message
                    latestMessage = context.getResources().getString(R.string.pm_image);
                    break;

                case ApiConstants.ATTACHMENT_VIDEO:
                    // Video Message
                    latestMessage = context.getResources().getString(R.string.pm_video);
                    break;

                case ApiConstants.ATTACHMENT_AUDIO:
                    // Audio Message
                    latestMessage = context.getResources().getString(R.string.pm_audio);
                    break;

                case ApiConstants.ATTACHMENT_FILE:
                    // Document Message
                    latestMessage = context.getResources().getString(R.string.pm_document);
                    break;

                case ApiConstants.ATTACHMENT_STICKER:
                    // sticker Message
                    latestMessage = context.getResources().getString(R.string.pm_sticker);
                    break;

                case ApiConstants.ATTACHMENT_GIF:
                    // gif Message
                    latestMessage = context.getResources().getString(R.string.pm_gif);
                    break;

                case ApiConstants.ATTACHMENT_LOCATION:
                    // gif Message
                    latestMessage = context.getResources().getString(R.string.pm_location);
                    break;

                default:
                    latestMessage = lastMessageOwner + attachmentType;
                    if (message.getMetaMessage() != null &&
                            !message.getMetaMessage().isEmpty()) {
                        isLastMessageMeta = true;
                        latestMessage = message.getMetaMessage();
                    }
                    break;
            }

        }
    }

    public String getConversationId() {
        return conversationId;
    }

    public String getChatOwnerId() {
        return chatOwnerId;
    }

    public boolean isChatMuted() {
        return isChatMuted;
    }

    public void setChatMuted(boolean chatMuted) {
        isChatMuted = chatMuted;
    }

    public void setChatActive(boolean chatActive) {
        isChatActive = chatActive;
    }

    public boolean isChatActive() {
        return isChatActive;
    }

    public boolean isBlockedByReceiver() {
        return blockedByReceiver;
    }

    public void setBlockedByReceiver(boolean blockedByReceiver) {
        this.blockedByReceiver = blockedByReceiver;
    }

    public boolean isBlockedBySender() {
        return blockedBySender;
    }

    public void setBlockedBySender(boolean blockedBySender) {
        this.blockedBySender = blockedBySender;
    }

    public int getNewMessageCount() {
        return newMessageCount;
    }

    public void setNewMessageCount(int newMessageCount) {
        this.newMessageCount = newMessageCount;
    }

    public void setGroupUpdatedTime(long groupUpdatedTime) {
        this.groupUpdatedTime = Utils.getRelativeTimeString(groupUpdatedTime);
    }

    public String getGroupUpdatedTime() {
        return groupUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLastMessage() {
        return latestMessage;
    }

    public String getLastMessageOwner() {
        return lastMessageOwner;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public String getLastMessageId() {
        return lastMessageId;
    }

    public void setLastMessageId(String lastMessageId) {
        this.lastMessageId = lastMessageId;
    }

    public void setLastMessageStatus(int lastMessageStatus) {
        this.lastMessageStatus = lastMessageStatus;
    }

    public int getLastMessageStatus() {
        return lastMessageStatus;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    public String getTypingText() {
        return typingText;
    }

    public boolean isDeletedUser() {
        return isDeletedUser;
    }

    public User getReceiverUserModel() {
        return receiverUserModel;
    }

    /**
     * Method to set the receiver User model when its one to one chat.
     *
     * @param receiverUserModel User instance which will contains the another user info.
     */
    public void setReceiverUserModel(User receiverUserModel) {
        this.receiverUserModel = receiverUserModel;
        // Checking if the user exist or not.
        if (receiverUserModel != null && receiverUserModel.getId() != null
                && !receiverUserModel.getId().isEmpty()) {
            isDeletedUser = false;
            title = receiverUserModel.getDisplayName();
            profileImage = receiverUserModel.getProfileImageUrl();
            isOnline = receiverUserModel.isOnline();
        } else {
            isDeletedUser = true;
            title = Channelize.getInstance().getContext().getResources().getString(R.string.pm_deleted_user);
            this.receiverUserModel = new User("");
            this.receiverUserModel.setDisplayName(title);
            this.receiverUserModel.setOnline(false);
        }
    }

    public List<Member> getMembersList() {
        return membersList;
    }

    public void setMembersListMap(Map<String, Member> membersListMap) {
        this.membersListMap = membersListMap;
    }

    public void setMemberModel(String userId, Member memberModel) {
        membersListMap.put(userId, memberModel);
    }

    public Member getMemberModel(String userId) {
        return membersListMap.get(userId);
    }

    public Map<String, Member> getMembersListMap() {
        return membersListMap;
    }

    public boolean isLastMessageMeta() {
        return isLastMessageMeta;
    }

    public void setLastMessageMeta(boolean lastMessageMeta) {
        isLastMessageMeta = lastMessageMeta;
    }

    public void setProfileColor() {
        Random random = new Random();
        this.profileColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public int getProfileColor() {
        return profileColor != 0 ? profileColor : R.color.colorAccent;
    }

    public List<String> getActiveMembersList() {
        return activeMembersList;
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    /**
     * Method to add Message as last message in chat.
     *
     * @param newMessageModel Message which needs to be add in chat.
     */
    public void addMessageInChat(Message newMessageModel) {
        if (lastMessageId == null || lastMessageId.isEmpty()
                || !lastMessageId.equals(newMessageModel.getId())) {
            if (isGroupChat() && newMessageModel.getOwnerId().equals(selfUid)) {
                newMessageModel.setMsgRecipients(getActiveMembersList());
            }
            Member memberModel = getMemberModel(selfUid);
            memberModel.setMessage(newMessageModel);
            memberModel.setTimeStamp(newMessageModel.getCreatedTimeStamp());
            setLastMessage(newMessageModel);
            setLastMessageId(newMessageModel.getId());
            setGroupUpdatedTime(newMessageModel.getCreatedTimeStamp());
            setMemberModel(selfUid, memberModel);
            setLastUpdatedTime(memberModel.getLastUpdatedTime());
            setTimeStamp(memberModel.getTimeStamp());
            if (!newMessageModel.getOwnerId().equals(selfUid)
                    && !newMessageModel.isMetaDataMessage()) {
                setNewMessageCount(getNewMessageCount() + 1);
            }
        }
    }

    /**
     * Method to check one to one conversations and update them when block/unblock called.
     *
     * @param isBlock    True, if need to check for a block subscriber.
     * @param isSelfCase True, if checking for the self block/unblock case.
     */
    public void checkForUserBlockUnBlock(boolean isBlock, boolean isSelfCase) {
        try {
            // If the subscriber is called for the ApiConstants.SELF_BLOCKED or ApiConstants.SELF_UNBLOCKED.
            if (isSelfCase) {
                setBlockedByReceiver(isBlock);
            } else {
                // If the subscriber is called for the ApiConstants.USERS_BLOCK or ApiConstants.USERS_UNBLOCK.
                setBlockedBySender(isBlock);
            }
            // If there is any block thing then chat will be inactive.
            setChatActive(!isBlockedBySender() && !isBlockedByReceiver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to get the update list of recipients according to active chat members.
     *
     * @param messageModel Message instance which contains the all recipients list.
     * @param userId       UserId of the user who marked the message as read/delivered.
     * @param msgStatus    Status value of the message according to read/delivered.
     * @return Returns the updated list.
     */
    private HashMap<String, Integer> getRecipientList(Message messageModel, String userId,
                                                      int msgStatus) {
        // Putting the message status value for each active member.
        HashMap<String, Integer> msgModelRecipients = messageModel.getMsgRecipients();

        if (isGroupChat() && msgModelRecipients != null) {
            msgModelRecipients.put(userId, msgStatus);
        }
        return msgModelRecipients;
    }

    /**
     * Method to check for the last message in chat.
     */
    public void updateNewMsgCount() {
        // Getting the member list only if its exist for the logged-in user id and message also exist.
        Map<String, Member> memberList = getMembersListMap() != null
                && getMembersListMap().containsKey(selfUid)
                && getMembersListMap().get(selfUid).getMessage() != null
                ? getMembersListMap() : null;
        setNewMessageCount(0);
        setLastMessageStatus(memberList != null
                && !memberList.get(selfUid).getMessage().getOwnerId().equals(selfUid)
                ? 0 : getLastMessageStatus());
    }

    /**
     * Method to update for the status of a message sent by the logged-in user.
     *
     * @param messageId MessageId for which status needs to be update
     * @param userId    UserId of the user who has seen that message or msg to that user has been delivered.
     * @param msgStatus Message Status which needs to be update.
     */
    public void checkForLastMessageUpdate(String messageId, String userId, int msgStatus) {

        // Getting the member list only if its exist for the logged-in user id and message also exist.
        Map<String, Member> memberList = getMembersListMap() != null
                && getMembersListMap().containsKey(selfUid)
                && getMembersListMap().get(selfUid).getMessage() != null
                ? getMembersListMap() : null;

        if (isGroupChat() && memberList != null) {
            // When the logged-in user's messages gets marked as read/delivered.
            Message messageModel = memberList.get(selfUid).getMessage();
            if (messageModel.getOwnerId().equals(selfUid)) {
                messageModel.setMsgRecipients(getRecipientList(messageModel,
                        userId, msgStatus));
                int status = messageModel.getMessageStatusValue();
                messageModel.setMessageStatus(status);
                setLastMessageStatus(status);
            }
        } else if (getLastMessage() != null && getLastMessageId().equals(messageId)
                && memberList != null && memberList.get(selfUid).getMessage().getOwnerId().equals(selfUid)
                && !userId.equals(selfUid)) {
            setLastMessageStatus(msgStatus);
        }
    }

    /**
     * Method to check for the typing status of a chat member.
     *
     * @param userId   UserId of the chat member who is typing.
     * @param isTyping True, if user is typing otherwise false.
     */
    public void checkForTyping(String userId, boolean isTyping) {
        Map<String, Member> membersList = getMembersListMap();
        Member memberModel = membersList.get(userId);
        memberModel.setTyping(isTyping);
        membersList.put(userId, memberModel);
        setMembersListMap(membersList);
        fetchTypingText();
    }

    /**
     * Method to check for the admin status of a chat member.
     *
     * @param userId  UserId of the chat member who has made admin.
     * @param isAdmin True, if user is typing otherwise false.
     */
    public void checkForAdminUpdate(String userId, boolean isAdmin) {
        Map<String, Member> membersList = getMembersListMap();
        Member memberModel = membersList.get(userId);
        memberModel.setAdmin(isAdmin);
        membersList.put(userId, memberModel);
        setMembersListMap(membersList);
    }

    /**
     * Method to update chat mute/unMute status.
     *
     * @param isChatMuted True, if chat muted otherwise false.
     */
    public void checkForChatMuteUnMute(boolean isChatMuted) {
        Map<String, Member> membersList = getMembersListMap();
        Member memberModel = membersList.get(selfUid);
        memberModel.setMuted(isChatMuted);
        membersList.put(selfUid, memberModel);
        setChatMuted(isChatMuted);
        setMembersListMap(membersList);
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            Conversation conversation = (Conversation) object;
            if (this.conversationId.equals(conversation.getConversationId())) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 7 * hash + this.conversationId.hashCode();
        return hash;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public void setGroupChat(boolean groupChat) {
        isGroupChat = groupChat;
    }

    public void setChatOwnerId(String chatOwnerId) {
        this.chatOwnerId = chatOwnerId;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(Integer unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }

    public Boolean getMute() {
        return mute;
    }

    public void setMute(Boolean mute) {
        this.mute = mute;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setMembersList(List<Member> membersList) {
        this.membersList = membersList;
    }

    public String getSelfUid() {
        return selfUid;
    }

    public void setSelfUid(String selfUid) {
        this.selfUid = selfUid;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public void setLastMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setLastMessageOwner(String lastMessageOwner) {
        this.lastMessageOwner = lastMessageOwner;
    }

    public void setTypingText(String typingText) {
        this.typingText = typingText;
    }

    public void setGroupUpdatedTime(String groupUpdatedTime) {
        this.groupUpdatedTime = groupUpdatedTime;
    }

    public void setDeletedUser(boolean deletedUser) {
        isDeletedUser = deletedUser;
    }

    public void setProfileColor(int profileColor) {
        this.profileColor = profileColor;
    }

    public void setActiveMembersList(List<String> activeMembersList) {
        this.activeMembersList = activeMembersList;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public String getFinalTitle() {
        return finalTitle;
    }

    public void setFinalTitle(String finalTitle) {
        this.finalTitle = finalTitle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public HashMap<String, String> getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(HashMap<String, String> lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(conversationId);
        parcel.writeString(title);
        parcel.writeInt(memberCount);
        parcel.writeByte((byte) (isGroupChat ? 1 : 0));
        parcel.writeString(chatOwnerId);
        parcel.writeString(profileImage);
        parcel.writeString(createdAt);
        parcel.writeString(type);
        if (unreadMessageCount == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(unreadMessageCount);
        }
        parcel.writeByte((byte) (mute == null ? 0 : mute ? 1 : 2));
        parcel.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        parcel.writeByte((byte) (isAdmin == null ? 0 : isAdmin ? 1 : 2));
        parcel.writeByte((byte) (isDeleted == null ? 0 : isDeleted ? 1 : 2));
        parcel.writeString(updatedAt);
        parcel.writeParcelable(message, i);
        parcel.writeParcelable(user, i);
        parcel.writeParcelable(owner, i);
        parcel.writeTypedList(membersList);
        parcel.writeString(selfUid);
        parcel.writeInt(profileColor);
        parcel.writeString(attachmentType);
        parcel.writeString(lastMessageId);
        parcel.writeString(latestMessage);
        parcel.writeString(lastMessageOwner);
        parcel.writeString(typingText);
        parcel.writeString(groupUpdatedTime);
        parcel.writeString(lastUpdatedTime);
        parcel.writeByte((byte) (isDeletedUser ? 1 : 0));
        parcel.writeByte((byte) (isLastMessageMeta ? 1 : 0));
        parcel.writeByte((byte) (isOnline ? 1 : 0));
        parcel.writeByte((byte) (isChatActive ? 1 : 0));
        parcel.writeByte((byte) (blockedByReceiver ? 1 : 0));
        parcel.writeByte((byte) (blockedBySender ? 1 : 0));
        parcel.writeByte((byte) (isChatMuted ? 1 : 0));
        parcel.writeInt(lastMessageStatus);
        parcel.writeInt(newMessageCount);
        parcel.writeLong(timeStamp);
        parcel.writeParcelable(receiverUserModel, i);
        parcel.writeStringList(activeMembersList);
        parcel.writeTypedList(conversationList);
        parcel.writeString(finalTitle);
        parcel.writeSerializable(lastReadAt);
    }
}
