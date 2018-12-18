package com.github.newk5.vcmp.javascript.wsmod.client;

import com.github.newk5.vcmp.javascript.plugin.internals.EventLoop;
import com.github.newk5.vcmp.javascript.plugin.internals.result.AsyncResult;
import com.github.newk5.vcmp.javascript.plugin.internals.result.CommonResult;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public class WSocketClient extends WebSocketClient {

    public static Map<String, WSClientMetadata> clients = new HashMap<>();
    private static EventLoop eventLoop = com.github.newk5.vcmp.javascript.plugin.internals.Runtime.eventLoop;

    public WSocketClient(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WSocketClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        WSClientMetadata s = clients.get(super.uri.toString());
        if (s != null) {

            if (s.getOnOpen() != null) {
                AsyncResult res = new CommonResult(s.getOnOpen(), new Object[]{"Successfully opened connection to: " + super.uri.toString(), handshakedata});
                res.setMaintainCallback(true);
                eventLoop.queue.add(res);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        WSClientMetadata s = clients.get(super.uri.toString());
        if (s != null) {

            if (s.getOnClose() != null) {
                AsyncResult res = new CommonResult(s.getOnClose(), new Object[]{code, reason, remote});
                res.setMaintainCallback(true);
                eventLoop.queue.add(res);
            }
        }
    }

    @Override
    public void onMessage(String message) {
        WSClientMetadata s = clients.get(super.uri.toString());

        if (s.getOnMessage() != null) {
            AsyncResult res = new CommonResult(s.getOnMessage(), new Object[]{message});
            res.setMaintainCallback(true);
            eventLoop.queue.add(res);
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        WSClientMetadata s = clients.get(super.uri.toString());
        if (s != null) {

            if (s.getOnError() != null) {
                AsyncResult res = new CommonResult(s.getOnError(), new Object[]{ex.getMessage()});
                res.setMaintainCallback(true);
                eventLoop.queue.add(res);
            }
        }
    }

}
