/*  Copyright (C) 2022-2024 Daniel Dakhno

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
package nodomain.freeyourgadget.gadgetbridge.service.devices.binary_sensor.protocol.constants;

public enum ReportState {
    REPORT_STATUS_DISABLED,
    REPORT_STATUS_ENABLED;

    public byte getReportStateByte(){
        return (byte) ordinal();
    }

    public static ReportState fromReportStateByte(byte reportState){
        for(ReportState value:ReportState.values()){
            if(value.getReportStateByte() == reportState){
                return value;
            }
        }
        return null;
    }
}
