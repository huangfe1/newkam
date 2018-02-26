package com.dreamer.service.socket;

import com.dreamer.domain.socket.Message;
import com.dreamer.domain.user.User;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MessageHandler extends TextWebSocketHandler {

//    @Autowired
//    private RedisDao redisDao;

    private final static Map<Integer, WebSocketSession> SESSION_MAP;

    static {
        SESSION_MAP = new ConcurrentHashMap<>();
    }


    //获取当前sessionMap
    private Map<Integer, WebSocketSession> getSessionMap() {
//        Map<Integer, WebSocketSession> SESSION_MAP = redisDao.getObject("SESSION_MAP", Map.class);
//        if (SESSION_MAP == null) {
//            SESSION_MAP = new HashedMap();
//            redisDao.putObject("SESSION_MAP", SESSION_MAP);
//        }
        return SESSION_MAP;
    }

    //获取当前session
    private WebSocketSession currentSession(Integer userId) {
        System.out.println(getSessionMap().size()+"当前连接人数");
        if (getSessionMap().containsKey(userId)) {//在线
            WebSocketSession socketSession = getSessionMap().get(userId);
            if (socketSession.isOpen()) {//在线
                return socketSession;
            } else {//不在线
                System.out.println(userId+"接收方在线但是没打开！");//TODO 不在线本地化存储
            }
        } else {
            System.out.println(userId+"接收方不在线！");//TODO 不在线本地化存储
        }
        return null;
    }

    //保存当前session
    private void saveSession(Integer userId, WebSocketSession socketSession) {
        getSessionMap().put(userId, socketSession);
    }

    private User getUser(WebSocketSession session){
        User user = (User) session.getAttributes().get("user");
        return user;
    }



    //建立连接后
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        User user = getUser(session);
        System.out.println(user.getId() + "当前登录的用户");
        Integer userId = user.getId();
        saveSession(userId, session);//存储当前
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //发送消息
        String text = message.getPayload();
        System.out.println("收到的消息是" + text);
        try {
            JSONObject json = JSONObject.fromObject(text);
            Message message1 = (Message) JSONObject.toBean(json,Message.class);
            Integer toId = message1.getToId();
            System.out.println("发送方是" + message1.getFromId());
            System.out.println("接收方是" + message1.getToId());
            System.out.println("消息是" + message1.getMsg());
            WebSocketSession toSession = currentSession(toId);
            if (toSession != null) {
                toSession.sendMessage(message);
            } else {
                //TODO
            }
//            //自动回复
//            Integer fid = message1.getFromId();
//            Integer tid = message1.getToId();
//            message1.setToId(fid);
//            message1.setFromId(tid);
//            session.sendMessage(new TextMessage(JSONObject.fromObject(message1).toString()));
//            System.out.println("---");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String s = "{\"fromId\":\"60115\",\"toId\":\"118013\",\"avatar\":\"http://wx.qlogo.cn/mmopen/9rFLdoky60dReLAoa4kQ8wCWibmmsgGqKkoJ5BhkQRpzQUV3Eo10VM6lsIP6ZZZTnsrHJeEViavRV7xdL2IhpYKXnUIMJFxA7J/0\",\"msg\":\"安师大\"}";
        JSONObject jsonObject = JSONObject.fromObject(s);
        System.out.println(jsonObject.toString());
        Message message = (Message) JSONObject.toBean(jsonObject,Message.class);
        System.out.println(message.getFromId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        User user = getUser(session);
        //移除当前
        getSessionMap().remove(user.getId());
        System.out.println("掉线移除！！"+user.getId());
    }

}
