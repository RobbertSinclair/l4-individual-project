from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.forms.models import model_to_dict
from map_app.models import GpsLocation
import json
from datetime import datetime

# Create your views here.

def get_locations_dict(locations):
    location_list = []
    for location in locations:
        location_dict = model_to_dict(location)
        location_dict["time"] = location_dict["time"].strftime("%H:%M:%S")
        location_list.append(location_dict)
    return {"locations": location_list}

def main_view(request):
    return HttpResponse("MAP APP")

def all_locations(request):
    locations = GpsLocation.objects.all()
    response_dict = get_locations_dict(locations)
    return JsonResponse(response_dict)

def locations_in_time(request, start_time, end_time):
    start = datetime.strptime(start_time, '%H:%M').time()
    end = datetime.strptime(end_time, '%H:%M').time()
    locations = GpsLocation.objects.filter(time__range=(start, end))
    response_dict = get_locations_dict(locations)
    return JsonResponse(response_dict)