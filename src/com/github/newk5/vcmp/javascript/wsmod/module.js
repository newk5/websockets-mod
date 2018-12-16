"use strict";
module.exports = {
    startServer: function (host, port, options) { 
        return _WebsocketServerWrapper_.startServer(host, port, options);
    },
    startClient: function (url, options) {
        return _WebsocketServerWrapper_.startClient(url, options);
    }

};