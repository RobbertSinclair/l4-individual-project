import matplotlib.pyplot as plt
import pandas as pd
import os
import json
import pymongo

client = pymongo.MongoClient(f"mongodb://{os.environ['MONGO_USER']}:{os.environ['MONGO_PASSWORD']}@{ os.environ['MONGO_HOST']}:{os.environ['MONGO_PORT']}")
db = client["gpsGame"]
collection = db["games"]
print("Getting the Phase 1 Games")
phase1_games = list(collection.find({"phase": {"$eq": 1}}))
print("Phase 1 Games Received")
print("Getting the Phase 2 Games")
phase2_games = list(collection.find({"phase": {"$ne": 1}}))
print("Phase 2 Games Received")

phase1_shadow_threshold = 6

products = ["SM-G990B", "CPH2219", "SM-N960F"]
phase1_players = {}
phase2_players = {}
print("Creating a player dictionary for Phase 1")
for game in phase1_games:
    for player in game["players"]:
        try:
            product = game["players"][player]["product"]
            if product in products and len(game["players"]) > 1:
                phase1_players[product] = game["players"][player]
        except:
            continue
print("Creating a player dictionary for Phase 2")
for game in phase2_games:
    for player in game["players"]:
        try:
            product = game["players"][player]["product"]
            if product in products:
                phase2_players[product] = game["players"][player]
        except:
            continue


print("Getting the accuracy scores from each accuracy")
labels = ["In Open", "In Shadow"]
phase1_model_pies = {}
phase2_model_pies = {}
for key in phase1_players.keys():
    phase1_accuracy = [location["accuracy"] for location in phase1_players[key]["locations"]]
    phase1_pie_data = [len([accuracy for accuracy in phase1_accuracy if accuracy < 6]),
        len([accuracy for accuracy in phase1_accuracy if accuracy >= 6])
    ]
    phase1_model_pies[key] = phase1_pie_data
    phase2_accuracy = [location["accuracy"] for location in phase2_players[key]["locations"]]
    phase2_min_accuracies = [location["minAccuracy"] for location in phase2_players[key]["locations"]]
    phase2_model_pies[key] = [0, 0]
    for i in range(len(phase2_accuracy)):
        if phase2_accuracy[i] < phase2_min_accuracies[i] * 2:
            phase2_model_pies[key][0] += 1
        else:
            phase2_model_pies[key][1] += 1
    plt.figure()
    plt.suptitle(f"Times in Shadow and in the open. Phone Product: {key}")

    plt.subplot(1, 2, 1)
    plt.title("Phase 1")
    plt.pie(phase1_model_pies[key], labels=labels, autopct='%1.1f%%')
    plt.subplot(1, 2, 2)
    plt.title("Phase 2")
    plt.pie(phase2_model_pies[key], labels=labels, autopct='%1.1f%%')
    plt.savefig(f"../dissertation/images/{key}_piechart.pdf")
    




