package com.dreamer.service.socket;

import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

public class MyHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
//
//    public static final String USER_ID = "USER.ID";
//
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
//        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
//        HttpServletRequest request1 = serverRequest.getServletRequest();
//        User user = (User)WebUtil.getCurrentUser(request1);
//
//        //        return super.beforeHandshake(request, response, wsHandler, attributes);
//    }
}
