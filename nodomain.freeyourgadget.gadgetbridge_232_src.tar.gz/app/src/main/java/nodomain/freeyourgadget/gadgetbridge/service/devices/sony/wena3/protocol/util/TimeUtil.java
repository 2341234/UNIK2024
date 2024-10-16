/*  Copyright (C) 2023-2024 akasaka / Genjitsu Labs

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
package nodomain.freeyourgadget.gadgetbridge.service.devices.sony.wena3.protocol.util;

import java.util.Date;

import nodomain.freeyourgadget.gadgetbridge.devices.sony.wena3.SonyWena3Constants;

public class TimeUtil {
    public static int dateToWenaTime(Date date) {
        return (int) ((date.getTime() - SonyWena3Constants.EPOCH_START) / (long)1000);
    }

    public static Date wenaTimeToDate(int wenaTime) {
        return new Date(((long) wenaTime) * 1000L + SonyWena3Constants.EPOCH_START);
    }
}