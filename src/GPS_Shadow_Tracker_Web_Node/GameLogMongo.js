const { MongoClient } = require("mongodb");
const {Location} = require("./Location");

class GameLogMongo {

    constructor(mongoClient) {
        this.mongoClient = mongoClient;
        this.gameCollection = this.mongoClient.db("gpsGame").collection("games");
        this.gameId = null;
        console.log("Game Log instance created");
    }

    async createGameInstance(playerArray) {
        const playerData = {};
        playerArray.forEach((player) => {
            const playerId = player._id.toString()
            playerData[playerId] = {
                locations: [],
                runnerTime: 0,
                chaserTime: 0
            }
        })
        const data = await this.gameCollection.insertOne({ "players": playerData, "inProgress": true, catchLocations: [] });
        this.gameId = data.insertedId;
        const location = new Location(0, 0, 0);
        console.log(data);
    }

    async addLocationDataLog(sender, location, player) {
        const currentPlayer = player[0];
        const mongoCoords = location.convertToMongoCoordinates().location;
        const locationsKey = `players.${sender.id}.locations`;
        const runnerKey = `players.${sender.id}.runnerTime`;
        const chaserKey = `players.${sender.id}.chaserTime`;
        const updateQuery = {
            "$push": {
                [locationsKey]: mongoCoords
            }
        }
        if (currentPlayer.chaser) {
            updateQuery["$inc"] = {[chaserKey]: 2}
        } else {
            updateQuery["$inc"] = {[runnerKey]: 2}
        }
        if (this.gameId != null) {
            this.gameCollection.updateOne({"_id": this.gameId},
                updateQuery
                )
        }
    }

    async endGameProgress() {
        if (this.gameId != null) {
            const result = await this.gameCollection.updateOne({_id: this.gameId}, {$set: {inProgress: false}});
            console.log(result);
            await this.getWinner();
            this.gameId = null;
        }
    }

    async getWinner() {
        const currentGame = await this.gameCollection.findOne({_id: this.gameId});
        const playerKeys = Object.keys(currentGame.players);
        const runnerTimes = playerKeys.map((player) => currentGame.players[player].runnerTime);
        const maxIndex = runnerTimes.indexOf(Math.max(runnerTimes));
        console.log(`The winner was ${playerKeys[maxIndex]}`)
        console.log(runnerTimes);
    }

    async logCatchPoint(location) {
        if (this.gameId != null) {
            const mongoCoords = location.convertToMongoCoordinates()
            const result = await this.gameCollection.updateOne({_id: this.gameId}, {$push: {catchLocations: mongoCoords}});
            console.log(result);
        }
    }
}

module.exports = {
    GameLogMongo: GameLogMongo
}