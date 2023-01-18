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
        const data = await this.gameCollection.insertOne({ "players": playerData, "inProgress": true, catchLocation: [] });
        this.gameId = data.insertedId;
        const location = new Location(0, 0, 0);
        console.log(data);
    }

    async addLocationDataLog(sender, location, player) {
        console.log(player);
        const mongoCoords = location.convertToMongoCoordinates().location;
        const key = `players.${sender.id}.locations`;
        console.log(key);
        const updateQuery = {
            "$push": {
                [key]: mongoCoords
            }
        }
        console.log(`Game Id is ${this.gameId}`);
        if (this.gameId != null) {
            this.gameCollection.updateOne({"_id": this.gameId},
                updateQuery
                )
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