import matplotlib.pyplot as plt
import pandas as pd
import os
import pymongo

plt.figure(1)
plt.title("Fused Location Client API Accuracy Distributions")
plt.xlabel("Accuracy in meters")
plt.ylabel("Probability Density")

fused_data = pd.read_csv("./fusedLocationData.csv")
plt.hist(fused_data["accuracy"], log=True)

plt.savefig("./fused_histogram.png")

plt.figure(2)
python_anywhere_data = pd.read_csv("./pythonAnywhere.csv")
plt.title("PythonAnywhere Locations Accuracy Distributions")
plt.xlabel("Accuracy in meters")
plt.ylabel("Probability Density")
plt.hist(python_anywhere_data["accuracy"], log=True)

plt.savefig("./python_anywhere_histogram.png")

client = pymongo.MongoClient(f"mongodb://{os.environ['MONGO_USER']}:{os.environ['MONGO_PASSWORD']}@{ os.environ['MONGO_HOST']}:{os.environ['MONGO_PORT']}")
mydb = client["gpsGame"]
collection = mydb["gpsShadows"]
cursor = collection.find({})
data = list(cursor)
pd_df = pd.DataFrame(data)
print(pd_df["accuracy"])
plt.figure(3)
plt.title("Location Accuracy Distribution on Current MongoDB server")
plt.xlabel("Accuracy in meters")
plt.ylabel("Probability Density")
plt.hist(pd_df["accuracy"], log=True)

plt.savefig("./mongo_db_histogram.png")