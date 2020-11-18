/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.network.mqtt;


/**
 *  The {@link ChannelizeConnectionHandler} class contains all the real time events related to user/conversation/connection which can be arrived when app is active.
 *  But all the methods are optional and they need to be added according to the need,
 *  {@link ChannelizeConnectionHandler#onRealTimeDataUpdate can be override if you want to modify the real time data according to your need}
 */
public interface ChannelizeConnectionHandler {

    /**
     * Method gets invoked when the mqtt lost the connection.
     */
    default void onDisconnected() {

    }

    /**
     * Method gets invoked when the mqtt connection established.
     */
    default void onConnected() {

    }

    /**
     * This method is universal and gets invoked for every kind of real time updates.
     * If you want to modify the real time data then you can override this method.
     *
     * @param topic           Topic for which the real time updated has came.
     * @param responseMessage Complete response that has been arrived for real time update.
     */
    default void onRealTimeDataUpdate(String topic, String responseMessage) {

    }
}
