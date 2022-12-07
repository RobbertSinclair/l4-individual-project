const { MongoClient } = require("mongodb");
const moment = require("moment");

class GPSMongo {

    constructor(url) {
        this.median = 0;
        this.mongoClient = new MongoClient(url);
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
            accuracy: {$gt: 6}
        }

        const result = await this.shadowCollection.find(query).toArray();
        return {locations: this.formatLocations(result), stats: {"median": this.median}};
    }

    async calculateMedian() {
        const median = await this.shadowCollection.find({}).sort({"accuracy": 1}).skip(await this.shadowCollection.countDocuments() / 2).limit(1).toArray();
        console.log(median[0].accuracy);
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
        const newUser = {"chaser": false};
        const data = await this.userCollection.insertOne(newUser);
        return data.insertedId;
    }

    async swapChaserState(sender) {
        const sample = await this.userCollection.aggregate([{ $sample: {size: 1}}]);
        console.log(sample);
    }

    async removePlayer(sender) {
        await this.userCollection.deleteOne({ "_id": sender.id});
    }

}

module.exports = {
    GPSMongo: GPSMongo
}