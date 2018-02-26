package com.dreamer.service.mobile;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;

public class MessageHandler extends TextWebSocketHandler {

    //存储所有的session
    private final static Map<Long,WebSocketSession> SESSION_MAP;

    static {
        SESSION_MAP = new HashedMap();
    }

    //建立连接后
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

//        super.afterConnectionEstablished(session);
    }
}
