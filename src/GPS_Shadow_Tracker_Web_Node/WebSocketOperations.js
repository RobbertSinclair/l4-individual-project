
const { SHADOW_THRESHOLD } = require("./Constants");
const WebSocket = require("ws");

class WebSocketOperations {

    types = {
        "LOCATION": this.getLocation,
        "CONNECT": this.getId
    }

    constructor(wss, mongoClient) {
        this.server = wss;
        this.mongoClient = mongoClient
        
    }

    async getId(sender) {
        const id = await this.mongoClient.createPlayer();
        const connectMessageObject = {
            "type": "CONNECT_MESSAGE",
            "message": `Player ${id.toString()} has joined`
        }
        const connectMessageString = JSON.stringify(connectMessageObject) 
        this.broadcastExceptSender(sender, connectMessageString);
        const connectIdObject = {
            "type": "CONNECT",
            "id": id.toString()
        }
        sender.id = id;
        sender.send(JSON.stringify(connectIdObject));
    }

    async playerDisconnected(sender) {
        await this.mongoClient.removePlayer(sender);
        const messageObject = {
            "type": "DISCONNECT",
            "message": `Player ${sender.id} has left the game`
        }
        const messageString = JSON.stringify(messageObject);
        this.broadcastAll(messageString);
    }

    broadcastAll(message, isBinary) {
        this.server.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(message, {binary: isBinary});
            }
        })
    }

    broadcastExceptSender(sender, message, isBinary) {
        this.server.clients.forEach((client) => {
            if (client !== sender && client.readyState === WebSocket.OPEN) {
                client.send(message, {binary: isBinary});
            }
        })
    }

}

module.exports ={
    WebSocketOperations
}