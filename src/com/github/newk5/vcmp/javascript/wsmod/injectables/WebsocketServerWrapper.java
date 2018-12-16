/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.newk5.vcmp.javascript.wsmod.injectables;

import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.github.newk5.vcmp.javascript.wsmod.server.WSServer;
import java.net.InetSocketAddress;
import com.github.newk5.vcmp.javascript.plugin.internals.Runtime;
import com.github.newk5.vcmp.javascript.plugin.output.Console;
import com.github.newk5.vcmp.javascript.wsmod.WebsocketsModule;
import com.github.newk5.vcmp.javascript.wsmod.client.WSClientMetadata;
import com.github.newk5.vcmp.javascript.wsmod.client.WSocketClient;
import static com.github.newk5.vcmp.javascript.wsmod.client.WSocketClient.clients;
import static com.github.newk5.vcmp.javascript.wsmod.server.WSServer.servers;
import com.github.newk5.vcmp.javascript.wsmod.server.WSServerMetadata;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author Carlos
 */
public class WebsocketServerWrapper {

    private ThreadPoolExecutor pool = WebsocketsModule.pool;
    private Console console = Runtime.console;

    public WSServer startServer(String host, int port, V8Object options) {
        WSServer wsServer = new WSServer(new InetSocketAddress(host, port));

        WSServerMetadata sm = new WSServerMetadata(wsServer);
        V8Object events = options.getObject("events");
        
        if (!events.isUndefined()) {
            sm.setOnClose(events.getObject("onClose").isUndefined() ? null : (V8Function) events.get("onClose"));
            sm.setOnOpen(events.getObject("onOpen").isUndefined() ? null : (V8Function) events.get("onOpen"));
            sm.setOnMessage(events.getObject("onMessage").isUndefined() ? null : (V8Function) events.get("onMessage"));
            sm.setOnError(events.getObject("onError").isUndefined() ? null : (V8Function) events.get("onError"));
            sm.setOnStart(events.getObject("onStart").isUndefined() ? null : (V8Function) events.get("onStart"));
        }
        pool.submit(() -> {

            servers.put(host + port, sm);
            sm.getServer().run();
        });

        return wsServer;
    }

    public WSocketClient startClient(String url, V8Object options) throws URISyntaxException {
        WSocketClient client = new WSocketClient(new URI(url)); 
        V8Object events = options.getObject("events");
        WSClientMetadata cm = new WSClientMetadata(client);
        if (!events.isUndefined()) {
            cm.setOnClose(events.getObject("onClose").isUndefined() ? null : (V8Function) events.get("onClose"));
            cm.setOnOpen(events.getObject("onOpen").isUndefined() ? null : (V8Function) events.get("onOpen"));
            cm.setOnMessage(events.getObject("onMessage").isUndefined() ? null : (V8Function) events.get("onMessage"));
            cm.setOnError(events.getObject("onError").isUndefined() ? null : (V8Function) events.get("onError"));
        }

        pool.submit(() -> {

            clients.put(url, cm);
            cm.getClient().connect();
        });

        return client;
    }

}
