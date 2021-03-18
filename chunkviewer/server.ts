import WebSocket from "ws";
import { connect } from "nats";

var server = new WebSocket.Server({ port: 1000 });
var clients: WebSocket[] = [];

connect().then((conn) => {
    conn.subscribe("chunk", {
        callback: (err, msg) => {
            var data = new TextDecoder("utf-8").decode(msg.data);
            clients.forEach((c) => {
                c.send(data);
            });
        },
    });
});

server.on("connection", (conn) => {
    clients.push(conn);
});
