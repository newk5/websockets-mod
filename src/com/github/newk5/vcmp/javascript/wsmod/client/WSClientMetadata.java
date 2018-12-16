
package com.github.newk5.vcmp.javascript.wsmod.client;

import com.eclipsesource.v8.V8Function;

public class WSClientMetadata {

    private WSocketClient client;

    private V8Function onOpen;
    private V8Function onClose;
    private V8Function onMessage;
    private V8Function onError;

    public WSClientMetadata() {
    }

    public WSClientMetadata(WSocketClient client) {
        this.client = client;
    }

    public WSocketClient getClient() {
        return client;
    }

    public void setClients(WSocketClient server) {
        this.client = server;
    }

    /**
     * @return the onOpen
     */
    public V8Function getOnOpen() {
        return onOpen;
    }

    /**
     * @param onOpen the onOpen to set
     */
    public void setOnOpen(V8Function onOpen) {
        this.onOpen = onOpen;
    }

    /**
     * @return the onClose
     */
    public V8Function getOnClose() {
        return onClose;
    }

    /**
     * @param onClose the onClose to set
     */
    public void setOnClose(V8Function onClose) {
        this.onClose = onClose;
    }

    /**
     * @return the onMessage
     */
    public V8Function getOnMessage() {
        return onMessage;
    }

    /**
     * @param onMessage the onMessage to set
     */
    public void setOnMessage(V8Function onMessage) {
        this.onMessage = onMessage;
    }

    /**
     * @return the onError
     */
    public V8Function getOnError() {
        return onError;
    }

    /**
     * @param onError the onError to set
     */
    public void setOnError(V8Function onError) {
        this.onError = onError;
    }

}
