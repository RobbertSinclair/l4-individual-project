import matplotlib.pyplot as plt
import pandas as pd
import os
import json
import pymongo

client = pymongo.MongoClient(f"mongodb://{os.environ['MONGO_USER']}:{os.environ['MONGO_PASSWORD']}@{ os.environ['MONGO_HOST']}:{os.environ['MONGO_PORT']}")
db = client["gpsGame"]
collection = db["games"]
cursor = collection.find({})
data = list(cursor)
path = "../../pastGames"
accuracy_path = "./accuracy_graphs"
try:
    os.mkdir(path)
except FileExistsError:
    print("Directory exists")
try:
    os.mkdir(accuracy_path)
except FileExistsError:
    print("Directory exists")
counter = 0
for document in data:
    with open(f"{path}/{document['_id']}.json", "w") as f:
        document["_id"] = str(document["_id"])
        f.write(json.dumps(document))
    
    for player in document["players"]:
        player_dict = document["players"][player]
        location_times = [(2*i) for i in range(len(document["players"][player]["locations"]))]
        try:
            accuracies = [location["accuracy"] for location in document["players"][player]["locations"]]
            print(accuracies)
            plt.figure(counter)
            plt.plot(location_times, accuracies)
            print(player_dict.keys())
            try:
                plt.title(f"Location Accuracies for Player {player} with Phone\nBrand={player_dict['brand']}, model={player_dict['model']}, product={player_dict['product']}")
            except KeyError as e:
                print(e)
                plt.title(f"Location Accuracies for Player {player}")
            plt.xlabel("Time")
            plt.ylabel("Accuracy in meters")
            plt.savefig(f"{accuracy_path}/{counter}.png")
            counter += 1
        except KeyError:
            print("No accuracies found")