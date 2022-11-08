import asyncio
import json
from channels.generic.websocket import WebsocketConsumer
from asgiref.sync import async_to_sync

class GameConsumer(WebsocketConsumer):

    def connect(self):
        self.room_name = self.scope["url_route"]["kwargs"]["room_name"]
        self.room_group_name = self.room_name

        async_to_sync(self.channel_layer.group_add)(
            self.room_group_name, self.channel_name
        )

        self.accept()
        self.send(text_data=json.dumps({
            "type": "connection_received",
            "message": "You are connected to the socket"
        }))

    def disconnect(self, event):
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name, self.channel_name
            )