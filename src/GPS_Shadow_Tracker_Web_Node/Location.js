const { greatCircleDistance } = require("great-circle-distance");

class Location {

    constructor(latitude, longitude, accuracy) {
        this.updateLocation(latitude, longitude, accuracy)
    }

    updateLocation(latitude, longitude, accuracy) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
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

}

module.exports = {
    Location: Location
}