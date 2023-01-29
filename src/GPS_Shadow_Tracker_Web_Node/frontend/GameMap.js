import React from "react";
import { MapContainer, TileLayer, Marker, Circle, Popup } from "react-leaflet";
import PlayerData from "./PlayerData.js";

export default class GameMap extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            game: this.props.game,
            players: this.props.game.players
        }
        this.circleAreas = []
    }

    componentDidMount() {
        this.setState({
            game: this.props.game,
            players: this.props.game.players
        })
    }

    componentDidUpdate(prevProps) {
        if (this.props.game !== null && this.props.game !== prevProps.game) {
            console.log("this.state.game");
            console.log(this.state.game);
            this.setState({
                game: this.props.game,
                players: this.props.game.players
            })
        }
        console.log(this.state);
    }

    render() {
        let catchMarkers = [];
        let circles = [];
        const playerDetails = [];
        const colours = ["blue", "red", "yellow"];
        if (this.state.game.catchLocations) {
            catchMarkers = this.state.game.catchLocations.map(location => <Marker position={[location.location.coordinates[1], location.location.coordinates[0]]} ></Marker>);
        }
        let counter = 0;
        Object.keys(this.state.players).forEach((key) => {
            let player = this.state.players[key];
            const newCircles = player.locations.map(location => <Circle
                center={[location.location.coordinates[1], location.location.coordinates[0]]}
                radius={4}
                pathOptions={{color: colours[counter % colours.length]}}
            ></Circle>)
            circles = circles.concat(newCircles);
            playerDetails.push(<PlayerData player={this.state.players[key]} />)
            counter++;
        })
        return <div>
            <MapContainer center={[55.8724, -4.29]} zoom={14} scrollWheelZoom={true}>
                <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {catchMarkers}
                {circles}
            </MapContainer>
            <div>
                {playerDetails}
            </div>
        </div>
    }

}