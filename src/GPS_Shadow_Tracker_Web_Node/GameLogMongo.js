const { MongoClient } = require("mongodb");

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
        const data = await this.gameCollection.insertOne({ "players": playerData, "inProgress": true, catchLocation: [] });
        this.gameId = data.insertedId;
        console.log(data);
    }

    async addLocationDataLog(sender, location) {
        const mongoCoords = location.convertToMongoCoordinates();
        const key = `players.${sender}.locations`;
        console.log(key);
        if (this.gameId != null) {

        }
    }

    async endGameProgress() {
        if (this.gameId != null) {
            this.gameCollection.updateOne({_id: this.gameId}, {inProgess: false});
            this.gameId = null;
        }
    }

}

module.exports = {
    GameLogMongo: GameLogMongo
}