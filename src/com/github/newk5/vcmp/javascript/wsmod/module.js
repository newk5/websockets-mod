"use strict";
module.exports = {
    startServer: function (host, port, options) {
        _WebsocketServerWrapper_.startServer(host, port, options);
        return {
            broadcast: function (endpoint, data) {
                _WebsocketServerWrapper_.broadcast(endpoint, data);
            },
            addEndpoint: function (endpoint, options) {
                return _WebsocketServerWrapper_.addEndpoint(endpoint, options);
            }

        };
    },

    startClient: function (url, options) {
        return _WebsocketServerWrapper_.startClient(url, options);
    }

};