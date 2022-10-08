const map = L.map("map").setView([55.8724, -4.29], 14);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attritution: `&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>`
}).addTo(map);

function calculateColour(accuracy) {
    if (accuracy < 20) {
        return "blue";
    } else if (accuracy >= 20 && accuracy < 40) {
        return "green";
    } else if (accuracy >=40 && accuracy < 60) {
        return "orange";
    } else {
        return "red";
    }
} 

fetch("/all_locations")
.then(res => res.json())
.then(body => {
    const locations = body.locations;
    locations.forEach(location => {
        let circle = L.circle([location.latitude, location.longitude], {
            color: calculateColour(location.accuracy),
            fillColor: calculateColour(location.accuracy),
            fillOpacity: 0.5,
            radius: location.accuracy
        }).addTo(map);
        circle.bindPopup(`Accuracy: ${location.accuracy}<br>Time: ${location.time}`)
        console.log(`Added a circle at ${location.latitude} ${location.longitude}`)
    })
})