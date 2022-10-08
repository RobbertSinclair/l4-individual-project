const map = L.map("map").setView([55.8, -4.3], 13);

L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attritution: `&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>`
}).addTo(map);

fetch("/all_locations")
.then(res => res.json())
.then(body => {
    const locations = body.locations;
    locations.forEach(location => {
        let circle = L.circle([location.latitude, location.longitude], {
            color: "red",
            fillColor: '#f03',
            fillOpacity: 0.5,
            radius: location.accuracy
        }).addTo(map);
        console.log(`Added a circle at ${location.latitude} ${location.longitude}`)
    })
})