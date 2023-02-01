const { MongoClient } = require("mongodb");
const { Location } = require("./Location");
const { Player } = require("./Player")
const {CATCH_THRESHOLD, SHADOW_THRESHOLD} = require("./Constants");
const mongoose = require("mongoose")

class GPSMongo {

    constructor(mongoClient) {
        this.median = 0;
        this.mongoClient = mongoClient;
        this.connect();
        this.shadowCollection = this.mongoClient.db("gpsGame").collection("gpsShadows");
        this.userCollection = this.mongoClient.db("gpsGame").collection("players");        
        this.calculateMedian()
        console.log("GPSMongo instance created");
    }

    async connect() {
        try {
            await this.mongoClient.connect();
        } catch (err) {
            console.error(err, "Failed connection to MongoDB");
        }
    }

    async clearPlayerList() {
        await this.userCollection.deleteMany({});
    }

    convertStringToMongoObjectId(string) {
        return mongoose.Types.ObjectId(string);
    }

    createGPSSpotDocument(data) {
        return {
            location: {
                type: "Point",
                coordinates: [Number(data.longitude), Number(data.latitude)]
            },
            accuracy: Number(data.accuracy)
        }
    }

    formatLocations(data) {
        return data.map(item => {
            return {
                latitude: item.location.coordinates[1],
                longitude: item.location.coordinates[0],
                accuracy: item.accuracy,
                time: item.time
            }
        })
    }

    async getNearbyGpsShadows(location, distance) {
        const query = {
            location: {
                $near: {
                    $geometry: {
                        type: "Point",
                        coordinates: [Number(location.longitude), Number(location.latitude)]
                    },
                    $maxDistance: distance
                },
            },
            accuracy: {$gt: SHADOW_THRESHOLD}
        }

        const result = await this.shadowCollection.find(query).toArray();
        return {locations: this.formatLocations(result), stats: {"median": this.median}};
    }

    async calculateMedian() {
        const median = await this.shadowCollection.find({}).sort({"accuracy": 1}).skip(await this.shadowCollection.countDocuments() / 2).limit(1).toArray();
        this.median = median[0].accuracy;
    }

    async getGPSShadows() {
        const data = await this.mongoClient.db("gpsGame").collection("gpsShadows").find({accuracy: {$gt: 6}});
        const dataArray = await data.toArray();
        if (data) {
            return { locations: this.formatLocations(dataArray), stats: {"median": this.median} };
        } else {
            return null;
        }
    }

    async createSingleGPSShadow(data) {
        const newGPSSpot = this.createGPSSpotDocument(data);
        const result = await this.mongoClient.db("gpsGame").collection("gpsShadows").find({location: {$eq: newGPSSpot.location}, accuracy: {$eq: newGPSSpot.accuracy}}).toArray();
        if (result.length == 0) {
            await this.mongoClient.db("gpsGame").collection("gpsShadows").insertOne(newGPSSpot);
            return true;
        } else {
            return false;
        }
    }

    async getAllGPSPoints() {
        const data = await this.shadowCollection.find({});
        const dataArray = await data.toArray();
        if (data) {
            return { locations: this.formatLocations(dataArray), stats: {"median": this.median} };  
        } else {
            return null;
        }
    }

    async createPlayer(sender) {
        const newUser = {
            "chaser": false, 
            "location": {
                "type": "Point",
                "coordinates": [0, 0]
            },
            "accuracy": 3.8,
            "minAccuracy": 3.8
        };
        const data = await this.userCollection.insertOne(newUser);
        console.log(data);
        return {"id": data.insertedId.toString(), "chaser": newUser.chaser};
    }

    async updatePlayerModel(sender, brand, model, product) {
        const objectId = this.convertStringToMongoObjectId(sender.id);
        await this.userCollection.updateOne({_id: objectId}, {$set: {"brand": brand, "model": model, "product": product}});
    }

    async selectRandomPlayerAsChaser() {
        try {
            const otherChasers = await this.userCollection.updateOne({chaser: true}, [{$set: {chaser: false}}])
            const sample = await this.userCollection.aggregate([{ $sample: {size: 1}}]).toArray();
            const newChaser = sample[0];
            const results = await this.userCollection.updateOne(newChaser, [{ $set: {chaser: true}}]);
            return await this.userCollection.findOne({_id: newChaser._id});
        } catch (err) {
            return {"type": "ERROR"}
        }
        
    }

    async getCurrentChaser() {
        return await this.userCollection.findOne({chaser: true});
    }

    async getPlayerById(id) {
        console.log(`MongoId is ${id}`);
        const mongoId = mongoose.Types.ObjectId(id);
        return await this.userCollection.find({_id: mongoId}).toArray();
    }

    async updatePlayerLocation(id, location) {
        try {
            const mongoCoords = location.convertToMongoCoordinates();
            console.log(mongoCoords);
            console.log(`Player id is ${id}`);
            const mongoId = mongoose.Types.ObjectId(id);
            console.log(mongoId);
            const result = await this.userCollection.updateOne({_id: mongoId}, [
                {
                    $set: mongoCoords
                }
            ])
            console.log(result);
            console.log("Player Location changed successfully")

        } catch (err) {
            console.log("Error in the updatePlayerLocation")
            console.log(err.message);
        }
    }

    async findAnyPlayersToCatch() {
        const chaser = await this.getCurrentChaser();
        console.log(`CHASER_ID = ${chaser._id}`);
        const query = {
            _id: {$ne: chaser._id},
            location: {
                $near: {
                    $geometry: {
                        type: "Point",
                        coordinates: [Number(chaser.location.coordinates[0]), Number(chaser.location.coordinates[1])]
                    },
                    $maxDistance: CATCH_THRESHOLD
                },
            }
        }
        console.log(query);
        const playersToCatch = await this.userCollection.find(query).toArray();
        console.log(`Chaser Location = ${chaser.location.coordinates.toString()}`);
        console.log(playersToCatch);
        playersToCatch.forEach((player) => {
            console.log(player.location.coordinates);
        })
        return playersToCatch;
    }

    async handleCaughtPlayer(chaser, newChaser) {
        try {
            await this.userCollection.updateOne({_id: chaser._id}, [{$set: {chaser: false}}])
            await this.userCollection.updateOne({_id: newChaser._id}, [{$set: {chaser: true}}])
        } catch (err) {
            console.log("Handle Caught Player Error");
            console.log(err.message);
        }
    }

    async removePlayer(sender) {
        const idToDelete = this.convertStringToMongoObjectId(sender.id);
        await this.userCollection.deleteOne({ "_id": idToDelete});
    }

    async getAllPlayers() {
        return await this.userCollection.find({}).toArray();
    }

}

module.exports = {
    GPSMongo: GPSMongo
}