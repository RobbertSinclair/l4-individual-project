const map = L.map("map").setView([55.8724, -4.29], 14);
const timeSelect = document.getElementById("timeSelect");
const mean = document.getElementById("mean");
const min = document.getElementById("min");
const max = document.getElementById("max");
L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attritution: `&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>`
}).addTo(map);

let markerLayer = L.featureGroup();


timeSelect.addEventListener("submit", (e) => {
    e.preventDefault();
    const start = document.getElementById("start");
    const end = document.getElementById("end");

    console.log(start.value);
    console.log(end.value);
    const filterUrl = `/locations_time/${start.value}/${end.value}`;
    getLocations(filterUrl);
})

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


function getLocations(url) {
    fetch(url)
    .then(res => res.json())
    .then(body => {
        map.removeLayer(markerLayer);
        markerLayer = L.featureGroup(); 
        const locations = body.locations;
        locations.forEach(location => {
            let circle = L.circle([location.latitude, location.longitude], {
                color: calculateColour(location.accuracy),
                fillColor: calculateColour(location.accuracy),
                fillOpacity: 0.5,
                radius: location.accuracy
            }).addTo(markerLayer);
            circle.bindPopup(`Accuracy: ${location.accuracy}<br>Time: ${location.time}`);
            console.log(`Added a circle at ${location.latitude} ${location.longitude}`);
            mean.innerText = Number(body.stats.mean).toFixed(3);
            min.innerText = Number(body.stats.min).toFixed(3);
            max.innerText = Number(body.stats.max).toFixed(3);
        })
        map.addLayer(markerLayer);
    });
}

getLocations("/all_locations");
