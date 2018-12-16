
package com.github.newk5.vcmp.javascript.wsmod.server;

import com.github.newk5.vcmp.javascript.plugin.internals.EventLoop;
import com.github.newk5.vcmp.javascript.plugin.internals.result.AsyncResult;
import com.github.newk5.vcmp.javascript.plugin.internals.result.CommonResult;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WSServer extends WebSocketServer {

    public static Map<String, WSServerMetadata> servers = new HashMap<>();
    private static EventLoop eventLoop = com.github.newk5.vcmp.javascript.plugin.internals.Runtime.eventLoop;

    public WSServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        WSServerMetadata s = servers.get(super.getAddress().getHostString() + super.getPort());

        if (s.getOnOpen() != null) {
            AsyncResult res = new CommonResult(s.getOnOpen(), new Object[]{conn, handshake});
            eventLoop.queue.add(res);
        }

    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        WSServerMetadata s = servers.get(super.getAddress().getHostString() + super.getPort());

        if (s.getOnClose() != null) {
            AsyncResult res = new CommonResult(s.getOnClose(), new Object[]{conn, code, reason, remote});
            eventLoop.queue.add(res);
        }

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        WSServerMetadata s = servers.get(super.getAddress().getHostString() + super.getPort());
        if (s.getOnMessage() != null) {
            AsyncResult res = new CommonResult(s.getOnMessage(), new Object[]{conn, message});
            eventLoop.queue.add(res);
        }

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {

        System.out.println("received ByteBuffer from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        WSServerMetadata s = servers.get(super.getAddress().getHostString() + super.getPort());
        if (s.getOnError() != null) {
            AsyncResult res = new CommonResult(s.getOnError(), new Object[]{conn, ex.getMessage()});
            eventLoop.queue.add(res);
        }
    }
    @Override
    public void onStart() {
        WSServerMetadata s = servers.get(super.getAddress().getHostString() + super.getPort());
        if (s.getOnStart() != null) {
            AsyncResult res = new CommonResult(s.getOnStart(), new Object[]{"Websocket server successfully started at: ws://" + super.getAddress().getHostString() + ":" + super.getPort()});
            eventLoop.queue.add(res);
        }
    }

}
