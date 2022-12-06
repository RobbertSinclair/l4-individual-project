
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
        console.log("Received message");
        console.log(this.mongoClient);
        const data = JSON.parse(message);
        if (data.type === "LOCATION") {
            this.getUserLocation(message, sender);
        }
    }

    async getId(data, sender) {
        console.log("GET ID");
        console.log(this.mongoClient);
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
        console.log("PLAYER_DISCONNECTED");
        console.log(this.mongoClient);
        await this.mongoClient.removePlayer(sender);
        const messageObject = {
            "type": "DISCONNECT",
            "message": `Player ${sender.id} has left the game`
        }
        const messageString = JSON.stringify(messageObject);
        this.broadcastAll(messageString);
    }

    async getUserLocation(message, sender) {
        console.log("GET USER LOCATION");
        console.log(this.mongoClient)
        if (message.accuracy >= SHADOW_THRESHOLD) {
            await this.mongoClient.createSingleGPSShadow(message);
        }
        this.broadcastExceptSender(sender, message)
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