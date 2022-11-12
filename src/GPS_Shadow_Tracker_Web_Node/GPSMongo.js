const { MongoClient } = require("mongodb");
const moment = require("moment");

class GPSMongo {

    constructor(url) {
        this.median = 0;
        this.mongoClient = new MongoClient(url);
        this.connect();
        this.collection = this.mongoClient.db("gpsGame").collection("gpsShadows");
        
        this.calculateMedian()
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
            location: {
                type: "Point",
                coordinates: [data.longitude, data.latitude]
            },
            accuracy: data.accuracy,
            time: moment().format("HH:mm:ss")
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
                        coordinates: [location.longitude, location.latitude]
                    },
                    $maxDistance: distance
                },
            },
            accuracy: {$gt: 6}
        }

        const result = await this.collection.find(query).toArray();
        return {locations: this.formatLocations(result), stats: {"median": this.median}};
    }

    async calculateMedian() {
        const median = await this.collection.find({}).sort({"accuracy": 1}).skip(await this.collection.countDocuments() / 2).limit(1).toArray();
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

        await this.mongoClient.db("gpsGame").collection("gpsShadows").insertOne(newGPSSpot);

    }

    
    async getAllGPSPoints() {
        const data = await this.collection.find({});
        const dataArray = await data.toArray();
        if (data) {
            return { locations: this.formatLocations(dataArray), stats: {"median": this.median} };  
        } else {
            return null;
        }
        

    }

}

module.exports = {
    GPSMongo: GPSMongo
}