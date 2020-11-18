/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.response.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateDeserializer implements JsonDeserializer<Date> {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final DateFormat dateFormatStories = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    public DateDeserializer() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        dateFormatStories.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Date date = null;
        try {
            date = dateFormat.parse(json.getAsJsonPrimitive().getAsString());
        } catch (Exception e) {
            try {
                date = dateFormatStories.parse(json.getAsJsonPrimitive().getAsString());
            } catch (Exception e2) {
            }
        }
        return date;
    }
}
