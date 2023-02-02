const { greatCircleDistance } = require("great-circle-distance");

class Location {

    constructor(latitude, longitude, accuracy, noiseRatio, minAccuracy) {
        this.updateLocation(latitude, longitude, accuracy)
    }

    updateLocation(locationObject) {
        this.latitude = locationObject.latitude;
        this.longitude = locationObject.longitude;
        this.accuracy = locationObject.accuracy;
        this.noiseRatio = locationObject.noiseRatio;
        this.minAccuracy = locationObject.minAccuracy;
    }

    calculateDistance(otherLocation) {
        try {
            const distanceCoords = {
                lat1: this.latitude,
                lng1: this.longitude,
                lat2: otherLocation.latitude,
                lng2: otherLocation.longitude
            }
            return greatCircleDistance(distanceCoords) * 1000;
        } catch (e) {
            console.log("CANNOT CALCULATE DISTANCE");
            return -1;
        } 
    }

    convertToMongoCoordinates() {
        return {
            "location": {
                "type": "Point",
                "coordinates": [this.longitude, this.latitude]
            },
            "accuracy": this.accuracy,
            "noiseRatio": this.noiseRatio,
            "minAccuracy": this.minAccuracy
        }
    }

}

module.exports = {
    Location: Location
}