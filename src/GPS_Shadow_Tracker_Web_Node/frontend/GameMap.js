import React from "react";
import { MapContainer, TileLayer, Marker, Circle, Popup } from "react-leaflet";

export default class GameMap extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            game: {},
            players: []
        }
        this.circleAreas = []
        this.createCircles = this.createCircles.bind(this);
    }

    componentDidMount() {
        this.setState({
            game: this.props.game
        })
    }

    componentDidUpdate(prevProps) {
        if (this.props.game !== null && this.props.game !== prevProps.game) {
            console.log("this.state.game");
            console.log(this.state.game);
            this.setState({
                game: this.props.game
            })
            this.createCircles();
        }
        console.log(this.state);
    }

    createCircles() {
        this.circleAreas = [];
        const colours = ["blue", "red", "yellow"];
        console.log(this.state.game.players);
        const keys = Object.keys(this.state.game.players);
        console.log("Player Keys are")
        console.log(keys);
        let counter = 0;
        keys.forEach((key) => {
            const colour = colours[counter % colours.length]
            const circles = this.state.game.players[key].locations.map(location => <Circle
                center={[location.coordinates[1], location.coordinates[0]]}
                radius={5}
                pathOptions={{color: colour}} >
                <Popup>{key}</Popup>
            </Circle>);
            console.log(circles);
            this.circleAreas = this.circleAreas.concat(circles);
            console.log("this.circleAreas");
            console.log(this.circleAreas);
            counter++;
        })

    }

    render() {
        let catchMarkers = [];
        if (this.state.game.catchLocations) {
            catchMarkers = this.state.game.catchLocations.map(location => <Marker position={[location.location.coordinates[1], location.location.coordinates[0]]} ></Marker>);
        }
        return <div>
            <MapContainer center={[55.8724, -4.29]} zoom={14} scrollWheelZoom={true}>
                <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {catchMarkers}
                {this.circleAreas}
            </MapContainer>
        </div>
    }

}