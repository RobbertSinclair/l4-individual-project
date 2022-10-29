from django.urls import path
from map_app import views

app_name = "map_app"

urlpatterns = [
    path("", views.main_view, name="main_view"),
    path("all_locations", views.all_locations, name="all_locations"),
    path("locations_time/<str:start_time>/<str:end_time>", views.locations_in_time, name="locations_in_time"),
    path("submit_location", views.submit_location, name="submit_location"),
    path("gps_shadows", views.gps_shadows, name="gps_shadows")
]