const { MongoClient } = require("mongodb");
const moment = require("moment");

class GPSMongo {

    constructor(url) {
        this.mongoClient = new MongoClient(url);
        this.connect();
        console.log("GPSMongo instnace created");
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
            coordinates: {
                type: "Point",
                coordinates: [data.latitude, data.longitude]
            },
            accuracy: data.accuracy,
            time: moment().format("HH:mm:ss")
        }
    }

    formatLocations(data) {
        return data.map(item => {
            return {
                latitude: item.coordinates.coordinates[0],
                longitude: item.coordinates.coordinates[1],
                accuracy: item.accuracy,
                time: item.time
            }
        })
    }

    async createSingleGPSShadow(data) {
        const newGPSSpot = this.createGPSSpotDocument(data);

        await this.mongoClient.db("gpsGame").collection("gpsShadows").insertOne(newGPSSpot);

    }

    
    async getAllGPSPoints() {
        const data = await this.mongoClient.db("gpsGame").collection("gpsShadows").find({}).toArray();
        if (data) {
            return this.formatLocations(data);  
        } else {
            return null;
        }
        

    }

}

module.exports = {
    GPSMongo: GPSMongo
}