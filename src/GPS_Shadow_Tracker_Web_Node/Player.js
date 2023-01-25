const ws = require("ws");
const { MONGO_CLIENT } = require("./GPSMongo");
const { Location } = require("./Location");

class Player {

    constructor(client, id, chaser) {
        this.client = client;
        this.id = id;
        this.chaser = chaser;
        this.location = new Location(0, 0, 3.8);

    }

    swapChaserState() {
        this.chaser = !this.chaser;
    }
    
    setLocation(latitude, longitude, accuracy) {
        this.location.updateLocation(latitude, longitude, accuracy);
    }

    getLocation() {
        return this.location;
    }

    checkCaught(otherPlayer) {
        const distance = this.location.calculateDistance(otherPlayer.getLocation());
        return distance >= 0 && distance <= 5;
    }

}

module.exports = {
    Player: Player
}