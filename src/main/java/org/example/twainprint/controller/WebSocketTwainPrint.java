package org.example.twainprint.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/mysocket")
@Component
@Slf4j
public class WebSocketTwainPrint {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static final CopyOnWriteArraySet<WebSocketTwainPrint> WEB_SOCKET_SET = new CopyOnWriteArraySet<WebSocketTwainPrint>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据。
     */
    private Session session;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        this.session = session;
        // 将当前连接加入到set中
        WEB_SOCKET_SET.add(this);
        // 连接数+1
        addOnlineCount();
       // byte[] imageBytes = Files.readAllBytes(Paths.get("C:\\Users\\pings\\mmsc\\tmp\\mms0.pdf"));
        sendMessage("12");
        File file = new File("D:\\1.pdf");



        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[102410];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, bytesRead);
                this.session.getBasicRemote().sendBinary(byteBuffer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("MyWebSocketServer 加入新的连接，当前连接数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        // 将当前连接从set中移除
        WEB_SOCKET_SET.remove(this);
        // 连接数-1
        subOnlineCount();
        log.info("MyWebSocketServer 有一连接关闭！当前连接数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("MyWebSocketServer 收到来自窗口" + session.getId() + "的信息:" + message);
    }

    /**
     * @param session 连接session
     * @param error   措施信息
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("MyWebSocketServer 连接发生错误");
        error.printStackTrace();
    }


    /**
     * <p>@Description:給session连接推送消息</p>
     * <p>@param [message]</p>
     * <p>@return void</p>
     * <p>@throws </p>
     */
    private void sendMessage(Object message) throws IOException, EncodeException {
        this.session.getBasicRemote().sendText((String) message);
    }

    /**
     * <p>@Description:向所有连接群发消息</p>
     * <p>@param [message]</p>
     * <p>@return void</p>
     * <p>@throws </p>
     */
    public static void sendMessageToAll(Object message){
        for (WebSocketTwainPrint item : WEB_SOCKET_SET) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                log.error("MyWebSocketServer 向客户端推送数据发生错误");
            } catch (EncodeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketTwainPrint.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketTwainPrint.onlineCount--;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketTwainPrint that = (WebSocketTwainPrint) o;
        return Objects.equals(session, that.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(session);
    }
}



