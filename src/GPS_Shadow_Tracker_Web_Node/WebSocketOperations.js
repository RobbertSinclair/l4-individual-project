
const { SHADOW_THRESHOLD } = require("./Constants");
const WebSocket = require("ws");

class WebSocketOperations {

    constructor(wss, mongoClient) {
        this.server = wss;
        this.mongoClient = mongoClient;
    }
    
    types = {
        "LOCATION": this.getUserLocation,
        "CONNECT": this.getId
    }

    handleReceivedMessage(message, sender) {
        const data = JSON.parse(message);
        if (data.type === "LOCATION") {
            this.getUserLocation(data, sender);
        }
    }

    async getId(data, sender) {
        const playerData = await this.mongoClient.createPlayer();
        const connectMessageObject = {
            "type": "CONNECT_MESSAGE",
            "message": `Player ${data.toString()} has joined`
        }
        const connectMessageString = JSON.stringify(connectMessageObject) 
        this.broadcastExceptSender(sender, connectMessageString);
        const connectIdObject = {
            "type": "CONNECT",
            "id": playerData.id.toString(),
            "chaser": playerData.chaser
        }
        sender.id = playerData.id;
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

    async getUserLocation(message, sender) {
        console.log(message.accuracy);
        if (message.accuracy >= SHADOW_THRESHOLD) {
            await this.mongoClient.createSingleGPSShadow(message);
        }
        this.broadcastExceptSender(sender, JSON.stringify(message))
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