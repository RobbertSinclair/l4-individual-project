# Readme

This is a Seamful Game Based on GPS Shadows. It was my fourth year project at the University of Glasgow. The code consists of an Android app in the frontend and a Node.js server with MongoDB in the backend.

To view the frontend code, please enter this directory. GPS_Shadow_Tracker_App/app/src/main/java/com/example/gps_shadow_tracker_app/

The frontend consists of 5 packages all written in Kotlin these include:

* game
* gps
* rest
* ui
* websocket

The main app is run from the MainActivity.kt file which initialises everything

To view the backend please follow the simpler to understand directory of GPS_Shadow_Tracker_Web_Node/

All of the relevant files are in this directory. With the main file being the `index.js` file

## Build instructions

### Android Installation Instructions

To easily test out the project. Please load the APK onto an Android phone ( The app should work with every Android phone with Android 12 and above )

APK link: https://github.com/RobbertSinclair/l4-individual-project/releases/tag/Phase2.1. Please download the file that says app-debug.apk. A copy of the file is
enclosed at the root of this directory

### Building on Android Studio (Not Recommended)

If you wish to build it from Scratch. I recommend loading the GPS_Shadow_Tracker_App directory in Android Studio.

Once you have loaded the app on android studio, you will need to edit the GPS_Shadow_Tracker_APP/gradle/local.properties to include your own Google Maps API
key. Full documentation for how to obtain an API key can be found [here](https://developers.google.com/maps/documentation/javascript/get-api-key)

```
MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
```

### Building the server side

There shouldn't be any need to run the server-side code as the APK communicates with a hosted server. However, if you wish to run the code on your local machine then
start by running `npm install` to install the dependencies.

Next you need to install and set up a MongoDB server. Full instructions on how to do that can be found on the [Official MongoDB documentation](https://www.mongodb.com/docs/atlas/getting-started/?tck=docs_driver_nodejs).

Once you have a MongoDB server, simply create 4 new environmental variables these are:

* MONGO_USER - Your MongoDB username
* MONGO_PASSWORD - Your MongoDB Password
* MONGO_HOST - The Host name for your MongoDB cluster
* MONGO_PORT - The Port number that your MongoDB database is found on

After setting up those environmental variables simply run `node index.js` and the collections will be generated

### Requirements

Android 12 or above
