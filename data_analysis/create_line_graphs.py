import matplotlib.pyplot as plt
import pandas as pd
import os
import json
import pymongo

def createDirectory(path):
    try:
        os.mkdir(path)
    except FileExistsError:
        print("Directory Exists")

client = pymongo.MongoClient(f"mongodb://{os.environ['MONGO_USER']}:{os.environ['MONGO_PASSWORD']}@{ os.environ['MONGO_HOST']}:{os.environ['MONGO_PORT']}")
db = client["gpsGame"]
collection = db["games"]
cursor = collection.find({})
data = list(cursor)
path = "../../pastGames"
accuracy_path = "./accuracy_graphs"
ratio_path = "./catchRadius"
createDirectory(path)
createDirectory(accuracy_path)
createDirectory(ratio_path)
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

phase2_games = collection.find({"phase": {"$ne": 1}})
CATCH_RADIUS = 5
for document in phase2_games:
    for player in document["players"]:
        player_dict = document["players"][player]
        location_times = [(2*i) for i in range(len(player_dict["locations"]))]
        try:
            catch_radius = [CATCH_RADIUS / location["noiseRatio"] for location in player_dict["locations"]]
            print(catch_radius)
            plt.figure(counter)
            try:
                plt.title(f"Catch Radius for Player {player} with Phone\nBrand={player_dict['brand']}, model={player_dict['model']}, product={player_dict['product']}")
            except KeyError as e:
                print(e)
                print("There was no brand")
                plt.title(f"Catch Radius for Player {player}")
            plt.plot(location_times, catch_radius)
            plt.xlabel("Time")
            plt.ylabel("Catch Radius in meters")
            
            plt.savefig(f"{ratio_path}/{player_dict['brand']}-{player_dict['model']}-{counter}-radius.pdf")
            counter += 1
            ratios = [location["noiseRatio"] for location in player_dict["locations"]]
            plt.figure(counter)
            try:
                plt.title(f"Noise Ratio for Player {player} with Phone\nBrand={player_dict['brand']}, model={player_dict['model']}, product={player_dict['product']}")
            except KeyError as e:
                print(e)
                print("There was no brand")
            plt.plot(location_times, ratios)
            plt.xlabel("Time")
            plt.ylabel("Noise Ratio")

            plt.savefig(f"{ratio_path}/{player_dict['brand']}-{player_dict['model']}-{counter}-noiseRatio.pdf")
            counter += 1

                
            
        except KeyError:
            print("Could not find ratio")
        