/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.mqtt;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.channelize.apisdk.ApiConstants;
import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.model.Conversation;
import com.channelize.apisdk.model.Member;
import com.channelize.apisdk.model.Message;
import com.channelize.apisdk.model.User;
import com.channelize.apisdk.network.response.GenericResponse;
import com.channelize.apisdk.utils.Logcat;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import static com.channelize.apisdk.network.api.ChannelizeOkHttpUtil.GSON_INSTANCE;


public class ChannelizeMqttClient extends AWSIotMqttManager {

    // Member variables.
    public boolean isMqttConnected = false, isConnectedOnce = false;

    @SuppressLint("StaticFieldLeak")
    private static ChannelizeMqttClient instance;
    private List<ChannelizeUserEventHandler> chUserEventHandlerList = new ArrayList<>();
    private List<ChannelizeConversationEventHandler> chConversationEventHandlerList = new ArrayList<>();
    private List<ChannelizeConnectionHandler> chConnectionHandlerList = new ArrayList<>();
    private Platform platform;
    private boolean subscribeTopic = false;


    /**
     * Constructor to make the class singleton.
     *
     * @param context Context of calling class.
     */
    private ChannelizeMqttClient(final Context context) {
        super(MqttClient.generateClientId(), ApiConstants.AWS_IOT_ENDPOINT);
//        connectToServer(context);
    }

    /**
     * Method to get the instance of the ChannelizeMqttClient.
     *
     * @param context Context of calling class.
     * @return Returns the singleton instance of the class.
     */
    public synchronized static ChannelizeMqttClient getInstance(Context context) {
        if (instance == null) {
            //if there is no instance available... create new one
            instance = new ChannelizeMqttClient(context);
        }
        return instance;
    }

    /**
     * Method to set listener to get the connection updates.
     *
     * @param callback ChannelizeConnectionHandler listener instance.
     */
    public void addConnectionHandler(ChannelizeConnectionHandler callback) {
        chConnectionHandlerList.add(callback);
    }

    /**
     * Method to set listener to get the subscribe updates related to the conversation.
     *
     * @param callback ChannelizeConversationEventHandler listener instance.
     */
    public void addConversationEventHandler(ChannelizeConversationEventHandler callback) {
        chConversationEventHandlerList.add(callback);
    }

    /**
     * Method to set listener to get the subscribe updates related to user.
     *
     * @param callback ChannelizeUserEventHandler listener instance.
     */
    public void addUserEventHandler(ChannelizeUserEventHandler callback) {
        chUserEventHandlerList.add(callback);
    }

    /**
     * Method to remove connection update listener.
     *
     * @param callback ChannelizeConnectionHandler listener instance.
     */
    public void removeConnectionHandler(ChannelizeConnectionHandler callback) {
        chConnectionHandlerList.remove(callback);
    }

    /**
     * Method to remove conversation update listener.
     *
     * @param callback ChannelizeConversationEventHandler listener instance.
     */
    public void removeConversationEventHandler(ChannelizeConversationEventHandler callback) {
        chConversationEventHandlerList.remove(callback);
    }

    /**
     * Method to remove user update listener.
     *
     * @param callback ChannelizeUserEventHandler listener instance.
     */
    public void removeUserEventHandler(ChannelizeUserEventHandler callback) {
        chUserEventHandlerList.remove(callback);
    }

    /**
     * Method to get connected to the server.
     *
     * @param context Context of calling class.
     */
    public void connectToServer(Context context) {
        connectToServer(context, true);
    }

    public void connectToSeverLiveBroadCast(Context context, String topic) {
        connectToServerBroadCast(context, false, topic);
    }

    /**
     * Method to get connected to the server.
     *
     * @param context            Context of calling class.
     * @param subscribeAllTopics Topic which needs to be subscribed when mqtt gets connected.
     */
    public void connectToServer(Context context, boolean subscribeAllTopics) {
        try {
            // Connecting only when the Channelize sdk is connected.
            if (Channelize.getInstance().isConnected() && instance != null) {
                if (isMqttConnected() && subscribeAllTopics) {
                    subscribeToAllTopics();
                } else {
                    AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
                        @Override
                        public void onResult(UserStateDetails result) {
                            Logcat.d(ChannelizeMqttClient.class, "onResult: " + "user state: " + String.valueOf(result.getUserState()));

                            setAutoReconnect(true);
                            setCleanSession(false);
                            final Channelize channelize = Channelize.getInstance();
                            String currentUserId = channelize.getCurrentUserId();
                            if (!currentUserId.isEmpty()) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("userId", currentUserId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                AWSIotMqttLastWillAndTestament lastWill = new AWSIotMqttLastWillAndTestament(
                                        channelize.getApiKey() + "/users/server/offline",
                                        jsonObject.toString(), AWSIotMqttQos.QOS0);
                                setMqttLastWillAndTestament(lastWill);
                            }
                            connect(AWSMobileClient.getInstance(), new AWSIotMqttClientStatusCallback() {
                                @Override
                                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                                            final Throwable throwable) {

                                    if (status == AWSIotMqttClientStatus.Connected) {
                                        isMqttConnected = true;
                                        if (chConnectionHandlerList != null && !chConnectionHandlerList.isEmpty()) {
                                            for (ChannelizeConnectionHandler listener : chConnectionHandlerList) {
                                                listener.onConnected();
                                            }
                                        }
                                        if (subscribeAllTopics) {
                                            subscribeToAllTopics();
                                        }

                                        // If the mqtt was connected previously and its connecting again,
                                        // then make the user online. i.e mqtt is reconnected.
                                        if (isConnectedOnce) {
                                            Channelize.getInstance().setUserOnline();
                                        }
                                        isConnectedOnce = true;
                                    } else {
                                        isMqttConnected = false;
                                        if (chConnectionHandlerList != null) {
                                            for (ChannelizeConnectionHandler listener : chConnectionHandlerList) {
                                                listener.onDisconnected();
                                            }
                                        }
                                    }
                                    Logcat.d(ChannelizeMqttClient.class, "Status = " + status);
                                    Logcat.d(ChannelizeMqttClient.class, "throwable = " + throwable);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            Logcat.d(ChannelizeMqttClient.class, "onError: " + e);
                        }
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void connectToServerBroadCast(Context context, boolean subscribeAllTopics, String topic) {
        try {
            // Connecting only when the Channelize sdk is connected.
            if (Channelize.getInstance().isConnected() && instance != null) {
                if (isMqttConnected() && subscribeAllTopics) {
                    subscribeToTopic(topic);
                } else {
                    AWSMobileClient.getInstance().initialize(context, new Callback<UserStateDetails>() {
                        @Override
                        public void onResult(UserStateDetails result) {
                            Logcat.d(ChannelizeMqttClient.class, "onResult: " + "user state: " + String.valueOf(result.getUserState()));

                            setAutoReconnect(true);
                            setCleanSession(false);
                            final Channelize channelize = Channelize.getInstance();
                            String currentUserId = channelize.getCurrentUserId();
                            if (!currentUserId.isEmpty()) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("userId", currentUserId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                AWSIotMqttLastWillAndTestament lastWill = new AWSIotMqttLastWillAndTestament(
                                        channelize.getApiKey() + "/users/server/offline",
                                        jsonObject.toString(), AWSIotMqttQos.QOS0);
                                setMqttLastWillAndTestament(lastWill);
                            }
                            connect(AWSMobileClient.getInstance(), new AWSIotMqttClientStatusCallback() {
                                @Override
                                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                                            final Throwable throwable) {

                                    if (status == AWSIotMqttClientStatus.Connected) {
                                        isMqttConnected = true;
                                        if (chConnectionHandlerList != null && !chConnectionHandlerList.isEmpty()) {
                                            for (ChannelizeConnectionHandler listener : chConnectionHandlerList) {
                                                listener.onConnected();
                                            }
                                        }

                                        if (!subscribeTopic)
                                            subscribeToTopic(topic);

                                        // If the mqtt was connected previously and its connecting again,
                                        // then make the user online. i.e mqtt is reconnected.
                                        if (isConnectedOnce) {
                                            Channelize.getInstance().setUserOnline();
                                        }
                                        isConnectedOnce = true;
                                    } else {
                                        isMqttConnected = false;
                                        if (chConnectionHandlerList != null) {
                                            for (ChannelizeConnectionHandler listener : chConnectionHandlerList) {
                                                listener.onDisconnected();
                                            }
                                        }
                                    }
                                    Logcat.d(ChannelizeMqttClient.class, "Status = " + status);
                                    Logcat.d(ChannelizeMqttClient.class, "throwable = " + throwable);
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            Logcat.d(ChannelizeMqttClient.class, "onError: " + e);
                        }
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method to disconnect server.
     */
    public void disconnectServer() {
        try {
            disconnect();
            instance = null;
            isMqttConnected = false;
            isConnectedOnce = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to check that is mqtt client is currently connected or not.
     *
     * @return Returns true, if the mqtt client is connected.
     */
    public boolean isMqttConnected() {
        return isMqttConnected;
    }

    /**
     * Method to check mqtt client is initialized or not. If not then initialized it.
     */
    private void checkInitialized() {
        if (instance == null) {
            getInstance(Channelize.getInstance().getContext());
            connectToServer(Channelize.getInstance().getContext());
        } else if (!isMqttConnected()) {
            connectToServer(Channelize.getInstance().getContext());
        }
    }

    private void checkIndividualInitialization(String topic) {
        if (instance == null) {
            getInstance(Channelize.getInstance().getContext());
            connectToSeverLiveBroadCast(Channelize.getInstance().getContext(), topic);
        } else if (!isMqttConnected()) {
            connectToSeverLiveBroadCast(Channelize.getInstance().getContext(), topic);
        }
    }

    private Platform getPlatform() {
        if (platform == null) {
            platform = Platform.get();
        }
        return platform;
    }

    /**
     * Method to subscribe to a topic to get the real time updates.
     *
     * @param subscriptionTopic Topic, which needs to be subscribed.
     */
    public void subscribeToTopic(String subscriptionTopic) {
        try {
            if (subscriptionTopic.contains("live_broadcasts/")) {
                checkIndividualInitialization(subscriptionTopic);
            } else {
                checkInitialized();
            }
            if (isMqttConnected()) {
                this.subscribeTopic = true;
                subscriptionTopic = Channelize.getInstance().getApiKey() + "/" + subscriptionTopic;

                subscribeToTopic(subscriptionTopic, AWSIotMqttQos.QOS0, new AWSIotMqttNewMessageCallback() {
                    @Override
                    public void onMessageArrived(String messageTopic, byte[] data) {
                        Logcat.e("ChannelizeMqttClient", "onMessageArrived called!");
                        String selfUid = Channelize.getInstance().getCurrentUserId();
                        String response = null;
                        if (data != null) {
                            response = new String(data);
                        }
                        Logcat.d(ChannelizeMqttClient.class, messageTopic);
                        String topic = messageTopic.replace(Channelize.getInstance().getApiKey() + "/", "");
                        Logcat.d(ChannelizeMqttClient.class, "Before subscribe!  messageArrived, topic: " + topic + ", message: " + response);

                        if (response != null && !response.isEmpty()) {
                            Conversation conversation;
                            User user;
                            Message messageModel = null;
                            String userId, conversationId, messageId;
                            JSONObject responseObject = null;
                            JSONArray responseArray = null;
                            try {
                                Object object = new JSONTokener(response).nextValue();
                                if (object instanceof JSONObject) {
                                    responseObject = new JSONObject(response);
                                } else if (object instanceof JSONArray) {
                                    responseArray = new JSONArray(response);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            // Universal listener that has been invoked in every case when any type of event gets triggered.
                            if (chConnectionHandlerList != null) {
                                String finalResponse = response;
                                getPlatform().execute(() -> {
                                    for (ChannelizeConnectionHandler listener : chConnectionHandlerList) {
                                        if (topic.equalsIgnoreCase("call_invited")) {
                                        }
                                        listener.onRealTimeDataUpdate(topic, finalResponse);
                                    }
                                });
                            }

                            // Checking all the topics.

                            if (topic.equals("users/" + selfUid + "/add-friends")) {
                                if (chUserEventHandlerList != null) {
                                    responseObject = responseArray.optJSONObject(0);
                                    user = readJsonResponse(responseObject.toString(), User.class);
                                    getPlatform().execute(() -> {
                                        for (ChannelizeUserEventHandler listener : chUserEventHandlerList) {
                                            listener.onFriendAdded(user);
                                        }
                                    });
                                }

                            } else if (topic.equals("users/" + selfUid + "/remove-friends")) {
                                if (chUserEventHandlerList != null) {
                                    userId = responseArray.optString(0);
                                    getPlatform().execute(() -> {
                                        for (ChannelizeUserEventHandler listener : chUserEventHandlerList) {
                                            listener.onFriendRemoved(userId);
                                        }
                                    });
                                }

                            } else if (topic.equals("users/" + selfUid + "/conversation/join")) {
                                if (chConversationEventHandlerList != null) {
                                    conversation = readJsonResponse(responseObject.toString(), Conversation.class);
                                    getPlatform().execute(() -> {
                                        for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                            // TODO. @Ashish confirm that it's being removed in latest v2 or not
                                            listener.onUserJoined(conversation);
                                        }
                                    });
                                }

                            } else if (topic.equals("live_broadcasts/" + Channelize.getInstance().getBroadCastId() +
                                    "/start_watching")) {
                                if (chConversationEventHandlerList != null) {
                                    JSONObject finalResponseObject = responseObject;
                                    getPlatform().execute(() -> {
                                        for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                            listener.getLiveCount(finalResponseObject.toString());
                                        }
                                    });


                                }


                            } else if (topic.equals("live_broadcasts/" + Channelize.getInstance().getBroadCastId() +
                                    "/stop_watching")) {
                                if (chConversationEventHandlerList != null) {
                                    JSONObject finalResponseObject = responseObject;
                                    getPlatform().execute(() -> {

                                        for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                            listener.getLiveCount(finalResponseObject.toString());
                                        }
                                    });
                                }
                            } else if (topic.equals("live_broadcasts/" + Channelize.getInstance().getBroadCastId() +
                                    "/reaction_added")) {
                                if (chConversationEventHandlerList != null) {
                                    JSONObject finalResponseObject = responseObject;
                                    getPlatform().execute(() -> {

                                        for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                            listener.onLSCReactionsAdded(finalResponseObject.toString());
                                        }
                                    });
                                }
                            } else if (topic.equals("users/" + selfUid + "/conversation/remove")) {
                                if (chConversationEventHandlerList != null) {
                                    conversation = readJsonResponse(responseObject.toString(), Conversation.class);
                                    getPlatform().execute(() -> {
                                        for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                            listener.onUserLeft(conversation);
                                        }
                                    });
                                }

                            } else if (topic.equals("users/" + selfUid + "/conversation/delete")) {
                                if (chConversationEventHandlerList != null) {
                                    conversation = readJsonResponse(responseObject.toString(), Conversation.class);
                                    getPlatform().execute(() -> {
                                        for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                            listener.onConversationDeleted(conversation);
                                        }
                                    });
                                }
                            } else if (topic.equals("users/" + selfUid + "/conversation/clear")) {
                                if (chConversationEventHandlerList != null) {
                                    conversation = readJsonResponse(responseObject.toString(), Conversation.class);
                                    getPlatform().execute(() -> {
                                        for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                            listener.onConversationCleared(conversation);
                                        }
                                    });
                                }
                            } else if (topic.equals("users/" + selfUid + "/conversation/add-admin")) {
                                if (chConversationEventHandlerList != null) {
                                    boolean isAdmin = responseObject.optBoolean("isAdmin");
                                    conversationId = responseObject.optString("chatId");
                                    userId = responseObject.optString("userId");
                                    getPlatform().execute(() -> {
                                        for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                            listener.onConversationAdminAdded(conversationId, userId, isAdmin);
                                        }
                                    });
                                }

                            }

                            // TODO, @Ashish check it later, it's part of V2 but mqtt breaking while making model.
//                            else if (topic.equals("users/" + selfUid + "/conversation/typing")) {
//                                if (chConversationEventHandlerList != null) {
//                                    boolean isTyping = responseObject.optBoolean("isTyping");
//                                    conversation = readJsonResponse(responseObject.optJSONObject("conversation").toString(),
//                                            Conversation.class);
//                                    user = new Gson().fromJson(responseObject.optJSONObject("user").toString(), User.class);
//
//                                    getPlatform().execute(() -> {
//                                        for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
//                                            listener.onTypingStatusUpdated(conversation, user, isTyping);
//                                        }
//                                    });
//                                }
//
//                            }

                            //******************************************New MQTT Topics***************************************************************
                            //When new Message is created it could be of any type i.e text, image, video, gif, sticker
                            else if (topic.equals("users/" + selfUid + "/message_created")) {
                                if (chConversationEventHandlerList != null) {
                                    try {
                                        Message message = readJsonResponse(responseObject.getJSONObject("message").toString(), Message.class);
                                        getPlatform().execute(() -> {
                                            for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                listener.onMessageReceived(message);
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                            // When all the messages inside the conversation marked as read
                            else if (topic.equals("users/" + selfUid + "/conversation/mark_as_read")) {
                                // When the message is read by the logged-in user that was sent by another user.
                                // i.e. it will notify about that logged-in user has seen the unread messages (So remove unread message count).
                                if (chConversationEventHandlerList != null) {
                                    // Message that was read by the logged-in user.
                                    //messageId = responseObject.optString("messageId");
                                    try {
                                        conversation = new Gson().fromJson(responseObject.getJSONObject("conversation").toString(), Conversation.class);
                                        user = new Gson().fromJson(responseObject.getJSONObject("user").toString(), User.class);
                                        String timeStamp = (String) responseObject.get("timestamp");

                                        getPlatform().execute(() -> {
                                            for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                listener.onReadMessageToOwner(conversation, user, timeStamp);
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            // When all the messages inside the conversation marked as read
                            else if (topic.equals("users/" + selfUid + "/total_unread_message_count_updated")) {
                                // When the message is read by the logged-in user that was sent by another user.
                                // i.e. it will notify about that logged-in user has seen the unread messages (So remove unread message count).
                                if (chConversationEventHandlerList != null) {
                                    // Message that was read by the logged-in user.
                                    //messageId = responseObject.optString("messageId");
                                    try {
                                        conversation = new Gson().fromJson(responseObject.getJSONObject("conversation").toString(), Conversation.class);
                                        user = new Gson().fromJson(responseObject.getJSONObject("user").toString(), User.class);
                                        String timeStamp = (String) responseObject.get("timestamp");
                                        getPlatform().execute(() -> {
                                            for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                listener.onReadMessageToSelf(conversation, user, timeStamp);
                                            }
                                        });
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            //When the message is deleted for everyone in one-to-one or in group chat
                            else if (topic.equals("users/" + selfUid + "/messages/deleted_for_everyone")) {
                                if (chConversationEventHandlerList != null) {
                                    JSONArray messageJsonArray = responseObject.optJSONArray("messages");
                                    List<Message> mMessageList = new ArrayList<>();
                                    try {
                                        for (int i = 0; i < messageJsonArray.length(); i++) {
                                            mMessageList.add(new Gson().fromJson(messageJsonArray.get(i).toString(), Message.class));
                                        }
                                        if (mMessageList.size() > 0) {
                                            getPlatform().execute(() -> {
                                                for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                    listener.onMessagesDeletedForEveryOne(mMessageList);
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                            //When the message is deleted for self
                            else if (topic.equals("users/" + selfUid + "/message_deleted")) {
                                if (chConversationEventHandlerList != null) {
                                    // TODO, recheck payload.
                                    JSONArray messageJsonArray = responseObject.optJSONArray("messages");
                                    List<Message> mMessageList = new ArrayList<>();
                                    try {
                                        for (int i = 0; i < messageJsonArray.length(); i++) {
                                            mMessageList.add(new Gson().fromJson(messageJsonArray.get(i).toString(), Message.class));
                                        }
                                        if (mMessageList.size() > 0) {
                                            getPlatform().execute(() -> {
                                                for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                    listener.onMessagesDeleted(mMessageList);
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }


                            // When the logged-in user is being removed from a group.
                            else if (topic.equals("users/" + selfUid + "/removed")) {
                                String timeStamp = responseObject.optString("timestamp");
                                conversation = new Gson().fromJson(responseObject.optJSONObject("conversation").toString(),
                                        Conversation.class);
                                getPlatform().execute(() -> {
                                    for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                        listener.onUserLeft(conversation, timeStamp);
                                    }
                                });
                            }

                            // When the logged-in user is being added into a group.
                            else if (topic.equals("users/" + selfUid + "/joined")) {
                                String timeStamp = responseObject.optString("timestamp");
                                conversation = new Gson().fromJson(responseObject.optJSONObject("conversation").toString(),
                                        Conversation.class);
                                getPlatform().execute(() -> {
                                    for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                        listener.onUserJoined(conversation, timeStamp);
                                    }
                                });
                            }

                            // When the members are added in conversation
                            else if (topic.equals("users/" + selfUid + "/conversation/members_added")) {
                                if (chConversationEventHandlerList != null) {
                                    // TODO, recheck payload.
                                    String type = responseObject.optString("type");
                                    String timeStamp = responseObject.optString("timestamp");
                                    JSONObject conversationJsonObject = responseObject.optJSONObject("conversation");
                                    conversation = new Gson().fromJson(conversationJsonObject.toString(), Conversation.class);
                                    JSONArray addedmembersArray = responseObject.optJSONArray("members");
                                    List<Member> mMemberList = new ArrayList<>();
                                    try {
                                        for (int i = 0; i < addedmembersArray.length(); i++) {
                                            Member member = new Gson().fromJson(addedmembersArray.get(i).toString().replace("[", "").replace("]", ""), Member.class);
                                            mMemberList.add(member);
                                        }
                                        if (type != null && !type.isEmpty() && conversation != null && mMemberList.size() > 0 && timeStamp != null && !timeStamp.isEmpty()) {
                                            getPlatform().execute(() -> {
                                                for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                    listener.onMemberAdded(conversation, mMemberList);
                                                }
                                            });
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            }

                            // When members are removed from conversation
                            else if (topic.equals("users/" + selfUid + "/conversation/members_removed")) {
                                if (chConversationEventHandlerList != null) {
                                    // TODO, recheck payload.
                                    String type = responseObject.optString("type");
                                    String timeStamp = responseObject.optString("timestamp");
                                    JSONObject conversationJsonObject = responseObject.optJSONObject("conversation");
                                    conversation = new Gson().fromJson(conversationJsonObject.toString(), Conversation.class);
                                    JSONArray removedmembersArray = responseObject.optJSONArray("members");
                                    List<Member> mMemberList = new ArrayList<>();
                                    try {
                                        for (int i = 0; i < removedmembersArray.length(); i++) {
                                            Member member = new Gson().fromJson(removedmembersArray.get(i).toString().replace("[", "").replace("]", ""), Member.class);
                                            mMemberList.add(member);
                                        }
                                        if (type != null && !type.isEmpty() && conversation != null && mMemberList.size() > 0 && timeStamp != null && !timeStamp.isEmpty()) {
                                            getPlatform().execute(() -> {
                                                for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                    listener.onMemberRemoved(conversation, mMemberList);
                                                }
                                            });
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                            }

                            //When admin is added in conversation
                            else if (topic.equals("users/" + selfUid + "/conversation/admin_added")) {
                                if (chConversationEventHandlerList != null) {
                                    // TODO, recheck payload.

                                    String type = responseObject.optString("type");
                                    JSONObject conversationJsonObject = responseObject.optJSONObject("conversation");
                                    conversation = new Gson().fromJson(conversationJsonObject.toString(), Conversation.class);

                                    JSONObject adminUserObject = responseObject.optJSONObject("adminUser");
                                    Member member = new Gson().fromJson(adminUserObject.toString(), Member.class);

                                    String timeStamp = responseObject.optString("timestamp");

                                    if (type != null && !type.isEmpty() && conversation != null
                                            && member != null && timeStamp != null && !timeStamp.isEmpty()) {
                                        getPlatform().execute(() -> {
                                            for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                listener.onAdminAdded(conversation, member);
                                            }
                                        });
                                    }
                                }

                            }

                            //When the profile pic or conversation title is changed
                            else if (topic.equals("users/" + selfUid + "/conversation/updated")) {
                                if (chConversationEventHandlerList != null) {
                                    // TODO, recheck payload.

                                    String type = responseObject.optString("type");
                                    JSONObject conversationJsonObject = responseObject.optJSONObject("conversation");
                                    String timeStamp = responseObject.optString("timestamp");

                                    conversation = new Gson().fromJson(conversationJsonObject.toString(), Conversation.class);

                                    if (type != null && !type.isEmpty() && conversation != null && timeStamp != null && !timeStamp.isEmpty()) {
                                        getPlatform().execute(() -> {
                                            for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                listener.onConversationUpdated(conversation);
                                            }
                                        });
                                    }
                                }
                            }

                            //When the conversation is cleared
                            else if (topic.equals("users/" + selfUid + "/conversation_cleared")) {
                                if (chConversationEventHandlerList != null) {
                                    // TODO, recheck payload.

                                    String type = responseObject.optString("type");
                                    JSONObject conversationJsonObject = responseObject.optJSONObject("conversation");
                                    String timeStamp = responseObject.optString("timestamp");

                                    conversation = new Gson().fromJson(conversationJsonObject.toString(), Conversation.class);
                                    if (type != null && !type.isEmpty() && conversation != null && timeStamp != null && !timeStamp.isEmpty()) {
                                        getPlatform().execute(() -> {
                                            for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                listener.onConversationCleared(conversation);
                                            }
                                        });
                                    }
                                }
                            }

                            //When the conversation is deleted
                            else if (topic.equals("users/" + selfUid + "/conversation_deleted")) {
                                if (chConversationEventHandlerList != null) {
                                    // TODO, recheck payload.

                                    String type = responseObject.optString("type");
                                    JSONObject conversationJsonObject = responseObject.optJSONObject("conversation");
                                    String timeStamp = responseObject.optString("timestamp");

                                    conversation = new Gson().fromJson(conversationJsonObject.toString(), Conversation.class);
                                    if (type != null && !type.isEmpty() && conversation != null && timeStamp != null && !timeStamp.isEmpty()) {
                                        getPlatform().execute(() -> {
                                            for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                listener.onConversationDeleted(conversation);
                                            }
                                        });
                                    }
                                }
                            }
                            //When the user comes online and goes offline
                            else if (topic.equals("users/status_updated")) {
                                if (chUserEventHandlerList != null) {
                                    // TODO, recheck payload.

                                    String type = responseObject.optString("type");
                                    String timeStamp = responseObject.optString("timestamp");
                                    JSONObject userObject = responseObject.optJSONObject("user");
                                    user = new Gson().fromJson(userObject.toString(), User.class);

                                    if (chUserEventHandlerList != null && user != null) {
                                        // If an user goes offline.
                                        getPlatform().execute(() -> {
                                            for (ChannelizeUserEventHandler listener : chUserEventHandlerList) {
                                                listener.onUserStatusUpdated(user);
                                            }
                                        });
                                    }
                                }
                            }
                            //When the Mute status is updated
                            else if (topic.equals("users/" + selfUid + "/mute_updated")) {
                                if (chConversationEventHandlerList != null) {

                                    String type = responseObject.optString("type");
                                    String timeStamp = responseObject.optString("timestamp");
                                    //Conversation Object
                                    JSONObject conversationObject = responseObject.optJSONObject("conversation");
                                    conversation = new Gson().fromJson(conversationObject.toString(), Conversation.class);
                                    //Mute User Object
                                    JSONObject userObject = responseObject.optJSONObject("muteUpdatedUser");
                                    user = new Gson().fromJson(userObject.toString(), User.class);

                                    if (conversation != null && user != null && type != null && timeStamp != null && user.getId().equals(selfUid)) {
                                        getPlatform().execute(() -> {
                                            for (ChannelizeConversationEventHandler listener : chConversationEventHandlerList) {
                                                listener.onUserMuteStatusUpdated(conversation.getConversationId(), conversation.getMute());
                                            }
                                        });
                                    }

                                }
                            }

                            //When the user is Blocked
                            else if (topic.equals("users/" + selfUid + "/blocked")) {
                                if (chUserEventHandlerList != null && responseObject != null) {
                                    // If logged-in user has blocked some another user then blocker will be the logged-in and vice versa.
                                    User blockerUser = new Gson().fromJson(responseObject.optJSONObject("blocker").toString(), User.class);
                                    User blockeeUser = new Gson().fromJson(responseObject.optJSONObject("blockee").toString(), User.class);
                                    getPlatform().execute(() -> {
                                        for (ChannelizeUserEventHandler listener : chUserEventHandlerList) {
                                            listener.onUserBlocked(blockerUser, blockeeUser);
                                        }
                                    });
                                }

                            }

                            //When the user is Un-Blocked
                            else if (topic.equals("users/" + selfUid + "/unblocked")) {
                                if (chUserEventHandlerList != null) {
                                    // If logged-in user is un-blocked by some another user.
                                    User unBlockerUser = new Gson().fromJson(responseObject.optJSONObject("unblocker").toString(), User.class);
                                    User unBlockeeUser = new Gson().fromJson(responseObject.optJSONObject("unblockee").toString(), User.class);
                                    getPlatform().execute(() -> {
                                        for (ChannelizeUserEventHandler listener : chUserEventHandlerList) {
                                            listener.onUserUnBlocked(unBlockerUser, unBlockeeUser);
                                        }
                                    });
                                }
                            }

                            //When the call is received
                            else if (topic.equals("users/" + selfUid + "/call/invited")) {
                                Log.e("ChannelizeMqttClient", "/call/invited called!");

                            }
                        }
                        Logcat.d(ChannelizeMqttClient.class, "subscribe!  messageArrived, topic: " + topic + ", message: " + response);
                    }
                });
            }

        } catch (Exception ex) {
            System.err.println("Exception subscribing");
            ex.printStackTrace();
        }
    }

    /**
     * Method to subscribe to all the topic to listen for the real time update.
     */
    private void subscribeToAllTopics() {
        String selfUid = Channelize.getInstance().getCurrentUserId();

        subscribeToTopic("users/" + selfUid + "/add-friends");
        subscribeToTopic("users/" + selfUid + "/remove-friends");
        subscribeToTopic("users/online");
        subscribeToTopic("users/offline");

        //subscribeToTopic("self/" + selfUid + "/conversation/mark-as-read");
        //subscribeToTopic("message-owner/" + selfUid + "/conversation/mark-as-read");

        //New Comments
        //subscribeToTopic("users/" + selfUid + "/conversation/add-admin");
        //subscribeToTopic("users/" + selfUid + "/messages/delete");
        //subscribeToTopic("users/" + selfUid + "/conversation/clear");
        //subscribeToTopic("users/" + selfUid + "/conversation/delete");
        //subscribeToTopic("users/" + selfUid + "/conversation/messages");
        subscribeToTopic("users/" + selfUid + "/conversation/remove");
        subscribeToTopic("users/" + selfUid + "/conversation/join");
        subscribeToTopic("users/" + selfUid + "/conversation/update-info");
        //subscribeToTopic("users/" + selfUid + "/conversation/mute");
        //subscribeToTopic("users/" + selfUid + "/conversation/messages/deleted-for-everyone");


        //**************************************************MQTT New Topics*****************************************************

        // Mark as read message
        subscribeToTopic("users/" + selfUid + "/conversation/mark_as_read");
        subscribeToTopic("users/" + selfUid + "/total_unread_message_count_updated");

        //When a new Member is added
        subscribeToTopic("users/" + selfUid + "/conversation/members_added");

        //When a Member is removed
        subscribeToTopic("users/" + selfUid + "/conversation/members_removed");

        //When admin is added
        subscribeToTopic("users/" + selfUid + "/conversation/admin_added");

        //When the Conversation updated
        subscribeToTopic("users/" + selfUid + "/conversation/updated");

        //When the conversation is cleared
        subscribeToTopic("users/" + selfUid + "/conversation_cleared");

        //When the conversation is deleted
        subscribeToTopic("users/" + selfUid + "/conversation_deleted");

        //When someone is typing in the conversation
        subscribeToTopic("users/" + selfUid + "/conversation/typing");

        //When a user is joined
        subscribeToTopic("users/" + selfUid + "/joined");

        //When a user is removed
        subscribeToTopic("users/" + selfUid + "/removed");

        //When the mute status updated
        subscribeToTopic("users/" + selfUid + "/mute_updated");

        //Total unread countof messages
        subscribeToTopic("users/" + selfUid + "/total_unread_message_count_updated");

        //When a message is created
        subscribeToTopic("users/" + selfUid + "/message_created");

        //When the message is deleted for every one
        subscribeToTopic("users/" + selfUid + "/messages/deleted_for_everyone");

        //When message is deleted for self
        subscribeToTopic("users/" + selfUid + "/message_deleted");


        //When the status of any user changes from online to offline
        subscribeToTopic("users/status_updated"); // For Online Status

        //When the user is blocked
        subscribeToTopic("users/" + selfUid + "/blocked");

        //When the user is unblocked
        subscribeToTopic("users/" + selfUid + "/unblocked");


        //**************************************************MQTT New Call Topics*****************************************************

        subscribeToTopic("users/" + selfUid + "/call/deleted");

        subscribeToTopic("users/" + selfUid + "/call/invited");
        Log.e("subscribeToTopic", "topic subscribed: " + "users/" + selfUid + "/call/invited");

        subscribeToTopic("users/" + selfUid + "/call/joined");

        subscribeToTopic("users/" + selfUid + "/call/rejected");

    }

    /**
     * Method to unSubscribe a topic.
     *
     * @param subscriptionTopic Topic, which needs to be un-subscribe.
     */
    public void unSubscribeToTopic(String subscriptionTopic) {
        try {
            checkInitialized();
            if (isMqttConnected()) {
                subscriptionTopic = Channelize.getInstance().getApiKey() + "/" + subscriptionTopic;
                unsubscribeTopic(subscriptionTopic);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Method to publish a topic with the data.
     * @param topic Topic which needs to be published.
     * @param parameter JSONObject data which needs to be send.
     */
    public void publishTopic(String topic, JSONObject parameter) {
        try {
            checkInitialized();
            if (isMqttConnected()) {
                Logcat.d(ChannelizeMqttClient.class, "topic: " + topic + ", parameter: " + parameter);
                topic = Channelize.getInstance().getApiKey() + "/" + topic;
                publishData(parameter.toString().getBytes(), topic, AWSIotMqttQos.QOS0);
                Log.e("Publishing", topic + parameter.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logcat.d("Publishing Exception", e.toString());
        }
    }

    /**
     * Method to get the generic response from the response body.
     *
     * @param responseBody  ResponseBody which contains the response.
     * @param responseClass Class in which data needs to be parsed.
     * @return Returns the model.
     */
    private <T extends GenericResponse> T readJsonResponse(String responseBody, @NonNull Class<T> responseClass) {
        return GSON_INSTANCE.fromJson(responseBody, responseClass);
    }

}
