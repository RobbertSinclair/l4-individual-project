
const endpoint = `wss://${window.location.host}`;
const socket = new WebSocket(endpoint);

socket.onopen = (e) => {
    console.log("SERVER CONNECTION RECEIVED");
}

socket.onmessage = (e) => {
    console.log(`Message from the server: ${e.data}`);
}