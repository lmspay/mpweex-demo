/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.lmspay.mpweex.android.mpweex;

import android.support.annotation.Nullable;

import com.taobao.weex.appfram.websocket.IWebSocketAdapter;
import com.taobao.weex.appfram.websocket.WebSocketCloseCodes;
import com.taobao.weex.utils.WXLogUtils;

import java.io.EOFException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by moxun on 16/12/27.
 */

public class DefaultWebSocketAdapter implements IWebSocketAdapter {

    private WebSocket ws;
    private EventListener eventListener;
//    private WSEventReporter wsEventReporter;

    @Override
    public void connect(String url, @Nullable final String protocol, EventListener listener) {
        WXLogUtils.d("weex", "websocket connect");

        this.eventListener = listener;
//        this.wsEventReporter = WSEventReporter.newInstance();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(5, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(5, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        Request.Builder builder = new Request.Builder();

        if (protocol != null) {
            builder.addHeader(HEADER_SEC_WEBSOCKET_PROTOCOL, protocol);
        }

        builder.url(url);
//        wsEventReporter.created(url);

        Request wsRequest = builder.build();
        okHttpClient.newWebSocket(wsRequest, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                ws = webSocket;
                eventListener.onOpen();
//                Headers wsHeaders = response.headers();
//                Map<String, String> headers = new HashMap<>();
//                for (String name : wsHeaders.names()) {
//                    headers.put(name, wsHeaders.values(name).toString());
//                }
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                eventListener.onMessage(text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                eventListener.onClose(code, reason, true);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                if (t instanceof EOFException) {
                    eventListener.onClose(WebSocketCloseCodes.CLOSE_NORMAL.getCode(),
                            WebSocketCloseCodes.CLOSE_NORMAL.name(), true);
//                    wsEventReporter.closed();
                } else {
                    eventListener.onError(t.getMessage());
//                    wsEventReporter.frameError(e.getMessage());
                }
            }
        });
    }

    @Override
    public void send(String data) {
        if (ws != null) {
            try {
                ws.send(data);

//                wsEventReporter.frameSent(data);
            } catch (Exception e) {
                reportError(e.getMessage());
//                wsEventReporter.frameError(e.getMessage());
            }
        } else {
            reportError("WebSocket is not ready");
        }
    }

    @Override
    public void close(int code, String reason) {
        if (ws != null) {
            try {
                ws.close(code, reason);
            } catch (Exception e) {
                reportError(e.getMessage());
            }
        }
    }

    @Override
    public void destroy() {
        if (ws != null) {
            try {
                ws.close(WebSocketCloseCodes.CLOSE_GOING_AWAY.getCode(), WebSocketCloseCodes.CLOSE_GOING_AWAY.name());
            } catch (Exception e) {
            }
        }
    }

    private void reportError(String message) {
        if (eventListener != null) {
            eventListener.onError(message);
        }
    }
}
