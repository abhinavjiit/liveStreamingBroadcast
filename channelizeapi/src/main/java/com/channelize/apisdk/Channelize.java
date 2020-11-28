/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.channelize.apisdk.model.User;
import com.channelize.apisdk.network.api.ChannelizeApi;
import com.channelize.apisdk.network.api.ChannelizeApiClient;
import com.channelize.apisdk.network.api.ChannelizeOkHttpUtil;
import com.channelize.apisdk.network.mqtt.ChannelizeConnectionHandler;
import com.channelize.apisdk.network.mqtt.ChannelizeConversationEventHandler;
import com.channelize.apisdk.network.mqtt.ChannelizeMqttClient;
import com.channelize.apisdk.network.mqtt.ChannelizeUserEventHandler;
import com.channelize.apisdk.network.response.ChannelizeError;
import com.channelize.apisdk.network.response.CompletionHandler;
import com.channelize.apisdk.network.response.LoginResponse;
import com.channelize.apisdk.network.response.RequestResponse;
import com.channelize.apisdk.utils.ChannelizePreferences;
import com.channelize.apisdk.utils.CoreFunctionsUtil;
import com.channelize.apisdk.utils.Logcat;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * The {@link Channelize} class stores common configuration and state for Channelize SDK.
 */
public class Channelize {

    private Context applicationContext;
    private String currentUserId = "";
    private volatile String apiKey;
    private volatile String googlePlacesKey;
    private volatile boolean isConnected = false;
    private ChannelizeApi channelizeApi;
    private String broadCastId;
    private String conversationId;


    public String getBroadCastId() {
        return broadCastId;
    }

    public void setBroadCastId(String broadCastId) {
        this.broadCastId = broadCastId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getConversationId() {
        return conversationId;
    }

    private static final String CHANNELIZE_NOT_INITIALIZED_MESSAGE = "Must initialize Channelize before using getInstance()";
    @SuppressLint("StaticFieldLeak")
    private static Channelize instance;


    private Channelize(ChannelizeConfig config) {
        applicationContext = config.applicationContext;
        apiKey = config.apiKey;
        String currentUserId = ChannelizePreferences.getCurrentUserId(applicationContext);
        if (currentUserId != null && !currentUserId.isEmpty()
                && !currentUserId.equals("null")) {
            this.currentUserId = currentUserId;
        }
        googlePlacesKey = CoreFunctionsUtil.getMetaDataValue(applicationContext, ApiConstants.META_GOOGLE_KEY);
        Logcat.setLoggingEnabled(config.isLoggingEnabled);
    }


    /**
     * Entry point to initialize the Channelize SDK.
     * <p>
     * Only the Application context is retained.
     * See http://developer.android.com/resources/articles/avoiding-memory-leaks.html
     * <p>
     * Should be called from {@code OnCreate()} method of custom {@code Application} class.
     * <pre>
     * public class SampleApplication extends Application {
     *   &#64;Override
     *   public void onCreate() {
     *     final ChannelizeConfig config = new ChannelizeConfig.Builder(this).build();
     *     Channelize.initialize(config);
     *   }
     * }
     * </pre>
     *
     * @param config {@link ChannelizeConfig} user for initialization
     */
    public static void initialize(ChannelizeConfig config) {
        createChannelize(config);
    }

    private static synchronized Channelize createChannelize(ChannelizeConfig config) {
        if (instance == null) {
            instance = new Channelize(config);
            return instance;
        }
        return instance;
    }

    private static void checkInitialized() {
        if (instance == null) {
            throw new IllegalStateException(CHANNELIZE_NOT_INITIALIZED_MESSAGE);
        }
    }

    /**
     * @return Single instance of the {@link Channelize}.
     */
    public static Channelize getInstance() {
        checkInitialized();
        return instance;
    }

    private void setInstance() {
        instance = null;
    }

    /**
     * @return A {@link Context}.
     */
    public Context getContext() {
        return instance.applicationContext;
    }

    /**
     * @return The default api calling url of channelize.
     */
    public String getApiDefaultUrl() {
        return ApiConstants.CHANNELIZE_API_URL;
    }

    public String getCallApiUrl() {
        return ApiConstants.CHANNELIZE_CALL_API_URL;
    }

    /**
     * @return The Public api key to access all of the APIs.
     */
    public String getApiKey() {
        return instance.apiKey;
    }

    public void setApiKey(String apiKey) {
        instance.apiKey = apiKey;
    }

    public String getCurrentUserId() {
        return instance.currentUserId != null
                ? instance.currentUserId : "";
    }

    public void setCurrentUserId(String currentUserId) {
        instance.currentUserId = currentUserId;
    }

    /**
     * @return The google places api key to use map features.
     */
    public String getGooglePlacesKey() {
        return instance.googlePlacesKey;
    }

    /**
     * Method to connect to the channelize mqtt server.
     */
    public static void connect() {
        Channelize channelize = Channelize.getInstance();
        channelize.updateConnectStatus(true);
        if (channelize.getCurrentUserId() != null && !channelize.getCurrentUserId().isEmpty()) {
            ChannelizeMqttClient.getInstance(channelize.getContext()).connectToServer(channelize.getContext());
        }
    }

    /**
     * Method to disconnect from the channelize mqtt server.
     */
    public static void disconnect() {
        Channelize channelize = Channelize.getInstance();
        channelize.updateConnectStatus(false);
        ChannelizeMqttClient.getInstance(channelize.getContext()).disconnectServer();
    }

    /**
     * Method to set listener to get the connection updates.
     *
     * @param callback ChannelizeConnectionHandler listener instance.
     */
    public static void addConnectionHandler(ChannelizeConnectionHandler callback) {
        ChannelizeMqttClient.getInstance(Channelize.getInstance().getContext()).addConnectionHandler(callback);
    }

    /**
     * Method to set listener to get the subscribe updates related to the conversation.
     *
     * @param callback ChannelizeConversationEventHandler listener instance.
     */
    public static void addConversationEventHandler(ChannelizeConversationEventHandler callback) {
        ChannelizeMqttClient.getInstance(Channelize.getInstance().getContext()).addConversationEventHandler(callback);
    }

    /**
     * Method to set listener to get the subscribe updates related to user.
     *
     * @param callback ChannelizeUserEventHandler listener instance.
     */
    public static void addUserEventHandler(ChannelizeUserEventHandler callback) {
        ChannelizeMqttClient.getInstance(Channelize.getInstance().getContext()).addUserEventHandler(callback);
    }

    /**
     * Method to remove connection update listener.
     *
     * @param callback ChannelizeConnectionHandler listener instance.
     */
    public static void removeConnectionHandler(ChannelizeConnectionHandler callback) {
        ChannelizeMqttClient.getInstance(Channelize.getInstance().getContext()).removeConnectionHandler(callback);
    }

    /**
     * Method to remove conversation update listener.
     *
     * @param callback ChannelizeConversationEventHandler listener instance.
     */
    public static void removeConversationEventHandler(ChannelizeConversationEventHandler callback) {
        ChannelizeMqttClient.getInstance(Channelize.getInstance().getContext()).removeConversationEventHandler(callback);
    }

    /**
     * Method to remove user update listener.
     *
     * @param callback ChannelizeUserEventHandler listener instance.
     */
    public static void removeUserEventHandler(ChannelizeUserEventHandler callback) {
        ChannelizeMqttClient.getInstance(Channelize.getInstance().getContext()).removeUserEventHandler(callback);
    }

    /***
     * Method to publish a topic on mqtt with the data.
     * @param topic Topic which needs to be published.
     * @param parameter JSONObject data which needs to be send.
     */
    public static void publishMqttData(String topic, JSONObject parameter) {
        ChannelizeMqttClient.getInstance(Channelize.getInstance().getContext()).publishTopic(topic, parameter);
    }

    /**
     * Method to connect to the channelize mqtt server but do not subscribe all the topics.
     */
    public static void connectToMqttServer(boolean isSubscribeToAllTopics) {
        Channelize channelize = Channelize.getInstance();
        channelize.updateConnectStatus(true);
        if (channelize.getCurrentUserId() != null && !channelize.getCurrentUserId().isEmpty()) {
            ChannelizeMqttClient.getInstance(channelize.getContext()).connectToServer(channelize.getContext(), isSubscribeToAllTopics);
        }
    }

    /**
     * Method to check mqtt client is connected to mqtt server or not.
     *
     * @return Returns true, if connected to the mqtt server.
     */
    public static boolean isMqttConnected() {
        return ChannelizeMqttClient.getInstance(Channelize.getInstance().getContext()).isMqttConnected();
    }

    /**
     * Method to subscribe to mqtt for the each user id, to listen for the real time update on user.
     *
     * @param userList List of User on which subscriber needs to be applied.
     */
    public void addSubscriberOnUser(List<User> userList) {
        if (isConnected && userList != null && !userList.isEmpty()) {
            for (User user : userList) {
                addSubscriberOnUser(user.getId());
            }
        }
    }

    /**
     * Method to subscribe to mqtt for the user id, to listen for the real time update on user.
     *
     * @param userId UserId on which subscriber needs to be applied.
     */
    public void addSubscriberOnUser(String userId) {
        if (isConnected && userId != null && !userId.isEmpty()) {
            addSubscriber("users/" + userId);
        }
    }

    /**
     * Method to subscribe to mqtt for the real time update on a topic.
     *
     * @param topic topic on which subscriber needs to be applied.
     */
    public void addSubscriber(String topic) {
        if (isConnected && topic != null && !topic.isEmpty()) {
            ChannelizeMqttClient.getInstance(applicationContext).subscribeToTopic(topic);
        }
    }

    /* method to  unsubscribe the topic*/
    public void removeSubscriberTopic(String topic) {
        if (isConnected && topic != null && !topic.isEmpty()) {
            ChannelizeMqttClient.getInstance(applicationContext).unSubscribeToTopic(topic);
        }
    }

    /**
     * Method to update when mqtt is requested to be connect or disconnect.
     *
     * @param isConnected True, if requested to be connect.
     */
    public void updateConnectStatus(boolean isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * Method to return the connected status of the mqtt.
     *
     * @return True, if the connection is requested already.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Method to login the user in Channelize App.
     *
     * @param email             Email of the user.
     * @param password          Password of the user.
     * @param completionHandler CompletionHandler instance which will invoked when login request gets executed.
     */
    public void loginWithEmailPassword(final String email, final String password,
                                       final CompletionHandler<LoginResponse> completionHandler) {

        Map<String, Object> postParam = new HashMap<>();
        postParam.put("email", email);
        postParam.put("password", password);
        postParam.put("authenticationType", 1);
        ChannelizeOkHttpUtil apiClient = ChannelizeOkHttpUtil.getInstance(applicationContext);
        apiClient.sendPostRequest(getApiDefaultUrl() + "users/login?include=user", postParam,
                LoginResponse.class, new CompletionHandler<LoginResponse>() {
                    @Override
                    public void onComplete(LoginResponse loginResponse, ChannelizeError error) {
                        handleLoginResponse(loginResponse, completionHandler);
                    }

                });
    }

    public void logoutUserFromChannelize() {
        if (currentUserId != null && !currentUserId.isEmpty()) {
            checkApiClientInitialized();
            channelizeApi.logoutUserFromChannelize(null);
        }
    }

    /**
     * Method to login the user in Channelize App.
     *
     * @param userId            UserId of the user.
     * @param clientServerToken Client-Server access token.
     * @param completionHandler CompletionHandler instance which will invoked when login request gets executed.
     */
    public void loginWithUserId(final String userId, final String clientServerToken,
                                final CompletionHandler<LoginResponse> completionHandler) {

        Map<String, Object> postParam = new HashMap<>();
        postParam.put("userId", userId);
        postParam.put("pmClientServerToken", clientServerToken);
        postParam.put("authenticationType", 0);
        ChannelizeOkHttpUtil apiClient = ChannelizeOkHttpUtil.getInstance(applicationContext);
        apiClient.sendPostRequest(getApiDefaultUrl() + "users/login?include=user", postParam,
                LoginResponse.class, new CompletionHandler<LoginResponse>() {
                    @Override
                    public void onComplete(LoginResponse loginResponse, ChannelizeError error) {
                        if (completionHandler != null) {
                            completionHandler.onComplete(loginResponse, error);
                        }
                        handleLoginResponse(loginResponse, completionHandler);
                    }

                });
    }

    /**
     * Method to process login response.
     *
     * @param loginResponse     Response of the login.
     * @param completionHandler CompletionHandler instance which will invoked when login request gets executed.
     */
    private void handleLoginResponse(LoginResponse loginResponse,
                                     CompletionHandler<LoginResponse> completionHandler) {
        if (loginResponse != null && loginResponse.getUser() != null) {
            ChannelizePreferences.setAccessToken(applicationContext, loginResponse.getAccessToken());
            ChannelizePreferences.setChannelizeCurrentUser(applicationContext, loginResponse.getUser());
            addAuthHeader();
            ChannelizePreferences.setCurrentUserId(applicationContext, loginResponse.getUserId());
            this.currentUserId = loginResponse.getUserId();
            ChannelizePreferences.setCurrentUserProfileImage(applicationContext, loginResponse.getUser().getProfileImageUrl());
            ChannelizePreferences.setCurrentUserName(applicationContext, loginResponse.getUser().getDisplayName());
            if (isConnected()) {
                connect();
            }
        }
        completionHandler.onComplete(loginResponse, null);
    }

    /**
     * Method to add auth header into api calls.
     */
    public void addAuthHeader() {
        ChannelizeOkHttpUtil.getInstance(applicationContext).addAuthHeader();
    }

    /**
     * Method to logged out the current user.
     */
    public static void logout(CompletionHandler<RequestResponse> completionHandler) {
        new AsyncTask<Void, Boolean, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Channelize.getInstance().logoutUserFromChannelize();
                    ChannelizeOkHttpUtil.getInstance(Channelize.getInstance().getContext()).removeAuthHeader();
                    Channelize.getInstance().setUserOffline();
                    ChannelizePreferences.clearSharedPreferences(Channelize.getInstance().applicationContext);
                    ChannelizeOkHttpUtil.setInstance();
                    return null;
                } catch (Exception e) {
                    Log.d("LOGOUT_ERROR", "LOGOUT_ERROR");
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Channelize.getInstance().setCurrentUserId("");
                if (completionHandler != null) {
                    completionHandler.onComplete(new RequestResponse(true), null);
                }
            }
        }.execute();

    }

    /**
     * Method to register fcm token.
     *
     * @param fcmToken FCM Token which needs to be register.
     */
    public void registerFcmToken(String fcmToken) {
        if (fcmToken != null && getCurrentUserId() != null && !getCurrentUserId().isEmpty()) {
            ChannelizeOkHttpUtil apiClient = ChannelizeOkHttpUtil.getInstance(applicationContext);
            Map<String, Object> postParam = new HashMap<>();
            postParam.put("userId", getCurrentUserId());
            postParam.put("deviceType", "android");
            postParam.put("deviceId", getDeviceUUID(applicationContext));
            postParam.put("token", fcmToken);
            apiClient.sendPostRequest(getApiDefaultUrl() + "push_notification_tokens",
                    postParam);
        }
    }

    /**
     * Method to update the fcm token.
     *
     * @param fcmToken FCM Token which needs to be register.
     */
    public void updateFcmToken(String fcmToken) {
        registerFcmToken(fcmToken);
    }

    /**
     * Method to set the logged-in user offline.
     */
    public void setUserOffline() {
        if (currentUserId != null && !currentUserId.isEmpty()) {
            checkApiClientInitialized();
            channelizeApi.setUserOffline(null);
        }
    }

    /**
     * Method to set the logged-in user online.
     */
    public void setUserOnline() {
        if (currentUserId != null && !currentUserId.isEmpty()) {
            checkApiClientInitialized();
            channelizeApi.setUserOnline(null);
        }
    }

    /**
     * Method to check if the channelizeAPI is initialized or not.
     */
    private void checkApiClientInitialized() {
        if (channelizeApi == null) {
            channelizeApi = new ChannelizeApiClient(applicationContext);
        }
    }

    @SuppressLint("HardwareIds")
    private String getDeviceUUID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

}
