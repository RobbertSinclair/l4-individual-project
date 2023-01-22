import React from "react";
import { render } from "react-dom"
import GameAnalysis from "./gameAnalysis.js"

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {gameIds: [], selectedGame: null}
        this.onSelect = this.onSelect.bind(this);
    }

    componentDidMount() {
        fetch("/games")
        .then((res) => res.json())
        .then((data) => {
            this.setState({
                gameIds: data
            })
        })
    }

    onSelect(event) {
        this.setState({
            selectedGame: event.target.value
        });
        console.log(this.state);
    }

    render() {
        const options = this.state.gameIds.map(id => <option value={id}>{id}</option>)
        return <div>
            <h1>GPS Shadow Game Evaluation Log</h1>
            <form>
                <label for="games">Select Game</label>
                <select onChange={this.onSelect}>
                    {options}
                </select>
            </form>
            {this.state.selectedGame && <GameAnalysis id={this.state.selectedGame} />}
        </div>

    }
}

const root = document.getElementById("root");
console.log(root);
render(<App />, root);
export default App;