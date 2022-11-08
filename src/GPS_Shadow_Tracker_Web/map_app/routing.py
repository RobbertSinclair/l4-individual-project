from django.urls import re_path

from . import consumers

websocket_urlpatterns = [
    re_path(r'game_room/(?P<room_name>\w+)/$', consumers.GameConsumer.as_asgi())
]