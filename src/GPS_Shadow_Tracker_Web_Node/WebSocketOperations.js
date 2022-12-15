
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
        if (data.type === "PLAYER_CAUGHT") {
            this.handlePlayerCaught(data, sender);   
        }
    }

    async getId(data, sender) {
        const playerData = await this.mongoClient.createPlayer();
        const connectMessageObject = {
            "type": "CONNECT_MESSAGE",
            "message": `Player ${playerData.id.toString()} has joined`,
            "player": playerData.id.toString()
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
            "message": `Player ${sender.id.toString()} has left the game`
        }
        const messageString = JSON.stringify(messageObject);
        this.broadcastAll(messageString);
        await this.mongoClient.selectRandomPlayerAsChaser();
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

    async handlePlayerCaught(message, sender) {
        console.log("PLAYER_CAUGHT");
        console.log(message.caught_id);
        await this.mongoClient.handleCaughtPlayer(sender, message.caught_id);
        this.server.clients.forEach((client) => {
            console.log(client.id);
            if (client.id.toString() === message.caught_id && client.readyState === WebSocket.OPEN) {
                const data = JSON.stringify({
                    "type": "NEW_TYPE",
                    "message": "You have been caught\nYou are now the chaser",
                    "chaser": true
                });
                client.send(data);
            } else if (client === sender && client.readyState === WebSocket.OPEN) {
                const data = JSON.stringify({
                    "type": "NEW_TYPE",
                    "message": "You are now a runner",
                    "chaser": false
                });
                client.send(data);
            } else if (client.readyState === WebSocket.OPEN) {
                const data = JSON.stringify({
                    "type": "NEW_CHASER",
                    "message": `Player ${message.caught_id} is now the chaser`
                });
                client.send(data);
            }
        })
    }

    async getNewChaserState() {
        const result = await this.mongoClient.selectRandomPlayerAsChaser();
        const id = result._id.toString();
        console.log(id);
        this.server.clients.forEach((client) => {
            if (client.id.toString() === id && client.readyState === WebSocket.OPEN) {
                const data = JSON.stringify({
                    "type": "NEW_TYPE",
                    "message": "You are the chaser",
                    "chaser": true
                });
                client.send(data);
            } else if (client.readyState === WebSocket.OPEN){
                const data = JSON.stringify(({
                    "type": "NEW_TYPE",
                    "message": "You are a runner",
                    "chaser": false
                }));
                client.send(data);
            }
        })
        return;
    }

}

module.exports ={
    WebSocketOperations
}