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

import com.google.gson.annotations.SerializedName;
import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.R;
import com.channelize.apisdk.Utils;
import com.channelize.apisdk.network.response.GenericResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class User implements Parcelable, GenericResponse {

    // Member variables.
    @SerializedName("id")
    private String id;//Okay

    @SerializedName("displayName")
    private String displayName;//Okay

    @SerializedName("profileImageUrl")
    private String profileImageUrl;//Okay

    @SerializedName("profileUrl")
    private String profileUrl;//Okay

    @SerializedName("isOnline")
    private boolean isOnline;//Okay

    @SerializedName("notification")
    private boolean notification;//Okay

    @SerializedName("lastSeen")
    private String lastSeen;//Okay

    @SerializedName("language")
    private String language;
    @SerializedName("visibility")
    private boolean visibility;
    @SerializedName("hasBlocked")
    private boolean hasBlocked;
    @SerializedName("isBlocked")
    private boolean isBlocked;
    @SerializedName("email")
    private String email;
    @SerializedName("friendList")
    public List<String> friendList;
    @SerializedName("blockedList")
    public List<String> blockedList;
    @SerializedName("blockList")
    public List<String> blockList;

    public String configuration;
    public Integer video_resolution_width;
    public Integer video_resolution_height;

    private boolean isFriend = true, isDeletedUser, contactSelected, isAdmin;
    // Default icon color.
    private int profileColor;
    private List<User> userList;


    public User() {

    }

    /**
     * Public constructor to initialize the User with the List of models.
     *
     * @param userList List of User, which contains the list of contact screen members.
     */
    public User(List<User> userList) {
        this.id = "";
        this.userList = userList;
    }

    /**
     * Public constructor to initialize the User with the userId.
     *
     * @param userId UserId for which User instance needs to be created.
     */
    public User(String userId) {
        this.id = userId;
    }

    protected User(Parcel in) {
        id = in.readString();
        displayName = in.readString();
        profileImageUrl = in.readString();
        profileUrl = in.readString();
        isOnline = in.readByte() != 0;
        notification = in.readByte() != 0;
        lastSeen = in.readString();
        language = in.readString();
        visibility = in.readByte() != 0;
        hasBlocked = in.readByte() != 0;
        isBlocked = in.readByte() != 0;
        email = in.readString();
        friendList = in.createStringArrayList();
        blockedList = in.createStringArrayList();
        blockList = in.createStringArrayList();
        configuration = in.readString();
        if (in.readByte() == 0) {
            video_resolution_width = null;
        } else {
            video_resolution_width = in.readInt();
        }
        if (in.readByte() == 0) {
            video_resolution_height = null;
        } else {
            video_resolution_height = in.readInt();
        }
        isFriend = in.readByte() != 0;
        isDeletedUser = in.readByte() != 0;
        contactSelected = in.readByte() != 0;
        isAdmin = in.readByte() != 0;
        profileColor = in.readInt();
        userList = in.createTypedArrayList(User.CREATOR);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public void postProcess() {
        String selfId = Channelize.getInstance().getCurrentUserId();
        // When user comes online then checking it from blocked and block list.
        if (blockList != null && !blockList.isEmpty() && blockedList != null && !blockedList.isEmpty()) {
            this.hasBlocked = blockedList.contains(selfId);
            this.isBlocked = blockList.contains(selfId);
        }
        // When user comes online then checking it for friendship.
        if (friendList != null && !friendList.isEmpty()) {
            this.isFriend = friendList.toString().contains(selfId);
        }
        this.displayName = capitalize(displayName);
        setLastSeen(lastSeen);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName != null && !displayName.isEmpty() && !displayName.equals("null")
                ? displayName : null;
    }

    public void setDisplayName(String displayName) {
        this.displayName = capitalize(displayName);
    }

    /**
     * Method to capitalize the first letter of each word.
     *
     * @param capString String title which needs to be capitalize.
     * @return Returns the updated string.
     */
    private String capitalize(String capString) {
        if (capString != null && !capString.isEmpty()) {
            capString = capString.trim();
            StringBuffer capBuffer = new StringBuffer();
            Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
            while (capMatcher.find()) {
                capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
            }

            return capMatcher.appendTail(capBuffer).toString();
        } else {
            return capString;
        }
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public boolean isNotification() {
        return notification;
    }

    public void setNotification(boolean notification) {
        this.notification = notification;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public Integer getVideo_resolution_width() {
        return video_resolution_width;
    }

    public void setVideo_resolution_width(Integer video_resolution_width) {
        this.video_resolution_width = video_resolution_width;
    }

    public Integer getVideo_resolution_height() {
        return video_resolution_height;
    }

    public void setVideo_resolution_height(Integer video_resolution_height) {
        this.video_resolution_height = video_resolution_height;
    }

    /**
     * When logged-in user has blocked the other user.
     *
     * @return Returns true if the logged-in user has blocked the other one.
     */
    public boolean isBlockedBySender() {
        return hasBlocked;
    }

    public void setBlockedBySender(boolean blockedBySender) {
        this.hasBlocked = blockedBySender;
    }

    /**
     * When logged-in user is blocked by the other user.
     *
     * @return Returns true if the logged-in user is blocked.
     */
    public boolean isBlockedByReceiver() {
        return isBlocked;
    }

    public void setBlockedByReceiver(boolean blockedByReceiver) {
        this.isBlocked = blockedByReceiver;
    }

    /**
     * Method to check if there is any block thing then can not show online/offline status.
     *
     * @return Returns True if can show online status.
     */
    public boolean canShowOnlineStatus() {
        return !hasBlocked && !isBlocked;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isDeletedUser() {
        return isDeletedUser;
    }

    public void setDeletedUser(boolean deletedUser) {
        isDeletedUser = deletedUser;
    }

    /**
     * Set last seen time from the received time format.
     *
     * @param lastSeen Last which from which time need to be calculated.
     */
    public void setLastSeen(String lastSeen) {
        if (lastSeen != null && !lastSeen.isEmpty() && !lastSeen.equals("null")) {
            try {
                Context context = Channelize.getInstance().getContext();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                        Locale.getDefault());
                Date date = sdf.parse(lastSeen.replaceAll("Z$", "+0000"));
                Long lastSeenTime = date.getTime();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy/HH/mm a",
                        Locale.getDefault());
                String timeStamp = simpleDateFormat.format(new Date(lastSeenTime));
                String today = Utils.getCurrentTimestamp();
                String seenTime = Utils.getTimestamp(lastSeenTime, context);

                if (timeStamp.substring(0, 10).equals(today.substring(0, 10))) {
                    this.lastSeen = context.getResources().getString(R.string.pm_last_seen) + " "
                            + context.getResources().getString(R.string.pm_today).toLowerCase()
                            + " " + context.getResources().getString(R.string.pm_at) + " "
                            + seenTime;
                } else {
                    this.lastSeen = context.getResources().getString(R.string.pm_last_seen) + " "
                            + context.getResources().getString(R.string.pm_on) + " "
                            + timeStamp.substring(0, 5) + " "
                            + context.getResources().getString(R.string.pm_at) + " " + seenTime;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            this.lastSeen = null;
        }
    }

    public void setProfileColor(int profileColor) {
        this.profileColor = profileColor;
    }

    public void setProfileColor() {
        Random random = new Random();
        this.profileColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public int getProfileColor() {
        return profileColor != 0 ? profileColor : R.color.colorAccent;
    }

    public void setContactSelected(boolean contactSelected) {
        this.contactSelected = contactSelected;
    }

    public boolean isContactSelected() {
        return contactSelected;
    }

    public List<User> getUserList() {
        return userList;
    }

    public boolean isFriend() {
        return isFriend;
    }

    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object == null || object.getClass() != getClass()) {
            result = false;
        } else {
            User user = (User) object;
            if (this.id.equals(user.getId())) {
                result = true;
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

    @Override
    public String toString() {
        return "displayName: " + displayName + ", id: " + id
                + ", profileImageUrl: " + profileImageUrl
                + ", isOnline: " + isOnline + ", isBlocked: " + isBlocked + ", hasBlocked: " + hasBlocked + ", lastSeen: " + lastSeen
                + ", email: " + email + ", lastSeenTime: " + lastSeen;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public boolean isHasBlocked() {
        return hasBlocked;
    }

    public void setHasBlocked(boolean hasBlocked) {
        this.hasBlocked = hasBlocked;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }

    public List<String> getBlockedList() {
        return blockedList;
    }

    public void setBlockedList(List<String> blockedList) {
        this.blockedList = blockedList;
    }

    public List<String> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<String> blockList) {
        this.blockList = blockList;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(displayName);
        dest.writeString(profileImageUrl);
        dest.writeString(profileUrl);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeByte((byte) (notification ? 1 : 0));
        dest.writeString(lastSeen);
        dest.writeString(language);
        dest.writeByte((byte) (visibility ? 1 : 0));
        dest.writeByte((byte) (hasBlocked ? 1 : 0));
        dest.writeByte((byte) (isBlocked ? 1 : 0));
        dest.writeString(email);
        dest.writeStringList(friendList);
        dest.writeStringList(blockedList);
        dest.writeStringList(blockList);
        dest.writeString(configuration);
        if (video_resolution_width == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(video_resolution_width);
        }
        if (video_resolution_height == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(video_resolution_height);
        }
        dest.writeByte((byte) (isFriend ? 1 : 0));
        dest.writeByte((byte) (isDeletedUser ? 1 : 0));
        dest.writeByte((byte) (contactSelected ? 1 : 0));
        dest.writeByte((byte) (isAdmin ? 1 : 0));
        dest.writeInt(profileColor);
        dest.writeTypedList(userList);
    }
}
