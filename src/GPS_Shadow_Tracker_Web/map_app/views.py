from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.forms.models import model_to_dict
from map_app.models import GpsLocation
import json
from datetime import datetime
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.decorators import login_required

# Create your views here.

def get_locations_dict(locations):
    location_list = []
    for location in locations:
        location_dict = model_to_dict(location)
        location_dict["time"] = location_dict["time"].strftime("%H:%M:%S")
        location_list.append(location_dict)
    return {"locations": location_list}

@login_required(login_url="/admin/login")
def main_view(request):
    return render(request, "index.html")

@login_required(login_url="/admin/login")
def all_locations(request):
    locations = GpsLocation.objects.all()
    response_dict = get_locations_dict(locations)
    return JsonResponse(response_dict)

@login_required(login_url="/admin/login")
def locations_in_time(request, start_time, end_time):
    start = datetime.strptime(start_time, '%H:%M').time()
    end = datetime.strptime(end_time, '%H:%M').time()
    locations = GpsLocation.objects.filter(time__gte=start, time__lte=end)
    
    response_dict = get_locations_dict(locations)
    return JsonResponse(response_dict)

@csrf_exempt
def submit_location(request):
    if request.method == "POST":
        try:
            data = json.loads(request.body)
            current_locations = GpsLocation.objects.filter(latitude=data["latitude"], longitude=data["longitude"], accuracy=data["accuracy"])
            if len(current_locations) == 0:
                new_location = GpsLocation.objects.create(latitude=data["latitude"], longitude=data["longitude"], accuracy=data["accuracy"])
                new_location.save()
                return JsonResponse({"message": "success"})
            else:
                return JsonResponse({"message": "There is already a record for this location"})
        except:
            return JsonResponse({"message": "error something went wrong"})
    else:
        return HttpResponse("This must be a POST Request")