"use strict";
const express = require("express");
const router = express.Router();
const fs = require("fs").promises;
const http = require("http");
const WebSocket = require("ws");
const PORT = process.env.PORT;
const HOST = "0.0.0.0";
const URL = require("node:url");
const ROOT_NAME = __dirname;
const TEMPLATE_DIR = `${ROOT_NAME}/templates`;
const { MongoClient } = require("mongodb");
const url = `mongodb://${process.env.MONGO_USER}:${process.env.MONGO_PASSWORD}@${process.env.MONGO_HOST}:${process.env.MONGO_PORT}`;
const { GPSMongo } = require("./GPSMongo.js");
const { WebSocketOperations } = require("./WebSocketOperations");
const {GameLogMongo} = require("./GameLogMongo");
const players = {};
const chaser = null;
const mongoClient = new MongoClient(url);
const gpsMongo = new GPSMongo(mongoClient);
const logMongo = new GameLogMongo(mongoClient);
gpsMongo.clearPlayerList();
const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server: server });
const operationClient = new WebSocketOperations(wss, gpsMongo, logMongo);
app.use("/static", express.static("public"));
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

app.get("/", (req, res) => {
    fs.readFile(`${TEMPLATE_DIR}/index_react.html`)
    .then(contents => {
        res.setHeader("Content-Type", "text/html");
        res.writeHead(200);
        let s = contents.toString();
        res.end(s);
        console.log("GET / 200");
        return;
    })
    .catch(err => {
        res.writeHead(500);
        res.end(err);
        console.log("GET / 500");
        return;
    })
});

app.get("/all_locations", async (req, res) => {
    res.setHeader("Content-Type", "application/json");
    res.writeHead(200);
    const data = await gpsMongo.getAllGPSPoints();
    res.end(JSON.stringify(data));
    console.log(`GET /all_locations 200`);
})

app.get("/locations_time/:start/:end", (req, res) => {
    res.setHeader("Content-Type", "application/json");
    res.writeHead(200);
    res.end(JSON.stringify({"start": req.params.start, "end": req.params.end}));
    console.log(`GET /locations_time/${req.params.start}/${req.params.end} 200`);
})

app.post("/submit_location", async (req, res) => {
    const result = await gpsMongo.createSingleGPSShadow(req.body);
    res.setHeader("Content-Type", "application/json");
    res.writeHead(200);
    if (result) {
        res.end(JSON.stringify({"message": "success"}));
    } else {
        res.end(JSON.stringify({"message": "Location already submitted"}));
    }
})

app.get("/gps_shadows", async(req, res) => {
    const data = await gpsMongo.getGPSShadows();
    res.setHeader("Content-Type", "application/json");
    res.writeHead(200);
    res.end(JSON.stringify(data));
})

app.post("/gps_shadows_nearby/:distance", async(req, res) => {
    const result = await gpsMongo.getNearbyGpsShadows(req.body, Number(req.params.distance));
    res.setHeader("Content-Type", "application/json");
    res.writeHead(200);
    res.end(JSON.stringify(result));
})

function formatQueries(url) {
    let wsURL = new URL.parse(url);
    const urlQuery = wsURL.query;
    let result = {};
    try {
        const queries = urlQuery.split("&");
        console.log(queries);
        queries.forEach((param) => {
            let keyPair = param.split("=");

            result[keyPair[0]] = keyPair[1];
        })
    } catch (e) {
        result = {};
    }
    return result;
}

wss.on("connection", (sender, req) => {
    console.log("NEW CONNECTION");
    const params = formatQueries(req.url);
    console.log(params);
    if (Object.keys(params).length === 0 || !Object.keys(params).includes("ID")) {
        operationClient.getId({}, sender)
    } else {
        operationClient.getExistingPlayerId(params["ID"], sender);
    }
    
    sender.on("message", (message, isBinary) => {
        console.log(message);
        operationClient.handleReceivedMessage(message, sender)
    });

    sender.on("close", () => {
        operationClient.waitForReconnect(sender);
    })

})

server.listen(PORT, () => {
    console.log(`Listening on *:${PORT}`);
})
