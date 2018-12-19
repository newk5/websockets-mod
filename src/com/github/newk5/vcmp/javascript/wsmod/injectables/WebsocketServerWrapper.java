package com.github.newk5.vcmp.javascript.wsmod.injectables;

import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.github.newk5.vcmp.javascript.plugin.internals.Runtime;
import com.github.newk5.vcmp.javascript.plugin.output.Console;
import com.github.newk5.vcmp.javascript.wsmod.WebsocketsModule;
import com.github.newk5.vcmp.javascript.wsmod.client.WSClientMetadata;
import com.github.newk5.vcmp.javascript.wsmod.client.WSocketClient;
import static com.github.newk5.vcmp.javascript.wsmod.client.WSocketClient.clients;
import com.github.newk5.vcmp.javascript.wsmod.server.SocketServer;
import static com.github.newk5.vcmp.javascript.wsmod.server.SocketServer.servers;
import com.github.newk5.vcmp.javascript.wsmod.server.WSServerMetadata;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.webbitserver.WebServer;
import org.webbitserver.WebServers;
import org.webbitserver.WebSocketConnection;

public class WebsocketServerWrapper {

    private ThreadPoolExecutor pool = WebsocketsModule.pool;
    private Console console = Runtime.console;
    WebServer webServer;

    public void addEndpoint(String endpoint, V8Object options) throws URISyntaxException {
        String uri = endpoint.startsWith("/") ? endpoint : "/" + endpoint;

        WSServerMetadata sm = new WSServerMetadata(webServer);
        sm.setEndpoint(endpoint);
        V8Object events = options.getObject("events");

        if (!events.isUndefined()) {
            sm.setOnClose(events.getObject("onClose").isUndefined() ? null : (V8Function) events.get("onClose"));
            sm.setOnOpen(events.getObject("onOpen").isUndefined() ? null : (V8Function) events.get("onOpen"));
            sm.setOnMessage(events.getObject("onMessage").isUndefined() ? null : (V8Function) events.get("onMessage"));
            sm.setOnError(events.getObject("onError").isUndefined() ? null : (V8Function) events.get("onError"));
            sm.setOnStart(events.getObject("onStart").isUndefined() ? null : (V8Function) events.get("onStart"));
        }
        servers.put(uri, sm);
        webServer.add(uri, new SocketServer());

        WSocketClient client = new WSocketClient(new URI("ws://localhost:" + webServer.getPort() + uri + "?event=start"));

        pool.submit(() -> {

            try {

                client.connectBlocking();
                client.send(new byte[0]);
            } catch (Exception ex) {
                Logger.getLogger(WebsocketServerWrapper.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

    }

    public void broadcast(String endpoint, String data) {

        Iterator<Entry<String, WebSocketConnection>> it = SocketServer.connections.entrySet().iterator();

        pool.submit(() -> {
            while (it.hasNext()) {
                Entry<String, WebSocketConnection> e = it.next();
                if (e.getValue().httpRequest().uri().equals(endpoint)) {
                    try {
                        e.getValue().send(data);
                    } catch (Exception ex) {
                        System.out.println(ex.toString());
                        it.remove();
                    }
                }
            }
        });

    }

    public void startServer(String endpoint, int port, V8Object options) throws URISyntaxException {
        if (webServer == null) {
            String uri = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
            webServer = WebServers.createWebServer(port).add(uri, new SocketServer());

            WSServerMetadata sm = new WSServerMetadata(webServer);
            sm.setEndpoint(endpoint);

            V8Object events = options.getObject("events");

            if (!events.isUndefined()) {
                sm.setOnClose(events.getObject("onClose").isUndefined() ? null : (V8Function) events.get("onClose"));
                sm.setOnOpen(events.getObject("onOpen").isUndefined() ? null : (V8Function) events.get("onOpen"));
                sm.setOnMessage(events.getObject("onMessage").isUndefined() ? null : (V8Function) events.get("onMessage"));
                sm.setOnError(events.getObject("onError").isUndefined() ? null : (V8Function) events.get("onError"));
                sm.setOnStart(events.getObject("onStart").isUndefined() ? null : (V8Function) events.get("onStart"));
            }

            servers.put(uri, sm);

            WSocketClient client = new WSocketClient(new URI("ws://localhost:" + port + uri + "?event=start"));

            pool.submit(() -> {

                try {
                    webServer.start().get();
                    client.connectBlocking();
                    client.send(new byte[0]);
                } catch (Exception ex) {
                    Logger.getLogger(WebsocketServerWrapper.class.getName()).log(Level.SEVERE, null, ex);
                }

            });
        } else {
            console.error("The server has already been started!");
        }

    }

    public WSocketClient startClient(String url, V8Object options) throws URISyntaxException {
        WSocketClient client = new WSocketClient(new URI(url));

        WSClientMetadata cm = new WSClientMetadata(client);

        if (options != null) {
            V8Object events = options.getObject("events");

            if (options.contains("timeout")) {
                client.setConnectionLostTimeout(options.getInteger("timeout"));
            }

            if (!events.isUndefined()) {
                cm.setOnClose(events.getObject("onClose").isUndefined() ? null : (V8Function) events.get("onClose"));
                cm.setOnOpen(events.getObject("onOpen").isUndefined() ? null : (V8Function) events.get("onOpen"));
                cm.setOnMessage(events.getObject("onMessage").isUndefined() ? null : (V8Function) events.get("onMessage"));
                cm.setOnError(events.getObject("onError").isUndefined() ? null : (V8Function) events.get("onError"));
            }
        }
        pool.submit(() -> {

            clients.put(url, cm);
            cm.getClient().connect();
        });

        return client;
    }

}
