import React from "react";
import { render } from "react-dom"

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {gameIds: [], selectedGame: null}
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

    render() {
        const options = this.state.gameIds.map(id => <option value={id}>id</option>)
        return <div>
            <h1>GPS Shadow Game Evaluation Log</h1>
            <form>
                <label for="games">Select Game</label>
                <select>
                    {options}
                </select>
            </form>
        </div>

    }
}

const root = document.getElementById("root");
console.log(root);
render(<App />, root);
export default App;