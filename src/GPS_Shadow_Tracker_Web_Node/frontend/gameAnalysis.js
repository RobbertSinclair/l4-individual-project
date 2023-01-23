import React from "react";
import GameMap from "./GameMap";


export default class GameAnalysis extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            game: {catchLocations: [], players: {}},
            loading: true
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.id != prevProps.id) {
            this.setState({loading: true})
            fetch(`/games/${this.props.id}`)
            .then(res => res.json())
            .then((data) => {
                this.setState({
                    game: data
                })
                this.setState({
                    loading: false
                })
            })
            
        }
    }

    render() {
        return <div>
            {this.state.loading && this.state.game === null ? <h1>Loading</h1> : <GameMap game={this.state.game} />}
        </div>
    }
}