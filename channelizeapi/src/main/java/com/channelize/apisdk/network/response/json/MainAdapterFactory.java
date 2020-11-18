/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response.json;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.channelize.apisdk.model.Conversation;
import com.channelize.apisdk.model.Message;
import com.channelize.apisdk.model.User;

import java.io.IOException;

public class MainAdapterFactory implements TypeAdapterFactory {
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegateAdapter.write(out, value);
            }

            @Override
            public T read(JsonReader in) throws IOException {
                T obj = delegateAdapter.read(in);
                if (obj instanceof Conversation) {
                    ((Conversation) obj).postProcessV3();
                } else if (obj instanceof Message) {
                    //((Message) obj).postProcess();
                } else if (obj instanceof User) {
                    //((User) obj).postProcess();
                }
                return obj;
            }
        };
    }
}
