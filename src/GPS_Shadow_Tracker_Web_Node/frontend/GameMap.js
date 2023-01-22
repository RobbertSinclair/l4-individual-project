import React from "react";
import { MapContainer, TileLayer, Marker } from "react-leaflet";

export default class GameMap extends React.Component {

    constructor(props) {
        super(props);
        this.setState = {
            game: this.props.game
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.game != prevProps.game) {
            this.setState({
                game: this.props.game
            })
        }
    }

    render() {
        let catchMarkers;
        if (this.state.game != null) {
            catchMarkers = this.state.game.catchLocations.map(location => <Marker position={[location.location.coordinates[1], location.location.coordinates[0]]} ></Marker>);
        } else {
            catchMarkers = [];
        }
        return <div>
            <MapContainer center={[55.8724, -4.29]} zoom={14} scrollWheelZoom={true}>
                <TileLayer
                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {catchMarkers}
            </MapContainer>
        </div>
    }

}