package kuyou.common.ku09.event.rc;

import android.location.Location;

import kuyou.common.ipc.RemoteEvent;
import kuyou.common.ku09.event.rc.base.EventRemoteControl;

/**
 * action :事件[设备位置变化]
 * <p>
 * remarks:  <br/>
 * author: wuguoxian <br/>
 * date: 21-3-29 <br/>
 * </p>
 */
public class EventLocationChange extends EventRemoteControl {

    protected static final String KEY_LONGITUDE = "event.data.Longitude";
    protected static final String KEY_LATITUDE = "event.data.Latitude";

    @Override
    public int getCode() {
        return Code.LOCATION_CHANGE;
    }

    public EventLocationChange setLongitude(double val) {
        getData().putDouble(KEY_LONGITUDE, val);
        return EventLocationChange.this;
    }

    public EventLocationChange setLatitude(double val) {
        getData().putDouble(KEY_LATITUDE, val);
        return EventLocationChange.this;
    }

    public EventLocationChange setLocation(Location location) {
        setLongitude(location.getLongitude());
        setLatitude(location.getLatitude());
        return EventLocationChange.this;
    }

    public static Location getLocation(RemoteEvent event) {
        Location location = new Location("gps");
        location.setLongitude(event.getData().getDouble(KEY_LONGITUDE));
        location.setLatitude(event.getData().getDouble(KEY_LATITUDE));
        return location;
    }
}
