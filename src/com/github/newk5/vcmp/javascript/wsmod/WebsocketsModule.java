
package com.github.newk5.vcmp.javascript.wsmod;

import com.eclipsesource.v8.V8;
import com.github.newk5.vcmp.javascript.plugin.module.Module;
import com.github.newk5.vcmp.javascript.wsmod.client.WSocketClient;
import com.github.newk5.vcmp.javascript.wsmod.injectables.WebsocketServerWrapper;
import com.github.newk5.vcmp.javascript.wsmod.server.WSServer;
import io.alicorn.v8.V8JavaAdapter;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;


public class WebsocketsModule extends Plugin {

    public static ThreadPoolExecutor pool;
    private static V8 v8 = com.github.newk5.vcmp.javascript.plugin.internals.Runtime.v8;

    public WebsocketsModule(PluginWrapper wrapper) {
        super(wrapper);
        this.pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    @Extension
    public static class Websockets implements Module {

        @Override
        public void inject() {
            V8JavaAdapter.injectClass("_WebSocketConnection_", WebSocket.class, v8);
            V8JavaAdapter.injectClass("_WebSocketClientHandshake_", ClientHandshake.class, v8);

            V8JavaAdapter.injectClass("_WSServer_", WSServer.class, v8);
            V8JavaAdapter.injectClass("_WSocketClient_", WSocketClient.class, v8);
            V8JavaAdapter.injectObject("_WebsocketServerWrapper_", new WebsocketServerWrapper(), v8);

        }

        @Override
        public String javascript() {
            InputStream in = WebsocketsModule.class.getResourceAsStream("module.js");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String code =   reader.lines().collect(Collectors.joining("\n"));
            
            return code;
        }


    }
}
