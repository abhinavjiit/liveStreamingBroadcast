/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response.json;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class IntDeserializer implements JsonDeserializer<Integer> {
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        final JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
        if (jsonPrimitive.isString()) {
            final String numberText = json.getAsString();
            if (TextUtils.isEmpty(numberText)) {
                return 0;
            }
            return Integer.parseInt(numberText);
        } else if (jsonPrimitive.isNumber()) {
            return json.getAsInt();
        }
        return 0;
    }
}
