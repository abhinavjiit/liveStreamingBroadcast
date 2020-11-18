/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk;


import android.content.Context;


/**
 * Configurable Channelize options.
 */
public class ChannelizeConfig {

    // Must required variables to use channelize.
    final Context applicationContext;
    final String apiKey;
    final boolean isLoggingEnabled;


    public ChannelizeConfig(Context applicationContext, String apiKey,
                            boolean isLoggingEnabled) {
        this.applicationContext = applicationContext;
        this.apiKey = apiKey;
        this.isLoggingEnabled = isLoggingEnabled;
    }

    /**
     * Builder for creating {@link ChannelizeConfig} instances.
     */
    public static class Builder {
        private Context applicationContext;
        private volatile String apiKey;
        private volatile boolean isLoggingEnabled = false;

        /**
         * Start building a new {@link ChannelizeConfig} instance.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }

            this.applicationContext = context.getApplicationContext();
        }

        public Builder setAPIKey(String apiKey) {
            if (apiKey == null) {
                throw new IllegalArgumentException("API Key must not be null.");
            }
            this.apiKey = apiKey;
            return this;
        }

        /**
         * Enable debug mode
         */
        public Builder setLoggingEnabled(boolean isLoggingEnabled) {
            this.isLoggingEnabled = isLoggingEnabled;
            return this;
        }

        /**
         * Build the {@link ChannelizeConfig} instance
         */
        public ChannelizeConfig build() {
            return new ChannelizeConfig(applicationContext, apiKey, isLoggingEnabled);
        }
    }
}
