package com.github.newk5.vcmp.javascript.wsmod.server;

import com.github.newk5.vcmp.javascript.plugin.internals.EventLoop;
import com.github.newk5.vcmp.javascript.plugin.internals.result.AsyncResult;
import com.github.newk5.vcmp.javascript.plugin.internals.result.CommonResult;
import java.util.HashMap;
import java.util.Map;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

public class SocketServer extends BaseWebSocketHandler {

    public static Map<String, WSServerMetadata> servers = new HashMap<>();
    private static EventLoop eventLoop = com.github.newk5.vcmp.javascript.plugin.internals.Runtime.eventLoop;
    private int connectionCount;

    @Override
    public void onPong(WebSocketConnection connection, byte[] msg) throws Throwable {

    }

    @Override
    public void onMessage(WebSocketConnection connection, byte[] msg) throws Throwable {
        if (msg.length == 0) {
            String uri = connection.httpRequest().uri();
            if (uri.endsWith("event=start")) {
                uri = uri.split("\\?")[0];
            }
            WSServerMetadata s = servers.get(uri);

            if (s.getOnStart() != null) {

                AsyncResult res = new CommonResult(s.getOnStart(), new Object[]{connection, "Websocket server endpoint: " + uri});
                res.setMaintainCallback(true);
                eventLoop.queue.add(res);
            }
        }
    }

    @Override
    public void onPing(WebSocketConnection connection, byte[] msg) throws Throwable {

    }

    @Override
    public void onOpen(WebSocketConnection connection) {
        String uri = connection.httpRequest().uri();
        int params = connection.httpRequest().queryParamKeys().size();
        if (params == 0) {
            WSServerMetadata s = servers.get(uri);
            if (s.getOnOpen() != null) {

                String addr = connection.httpRequest().remoteAddress().toString();
                AsyncResult res = new CommonResult(s.getOnOpen(), new Object[]{connection, uri, addr});
                res.setMaintainCallback(true);
                eventLoop.queue.add(res);
            }
        }

        connectionCount++;
    }

    @Override
    public void onClose(WebSocketConnection connection) {
        String uri = connection.httpRequest().uri();
        if (uri.endsWith("event=start")) {
            uri = uri.split("\\?")[0];
        }
        WSServerMetadata s = servers.get(connection.httpRequest().uri());

        if (s.getOnClose() != null) {
            String addr = connection.httpRequest().remoteAddress().toString();

            AsyncResult res = new CommonResult(s.getOnClose(), new Object[]{connection, uri, addr});
            res.setMaintainCallback(true);
            eventLoop.queue.add(res);
        }
        connectionCount--;
    }

    @Override
    public void onMessage(WebSocketConnection connection, String message) {
        String uri = connection.httpRequest().uri();
        WSServerMetadata s = servers.get(connection.httpRequest().uri());

        if (s.getOnMessage() != null) {
            String addr = connection.httpRequest().remoteAddress().toString();

            AsyncResult res = new CommonResult(s.getOnMessage(), new Object[]{connection, message, uri, addr});
            res.setMaintainCallback(true);
            eventLoop.queue.add(res);
        }
    }

}
