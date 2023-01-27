import matplotlib.pyplot as plt
import pandas as pd
import os
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

for document in data:
    with open(f"{path}/{document['_id']}.json", "w") as f:
        f.write(str(document))

    for player in document["players"]:
        player_dict = document["players"][player]
        location_times = [(2*i) for i in range(len(document["players"][player]["locations"]))]
        try:
            accuracies = [location["accuracy"] for location in document["players"][player]["locations"]]
            print(accuracies)
            plt.plot(location_times, accuracies)
            print(player_dict.keys())
            try:
                plt.title(f"Location Accuracies for Player {player} with Phone Brand={player_dict['brand']}, model={player_dict['model']}, product={player_dict['product']}")
            except KeyError:
                plt.title(f"Location Accuracies for Player {player}")
            plt.title(f"Location Accuracies for Player {player}")
            plt.xlabel("Time")
            plt.ylabel("Accuracy in meters")
            plt.show()
        except KeyError:
            print("No accuracies found")