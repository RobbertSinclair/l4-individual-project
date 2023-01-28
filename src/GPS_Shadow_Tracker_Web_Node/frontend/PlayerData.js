import React from "react";
import { Chart as ChartJS, ArcElement, Tooltip, Legend, LinearScale } from "chart.js";
import { Pie, Line } from "react-chartjs-2";

export default class PlayerData extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            player: this.props.player
        }
        ChartJS.register(ArcElement, Tooltip, Legend, LinearScale);
    }

    componentDidUpdate(prevProps) {
        if (prevProps.player !== this.props.player) {
            this.setState({
                player: this.props.player
            })
        }
    }

    render() {
        let pieChartData = {}
        if (this.state.player.runnerTime && this.state.player.chaserTime) {
            pieChartData = {
                datasets: [{
                    label: "Stats",
                    backgroundColor: [
                        'red',
                        'blue'
                    ],
                    data: [this.state.player.runnerTime, this.state.player.chaserTime]
                }],
                labels: [
                    "Runner Time",
                    "Chaser Time"
                ]
            }
        }
        
        return <div>
            <Pie data={pieChartData} />
            <h3>Brand: {this.state.player.brand}</h3>
            <h3>Product: {this.state.player.product}</h3>
        </div>
    }

}