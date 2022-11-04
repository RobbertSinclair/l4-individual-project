import pytz
from django.db import models
from datetime import datetime, timezone

class GpsLocation(models.Model):

    longitude = models.FloatField(blank=False)
    latitude = models.FloatField(blank=False)
    accuracy = models.FloatField(blank=False)
    time = models.TimeField(blank=True)

    def save(self, *args, **kwargs):
        self.time = datetime.now(pytz.timezone('Europe/London'))
        super(GpsLocation, self).save(*args, kwargs)
        

