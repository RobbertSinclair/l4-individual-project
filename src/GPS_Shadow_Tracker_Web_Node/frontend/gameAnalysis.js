import React from "react";
import GameMap from "./GameMap";


export default class GameAnalysis extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            game: {},
            loading: true
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.id != prevProps.id) {
            this.setState({loading: true})
            fetch(`/games/${this.props.id}`)
            .then(res => res.json())
            .then((data) => {
                console.log(data);
                this.setState({
                    game: data,
                    loading: false
                })
                console.log(this.state.game);
            })
            
        }
    }

    render() {
        return <div>
            {this.state.loading ? <h1>Loading</h1> : <GameMap game={this.state.game} />}
        </div>
    }
}