/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;


public class ApiConstants {

    public static final String APP_ID = "2336dbc90d61465ebce940642b537ba4";

    static final String CHANNELIZE_CALL_API_URL = "https://callsapi.channelize.io/v2/";

    // This will be the aws api calling url.
    static final String CHANNELIZE_API_URL = "https://api.channelize.io/v2/";
    //static final String CHANNELIZE_API_URL = "https://api.channelize.io/devv2/";
    //static final String CHANNELIZE_API_URL = "https://phn86j6grl.execute-api.us-east-1.amazonaws.com/dev/";
    public static final String AWS_IOT_ENDPOINT = "am1p7ut7tcfuv-ats.iot.us-east-1.amazonaws.com";

    // Constant variables.
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_FAILED = "failed";
    public static final String PROFILE_IMAGE_URL = "profile";
    public static final String CONTACT_SEARCH = "contact_search";
    public static final String GROUP_SEARCH = "group_search";

    // Content type work.
    public static final String CONTENT_TYPE = "contentType";
    public static final int META_MESSAGE_CONTENT_TYPE = 1;
    public static final int STICKER_GIF_CONTENT_TYPE = 2;
    public static final int LOCATION_CONTENT_TYPE = 3;
    // This is for content type work.
    public static final int INTEGRATED_CONTENT_TYPE_VALUE = 3;
    public static final int UPCOMING_CONTENT_TYPE_VALUE = 4;

    public static final String META_GROUP_CREATE = "meta_group_create";
    public static final String META_GROUP_ADD_MEMBERS = "meta_group_add_members";
    public static final String META_GROUP_REMOVE_MEMBERS = "meta_group_remove_members";
    public static final String META_GROUP_LEAVE = "meta_group_leave";
    public static final String META_GROUP_MAKE_ADMIN = "meta_group_make_admin";
    public static final String META_GROUP_CHANGE_TITLE = "meta_group_change_title";
    public static final String META_GROUP_CHANGE_PHOTO = "meta_group_change_photo";
    public static final String META_CALL_VOICE_MISSED = "meta_call_voice_missed";
    public static final String META_CALL_VIDEO_MISSED = "meta_call_video_missed";

    public static final String META_GOOGLE_KEY = "com.google.android.geo.API_KEY";

    // Request types.
    public static final String GET_REQUEST = "GET";
    public static final String POST_REQUEST = "POST";
    public static final String PUT_REQUEST = "PUT";
    public static final String DELETE_REQUEST = "DELETE";

    // Data limit on each screen.
    public static final int CONTACTS_LIMIT = 50;
    public static final int CONVERSATION_LIMIT = 30;
    public static final int MESSAGE_LIMIT = 20;

    // modules.
    public static final String MODULE_STICKER_GIF = "stickers-gifs";

    @Retention(SOURCE)
    @StringDef({ATTACHMENT_TEXT, ATTACHMENT_IMAGE, ATTACHMENT_VIDEO, ATTACHMENT_AUDIO, ATTACHMENT_STICKER, ATTACHMENT_GIF, ATTACHMENT_LOCATION, ATTACHMENT_LINK, ATTACHMENT_FILE})
    public @interface ATTACHMENT {
    }

    public static final String ATTACHMENT_TEXT = "text";
    public static final String ATTACHMENT_IMAGE = "image";
    public static final String ATTACHMENT_VIDEO = "video";
    public static final String ATTACHMENT_AUDIO = "audio";
    public static final String ATTACHMENT_LINK = "link";
    public static final String ATTACHMENT_FILE = "file";
    public static final String ATTACHMENT_STICKER = "sticker";
    public static final String ATTACHMENT_GIF = "gif";
    public static final String ATTACHMENT_LOCATION = "location";

    public static final String ATTACHMENT_NORMAL = "normal";
    public static final String ATTACHMENT_REPLY = "reply";
    public static final String ATTACHMENT_ADMIN = "admin";
}
