/*  Copyright (C) 2016-2024 Andreas Shimokawa, Daniele Gobbetti, Ganblejs

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>. */
package nodomain.freeyourgadget.gadgetbridge.externalevents;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import net.e175.klaus.solarpositioning.DeltaT;
import net.e175.klaus.solarpositioning.SPA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import nodomain.freeyourgadget.gadgetbridge.BuildConfig;
import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.model.CalendarEventSpec;
import nodomain.freeyourgadget.gadgetbridge.util.GBPrefs;
import nodomain.freeyourgadget.gadgetbridge.util.PendingIntentUtils;

public class AlarmReceiver extends BroadcastReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmReceiver.class);

    public AlarmReceiver() {
        Context context = GBApplication.getContext();
        Intent intent = new Intent("DAILY_ALARM");
        intent.setPackage(BuildConfig.APPLICATION_ID);
        PendingIntent pendingIntent = PendingIntentUtils.getBroadcast(context, 0, intent, 0, false);
        AlarmManager am = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));

        if (am != null) {
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 10000, AlarmManager.INTERVAL_DAY, pendingIntent);
        }
        else {
            LOG.warn("could not get alarm manager!");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!GBApplication.getPrefs().getBoolean("send_sunrise_sunset", false)) {
            LOG.info("won't send sunrise and sunset events (disabled in preferences)");
            return;
        }
        LOG.info("will resend sunrise and sunset events");

        final GregorianCalendar dateTimeTomorrow = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        dateTimeTomorrow.set(Calendar.HOUR, 0);
        dateTimeTomorrow.set(Calendar.MINUTE, 0);
        dateTimeTomorrow.set(Calendar.SECOND, 0);
        dateTimeTomorrow.set(Calendar.MILLISECOND, 0);
        dateTimeTomorrow.add(GregorianCalendar.DAY_OF_MONTH, 1);

        /*
         * rotate ids ud reuse the id from two days ago for tomorrow, this way we will have
         * sunrise /sunset for 3 days while sending only sunrise/sunset per day
         */
        byte id_tomorrow = (byte) ((dateTimeTomorrow.getTimeInMillis() / (1000L * 60L * 60L * 24L)) % 3);

        GBApplication.deviceService().onDeleteCalendarEvent(CalendarEventSpec.TYPE_SUNRISE, id_tomorrow);
        GBApplication.deviceService().onDeleteCalendarEvent(CalendarEventSpec.TYPE_SUNSET, id_tomorrow);

        GBPrefs gbPrefs = GBApplication.getPrefs();
        float[] longlat = gbPrefs.getLongLat(context);
        float longitude = longlat[0];
        float latitude = longlat[1];

        GregorianCalendar[] sunriseTransitSetTomorrow = SPA.calculateSunriseTransitSet(dateTimeTomorrow, latitude, longitude, DeltaT.estimate(dateTimeTomorrow));

        CalendarEventSpec calendarEventSpec = new CalendarEventSpec();
        calendarEventSpec.durationInSeconds = 0;
        calendarEventSpec.description = null;

        calendarEventSpec.type = CalendarEventSpec.TYPE_SUNRISE;
        calendarEventSpec.title = "Sunrise";
        if (sunriseTransitSetTomorrow[0] != null) {
            calendarEventSpec.id = id_tomorrow;
            calendarEventSpec.timestamp = (int) (sunriseTransitSetTomorrow[0].getTimeInMillis() / 1000);
            GBApplication.deviceService().onAddCalendarEvent(calendarEventSpec);
        }

        calendarEventSpec.type = CalendarEventSpec.TYPE_SUNSET;
        calendarEventSpec.title = "Sunset";
        if (sunriseTransitSetTomorrow[2] != null) {
            calendarEventSpec.id = id_tomorrow;
            calendarEventSpec.timestamp = (int) (sunriseTransitSetTomorrow[2].getTimeInMillis() / 1000);
            GBApplication.deviceService().onAddCalendarEvent(calendarEventSpec);
        }
    }
}
