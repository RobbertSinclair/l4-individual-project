
const endpoint = `ws://${window.location.host}/game_room/test/`;
const socket = new WebSocket(endpoint);

socket.onopen = (e) => {
    console.log("open", e);
}

socket.onmessage = (e) => {
    
}