const { SHADOW_THRESHOLD, GAME_DURATION, SECOND, SYNC_DELAY } = require("./Constants");
const { Player } = require("./Player")
const WebSocket = require("ws");
const {Location} = require("./Location");
const {GameLogMongo} = require("./GameLogMongo");

class WebSocketOperations {

    constructor(wss, mongoClient, logClient) {
        this.server = wss;
        this.mongoClient = mongoClient;
        this.logClient = logClient;
        this.gameInProgress = false;
        this.gameTime = 0;
        this.jailTime = false;
    }

    handleReceivedMessage(message, sender) {
        const data = JSON.parse(message);
        console.log(data);
        if (data.type === "CONNECT") {
            this.getBrandAndModel(sender, data);
        }
        if (data.type === "LOCATION") {
            this.getUserLocation(data, sender);
        }
        if (data.type === "START_GAME") {
            this.startGame(sender);
        }
        if (data.type === "END_GAME") {
            this.endGame(sender);
        }
    }

    async initiateJailTime() {
        this.jailTime = true;
        await this.timeDelay(30 * SECOND);
        this.jailTime = false;
        this.broadcastAll(JSON.stringify({"type": "END_JAIL"}));
    }

    async getBrandAndModel(sender, message) {
        if (message.brand && message.product && message.model) {
            this.mongoClient.updatePlayerModel(sender, message.brand, message.product, message.model);
        }
    }

    async startGame(sender) {
        this.gameInProgress = true;
        this.gameTime = GAME_DURATION;
        const message = JSON.stringify({
            "type": "START_GAME",
            "message": "The game has started",
            "gameTime": GAME_DURATION 
        })
        this.broadcastAll(message);
        this.getNewChaserState();
        const players = await this.mongoClient.getAllPlayers();
        this.logClient.createGameInstance(players);
        this.syncClock()
    }


    async syncClock() {
        const interval = setInterval(() => {
            this.gameTime = this.gameTime - SYNC_DELAY;
            const message = JSON.stringify({
                "type": "SYNC_TIME",
                "gameTime": this.gameTime
            });
            if (this.gameTime <= 0 && !this.gameInProgress) {
                this.gameTime = 0;
                if (this.gameInProgress) {
                    this.endGame();
                }
                clearInterval(interval);
            }
            this.broadcastAll(message);
        }, SYNC_DELAY * SECOND)
    }

    async endGame() {
        this.gameInProgress = false;
        await this.logClient.endGameProgress();
        this.broadcastAll(JSON.stringify({"type": "END_GAME", "message": "The game has ended"}))
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
        if (this.gameInProgress) {
            const gameStartMessage = JSON.stringify({
                "type": "START_GAME",
                "message": "The game has started",
                "gameTime": this.gameTime
            });
            sender.send(gameStartMessage);
        }
    }

    async getExistingPlayerId(id, sender) {
        const matchingIds = await this.mongoClient.getPlayerById(id);
        if (matchingIds.length === 1) {
            sender.id = matchingIds[0]._id.toString();
            const connectIdObject = {
                "type": "CONNECT",
                "id": matchingIds[0]._id.toString(),
                "chaser": matchingIds[0].chaser
            }
            sender.send(JSON.stringify(connectIdObject));
        } else {
            this.getId({}, sender);
        }
    }

    async timeDelay(ms) {
        return new Promise(r => setTimeout(r, ms));
    }

    async waitForReconnect(sender) {
        let reconnected = false;
        let counter = 0;
        while (counter < 30 && !reconnected) {
            await this.timeDelay(1000);
            this.server.clients.forEach(client => {
                if (client.id === sender.id) {
                    reconnected = true;
                }
            });
            counter++;
        }
        if (!reconnected) {
            this.playerDisconnected(sender);
        }
    }

    async playerDisconnected(sender) {
        await this.mongoClient.removePlayer(sender);
        const messageObject = {
            "type": "DISCONNECT",
            "message": `Player ${sender.id.toString()} has left the game`
        }
        const messageString = JSON.stringify(messageObject);
        this.broadcastAll(messageString);
        await this.getNewChaserState();
    }

    inShadow(message) {
        try {
            return message.accuracy >= message.minAccuracy * 2;
        } catch (e) {
            return message.accuracy >= SHADOW_THRESHOLD;
        }
    }

    async getUserLocation(message, sender) {
        const startTime = performance.now();
        const newLocation = new Location(message.latitude, message.longitude, message.accuracy);
        await this.mongoClient.updatePlayerLocation(sender.id, newLocation);
        if (this.inShadow(message)) {
            await this.mongoClient.createSingleGPSShadow(message);
        } else {
            const chaser = await this.mongoClient.getCurrentChaser();
            if (!this.jailTime) {
                this.sendToChaser(sender, chaser, JSON.stringify(message))
                const catchList = await this.mongoClient.findAnyPlayersToCatch(chaser);
                if (catchList.length > 0) {
                    const newChaser = catchList[0];
                    this.logClient.logCatchPoint(newLocation);
                    this.initiateJailTime();
                    await this.handlePlayerCaught(chaser, newChaser);
                }
            }
        }
        this.mongoClient.getPlayerById(sender.id).then((player) => { this.logClient.addLocationDataLog(sender, newLocation, player)});
        const endTime = performance.now()
        sender.send(`That request took ${endTime - startTime} milliseconds to process`)
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

    sendToChaser(sender, chaser, message, isBinary) {
        if (chaser != null) {
            console.log(`Chaser id is ${chaser._id.toString()}`)
            if (sender.id != chaser._id.toString()) {
                this.server.clients.forEach((client) => {
                    if (client.id === chaser._id.toString() && client.readyState === WebSocket.OPEN) {
                        client.send(message, {binary: isBinary});
                    }
                })
            }
        }
    }

    async handlePlayerCaught(chaser, newChaser) {
        await this.mongoClient.handleCaughtPlayer(chaser, newChaser);
        this.server.clients.forEach((client) => {
            if (client.id.toString() === newChaser._id.toString() && client.readyState === WebSocket.OPEN) {
                const data = JSON.stringify({
                    "type": "NEW_TYPE",
                    "message": "You have been caught\nYou are now the chaser",
                    "chaser": true
                });
                client.send(data);
            } else if (client.id.toString() === chaser._id.toString() && client.readyState === WebSocket.OPEN) {
                const data = JSON.stringify({
                    "type": "NEW_TYPE",
                    "message": `You have caught ${newChaser._id.toString()}\n You are now a runner`,
                    "chaser": false
                });
                client.send(data);
            } else if (client.readyState === WebSocket.OPEN) {
                const data = JSON.stringify({
                    "type": "NEW_CHASER",
                    "message": `Player ${newChaser._id.toString()} is now the chaser`
                });
                client.send(data);
            }
        })
    }

    async getNewChaserState() {
        const result = await this.mongoClient.selectRandomPlayerAsChaser();
        if (result.type === "ERROR") {
            return;
        }
        const id = result._id.toString();
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
        this.initiateJailTime();
        return;
    }
}

module.exports ={
    WebSocketOperations
}