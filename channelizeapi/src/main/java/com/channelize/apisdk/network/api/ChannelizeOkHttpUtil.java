/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.api;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.channelize.apisdk.ApiConstants;
import com.channelize.apisdk.Channelize;
import com.channelize.apisdk.R;
import com.channelize.apisdk.network.response.ChannelizeError;
import com.channelize.apisdk.network.response.CompletionHandler;
import com.channelize.apisdk.network.response.GenericResponse;
import com.channelize.apisdk.network.response.RequestResponse;
import com.channelize.apisdk.network.response.json.MainAdapterFactory;
import com.channelize.apisdk.utils.ChannelizePreferences;
import com.channelize.apisdk.utils.CoreFunctionsUtil;
import com.channelize.apisdk.utils.Logcat;
import com.channelize.apisdk.utils.TLSSocketFactory;
import com.channelize.apisdk.utils.TrustManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


public class ChannelizeOkHttpUtil extends OkHttpClient {

    // Member variables.
    @SuppressLint("StaticFieldLeak")
    private static ChannelizeOkHttpUtil instance;
    private Context mContext;
    private Request.Builder requestBuilder;
    private String postUrl, requestType, apiDefaultUrl;
    private static final long REQUEST_TIMEOUT = 60;

    //    public static final Gson GSON_INSTANCE = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new DateDeserializer())
    public static final Gson GSON_INSTANCE = new GsonBuilder()
//            .registerTypeHierarchyAdapter(Date.class, new DateSerializer())
//            .registerTypeHierarchyAdapter(boolean.class, new BooleanDeserializer())
//            .registerTypeHierarchyAdapter(int.class, new IntDeserializer())
            .registerTypeAdapterFactory(new MainAdapterFactory())
            .create();


    /**
     * Public constructor to initialize the class with required params.
     *
     * @param context Context of calling class.
     */
    private ChannelizeOkHttpUtil(Context context) {
        try {
            mContext = context;
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            Interceptor mainInterceptor = new Interceptor() {
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request request = chain.request();
                    // try the request
                    Response response = chain.proceed(request);
                    int tryCount = 0;
                    while (!response.isSuccessful() && tryCount < 3) {
                        Logcat.d(ChannelizeOkHttpUtil.class, "intercept, Request is not successful - " + tryCount);
                        tryCount++;
                        // retry the request
                        response = chain.proceed(request);
                    }

                    // otherwise just pass the original response on
                    return response;
                }
            };
            apiDefaultUrl = Channelize.getInstance().getApiDefaultUrl();
            newBuilder().readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(0, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .sslSocketFactory(new TLSSocketFactory(), new TrustManager())
                    .addInterceptor(mainInterceptor)
                    .addInterceptor(httpLoggingInterceptor).build();

            requestBuilder = new Request.Builder();
            Logcat.d(ChannelizeOkHttpUtil.class, "getApiKey: " + Channelize.getInstance().getApiKey());
            requestBuilder.addHeader("Public-Key", Channelize.getInstance().getApiKey());
            addAuthHeader();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    /**
     * Add the Authorization header once user is logged-in into app.
     * AccessToken will be received in login response.
     */
    public void addAuthHeader() {
        String accessToken = ChannelizePreferences.getAccessToken(mContext);
        if (accessToken != null && !accessToken.isEmpty()) {
            String base64EncodedAccessToken = null;
            try {
                base64EncodedAccessToken = Base64.encodeToString(accessToken
                        .getBytes("UTF-8"), Base64.NO_WRAP);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Logcat.d(ChannelizeOkHttpUtil.class, "addAuthHeader base64EncodedAccessToken: " + base64EncodedAccessToken);
            requestBuilder.addHeader("Authorization", "Bearer " + base64EncodedAccessToken);

            Logcat.d("Header", requestBuilder.toString());
        }
    }

    /**
     * Method to remove th auth header when logging-out from the channelize.
     */
    public void removeAuthHeader() {
        requestBuilder.removeHeader("Authorization");
    }

    /**
     * Method to get the singleton instance.
     *
     * @param context Context of calling class.
     * @return Returns the singleton instance of ChannelizeOkHttpUtil to make the networking calls.
     */
    public synchronized static ChannelizeOkHttpUtil getInstance(Context context) {
        if (instance == null) {
            instance = new ChannelizeOkHttpUtil(context);
        }
        return instance;
    }

    /**
     * Method to send the message request when message is of text type.
     *
     * @param postParam PostParam which contains the message parameters.
     */
    public void sendMessage(Map<String, Object> postParam, Class responseClass, CompletionHandler completionHandler) {
        postUrl = apiDefaultUrl + "messages/send";
        requestType = ApiConstants.POST_REQUEST;
        performAction((String) postParam.get("id"), null, null, postParam,
                responseClass, completionHandler);
    }

    /**
     * Method to send the POST request to execute a url.
     *
     * @param postUrl   Post url which needs to be execute.
     * @param postParam PostParams which needs to be added in POST request.
     */
    public void sendPostRequest(String postUrl, Map<String, Object> postParam) {
        sendPostRequest(postUrl, postParam, null, null, null, null);
    }

    /**
     * Method to send the POST request to execute a url.
     *
     * @param postUrl           Post url which needs to be execute.
     * @param postParam         PostParams which needs to be added in POST request.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void sendPostRequest(String postUrl, Map<String, Object> postParam,
                                Class responseClass, CompletionHandler completionHandler) {
        sendPostRequest(postUrl, postParam, null, null, responseClass, completionHandler);
    }

    /**
     * Method to send the POST request.
     *
     * @param postUrl           Post url which needs to be execute.
     * @param postParam         PostParams which needs to be added in post request.
     * @param imagePath         Path of the Image which needs to be send with POST request.
     * @param imageKey          Key of the image file (File needs to be send in key-value pair).
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void sendPostRequest(String postUrl, Map<String, Object> postParam, String imagePath,
                                String imageKey, Class responseClass, CompletionHandler completionHandler) {
        this.postUrl = postUrl;
        requestType = ApiConstants.POST_REQUEST;
        performAction(null, imagePath, imageKey, postParam, responseClass, completionHandler);
    }

    /**
     * Method to send the PUT request to execute a url.
     *
     * @param postUrl   Put url which needs to be execute.
     * @param postParam PostParams which needs to be added in PUT request.
     */
    public void sendPutRequest(String postUrl, Map<String, Object> postParam) {
        sendPutRequest(postUrl, postParam, null, null);
    }

    /**
     * Method to send the PUT request to execute a url.
     *
     * @param postUrl           Put url which needs to be execute.
     * @param postParam         PostParams which needs to be added in PUT request.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void sendPutRequest(String postUrl, Map<String, Object> postParam,
                               Class responseClass, CompletionHandler completionHandler) {
        this.postUrl = postUrl;
        requestType = ApiConstants.PUT_REQUEST;
        performAction(null, null, null, postParam, responseClass, completionHandler);
    }

    /**
     * Method to make the DELETE request.
     *
     * @param postUrl   Delete url which needs to be execute.
     * @param postParam PostParams which needs to be added in DELETE request.
     */
    public void sendDeleteRequest(String postUrl, Map<String, Object> postParam) {
        sendDeleteRequest(postUrl, postParam, null, null);
    }

    /**
     * Method to make the DELETE request.
     *
     * @param postUrl           Delete url which needs to be execute.
     * @param postParam         PostParams which needs to be added in DELETE request.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void sendDeleteRequest(String postUrl, Map<String, Object> postParam,
                                  Class responseClass, CompletionHandler completionHandler) {
        this.postUrl = postUrl;
        requestType = ApiConstants.DELETE_REQUEST;
        performAction(null, null, null, postParam, responseClass, completionHandler);
    }

    /**
     * Method to make the GET request.
     *
     * @param url               GET url which needs to be execute.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getResponse(String url, Class responseClass, CompletionHandler completionHandler) {
        getResponse(url, null, null, responseClass, completionHandler);
    }

    /**
     * Method to make the GET request.
     *
     * @param url               GET url which needs to be execute.
     * @param queryParam        Map of query string which need to be merge into url.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getResponse(String url, Map<String, String> queryParam, Class responseClass, CompletionHandler completionHandler) {
        getResponse(url, null, queryParam, responseClass, completionHandler);
    }

    /**
     * Method to make the GET request.
     *
     * @param url               GET url which needs to be execute.
     * @param tag               Request tag, which needs to be associated with the network call.
     * @param queryParam        Map of query string which need to be merge into url.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void getResponse(String url, String tag, Map<String, String> queryParam, Class responseClass, CompletionHandler completionHandler) {
        try {
            if (url != null && !url.isEmpty()) {
                HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
                if (queryParam != null && !queryParam.isEmpty()) {
                    for (Map.Entry<String, String> param : queryParam.entrySet()) {
                        httpBuilder.addQueryParameter(param.getKey(), param.getValue());
                    }
                }
                requestBuilder.url(httpBuilder.build());
                requestBuilder.get();
                if (tag != null && !tag.isEmpty()) {
                    requestBuilder.tag(tag);
                }
                Logcat.d(ChannelizeOkHttpUtil.class, "Request Url: " + httpBuilder.build());
                makeRequestToServer(null, responseClass, completionHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to perform action on post/put/delete request.
     *
     * @param tag               Request tag, which needs to be associated with the network call.
     * @param filePath          Path of the file which needs to be send with POST request.
     * @param fileKeyName       Key of image file (File needs to be send in key-value pair).
     * @param postParam         PostParams which needs to be added in request.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    private void performAction(String tag, String filePath, String fileKeyName,
                               Map<String, Object> postParam, Class responseClass, CompletionHandler completionHandler) {
        MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        multipartBody.setType(MultipartBody.FORM);

        RequestBody jsonRequestBody = null;
        if (postParam != null && !postParam.isEmpty()) {
            Set<String> keySet = postParam.keySet();
            for (String key : keySet) {
                Object objectValue = postParam.get(key);
                if (objectValue instanceof String) {
                    String value = (String) postParam.get(key);
                    multipartBody.addFormDataPart(key, value);
                }
            }
            MediaType JSON = MediaType.parse("application/json");
            JSONObject parameter = new JSONObject(postParam);

            Logcat.d(ChannelizeOkHttpUtil.class, "PostParams: " + postParam);
            Logcat.d(ChannelizeOkHttpUtil.class, "JSON Parameter: " + parameter);
            jsonRequestBody = RequestBody.create(JSON, parameter.toString());
        }

        // If the file exist then adding it into multipart.
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            String mimeType = CoreFunctionsUtil.getMimeType(filePath);
            Logcat.d(ChannelizeOkHttpUtil.class, "AttachmentPath: " + filePath);
            Logcat.d(ChannelizeOkHttpUtil.class, "mimeType: " + mimeType);

            // Sending audio file duration in param when the attachment is of music type.
            if (mimeType != null && mimeType.contains("audio/")) {
                try {
                    Uri uri = Uri.parse(filePath);
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(mContext, uri);
                    String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    multipartBody.addFormDataPart("duration", String.valueOf(Long.parseLong(durationStr)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            multipartBody.addFormDataPart(fileKeyName, file.getName(),
                    RequestBody.create(MediaType.parse(mimeType), file));
            jsonRequestBody = null;
        }

        RequestBody requestBody;
        if (jsonRequestBody != null) {
            requestBody = jsonRequestBody;
        } else {
            try {
                requestBody = multipartBody.build();
            } catch (IllegalStateException e) {
                requestBody = RequestBody.create(null, new byte[]{});
            }
        }

        Logcat.d(ChannelizeOkHttpUtil.class, "Action Url: " + postUrl);
        requestBuilder.url(postUrl);

        switch (requestType) {
            case ApiConstants.POST_REQUEST:
                requestBuilder.post(requestBody);
                break;

            case ApiConstants.PUT_REQUEST:
                requestBuilder.put(requestBody);
                break;

            case ApiConstants.DELETE_REQUEST:
                requestBuilder.delete(requestBody);
                break;
        }

        if (tag != null && !tag.isEmpty()) {
            requestBuilder.tag(tag);
        }

        makeRequestToServer(postParam, responseClass, completionHandler);
    }

    /**
     * Method to make request call to the server.
     *
     * @param postParam         PostParams which needs to be added in request.
     * @param responseClass     Class reference which contains the response.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    private void makeRequestToServer(final Map<String, Object> postParam,
                                     final Class responseClass, final CompletionHandler completionHandler) {
        newCall(requestBuilder.build()).enqueue(new Callback() {

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (completionHandler != null && !call.isCanceled()) {
                    updateError(CoreFunctionsUtil.isNetworkAvailable(mContext) ? 0 : 500,
                            e.getMessage(), postParam, completionHandler);
                }
                Logcat.d(ChannelizeOkHttpUtil.class, "onFailure, exception: " + e + ", " + call.request().toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (completionHandler != null && response.body() != null) {
                        String responseBody = response.body().string();
                        Logcat.d(ChannelizeOkHttpUtil.class, "Response body: " + responseBody);
                        if (response.isSuccessful()) {
                            JSONObject responseObject = getResponseObject(responseBody, responseClass);
                            if (responseObject != null) {
                                if (responseClass != null) {
                                    completionHandler.onComplete(readJsonResponse(responseObject.toString(), responseClass), null);
                                } else {
                                    completionHandler.onComplete(responseObject, null);
                                }
                            } else {
                                completionHandler.onComplete(readJsonResponse(getResponseObject(responseClass).toString(), responseClass), null);
                            }
                        } else {
                            completionHandler.onComplete(null, new ChannelizeError(response.code(), responseBody, postParam));
                        }
                        handleResponseBodyStream(response);
                    } else if (completionHandler != null) {
//                        Logcat.d(ChannelizeOkHttpUtil.class, "Response body: " + response.body().string());
                        updateError(response.code(), null, postParam, completionHandler);
                    } else if (response.body() != null) {
                        String responseBody = response.body().string();
                        Logcat.d(ChannelizeOkHttpUtil.class, "Response body: " + responseBody);
                    }
                    Logcat.d(ChannelizeOkHttpUtil.class, "onResponse: " + response);
                } catch (Exception e) {
                    updateError(response.code(), null, postParam, completionHandler);
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method to update the error handler when any occurs.
     *
     * @param code              Error code for error.
     * @param message           Message regarding the error.
     * @param postParam         PostParams which needs to be added in request.
     * @param completionHandler CompletionHandler instance to notify the completion.
     */
    private void updateError(int code, String message, Map<String, Object> postParam, CompletionHandler completionHandler) {
        if (message == null || message.isEmpty()) {
            message = mContext.getResources().getString(R.string.pm_something_went_wrong);
        }
        if (completionHandler != null) {
            completionHandler.onComplete(null, new ChannelizeError(code, null, message, null, postParam));
        }
    }

    /**
     * Methop to get the generic response from the response body.
     *
     * @param responseBody  ResponseBody which contains the response.
     * @param responseClass Class in which data needs to be parsed.
     * @return Returns the model.
     * @throws IOException
     */
    private <T extends GenericResponse> T readJsonResponse(String responseBody, @NonNull Class<T> responseClass) throws IOException {
        return GSON_INSTANCE.fromJson(responseBody, responseClass);
    }

    /**
     * Method to close stream when request is not successful.
     *
     * @param response Response instance.
     */
    private void handleResponseBodyStream(Response response) {
        if (response != null && response.body() != null) {
            response.body().close();
        }
    }

    /**
     * Method to get the JSONObject from the received response of OKHttp request.
     *
     * @param response      Response string which is received in successful operation.
     * @param responseClass Class reference which contains the response.
     * @return Returns the JSONObject of received response.
     */
    private JSONObject getResponseObject(String response, Class responseClass) {
        try {
            if (response != null && !response.isEmpty()) {
                Object object = new JSONTokener(response).nextValue();
                if (responseClass != null && responseClass.getName().contains(RequestResponse.class.getName())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("isSuccessful", true);
                    return jsonObject;
                } else if (object instanceof JSONObject) {
                    return new JSONObject(response);
                } else if (object instanceof JSONArray) {
                    return convertToJsonObject(new JSONArray(response));
                } else {
                    return getResponseObject(responseClass);
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to get the JSONObject of OKHttp request.
     *
     * @param responseClass Class reference which contains the response.
     * @return Returns the JSONObject of received response.
     */
    private JSONObject getResponseObject(Class responseClass) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (responseClass != null
                    && responseClass.getName().contains(RequestResponse.class.getName())) {
                jsonObject.put("isSuccessful", true);
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Method to convert jsonArray in JsonObject
     *
     * @param jsonArray JsonArray to convert in JsonObject
     * @return Converted JsonObject
     */
    private JSONObject convertToJsonObject(JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        if (jsonArray != null) {
            try {
                jsonObject.put("response", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        } else {
            return null;
        }
    }

    /**
     * Method to upload the file on Aws S3 server.
     *
     * @param requestUrl        Url on which file needs to be uploaded.
     * @param tag               Request tag, which needs to be associated with the network call.
     * @param filePath          Local file path of the file that need to be uploaded.
     * @param completionHandler CompletionHandler instance which will return the response.
     */
    public void uploadFileAwsS3(String requestUrl, String tag, String filePath, CompletionHandler<RequestResponse> completionHandler) {
        // Getting file info.
        File file = new File(filePath);
        String mimeType = CoreFunctionsUtil.getMimeType(filePath);
        Logcat.d(ChannelizeOkHttpUtil.class, "filePath: " + filePath + ", mimeType: " + mimeType);

        // Setting-up the request body with file.
        MediaType mediaType = MediaType.parse(mimeType);
        RequestBody requestBody = RequestBody.create(mediaType, file);

        // Creating Request Builder.
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(requestUrl);
        requestBuilder.put(requestBody);
        if (tag != null && !tag.isEmpty()) {
            requestBuilder.tag(tag);
        }

        newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                updateError(CoreFunctionsUtil.isNetworkAvailable(mContext) ? 0 : 500,
                        e.getMessage(), null, completionHandler);
                Logcat.d(ChannelizeOkHttpUtil.class, "uploadFileAwsS3, onFailure, exception: " + e + ", " + call.request().toString());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (completionHandler != null && response.body() != null) {
                        String responseBody = response.body().string();
//                        Logcat.d(ChannelizeOkHttpUtil.class, "UploadFileAwsS3 Response body: " + responseBody);

                        if (response.isSuccessful()) {
                            completionHandler.onComplete(readJsonResponse(getResponseObject(RequestResponse.class).toString(),
                                    RequestResponse.class), null);
                        } else {
                            completionHandler.onComplete(null, new ChannelizeError(response.code(), responseBody, null));
                        }
                        handleResponseBodyStream(response);
                    } else if (completionHandler != null) {
                        updateError(response.code(), null, null, completionHandler);
                    } else if (response.body() != null) {
                        String responseBody = response.body().string();
                        Logcat.d(ChannelizeOkHttpUtil.class, "UploadFileAwsS3 Response body: " + responseBody);
                    }
                    Logcat.d(ChannelizeOkHttpUtil.class, "UploadFileAwsS3, onResponse: " + response);
                } catch (Exception e) {
                    updateError(response.code(), null, null, completionHandler);
                    e.printStackTrace();
                }
            }
        });

    }
}
