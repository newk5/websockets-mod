"use strict";
module.exports = {
    startServer: function (host, port, options) { 
        return _WebsocketServerWrapper_.startServer(host, port, options);
    },
    addEndpoint: function (endpoint, options) { 
        return _WebsocketServerWrapper_.addEndpoint(endpoint, options);
    },
    startClient: function (url, options) {
        return _WebsocketServerWrapper_.startClient(url, options);
    }

};