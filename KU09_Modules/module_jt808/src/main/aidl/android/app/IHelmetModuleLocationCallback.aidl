package android.app;

import android.location.Location;

interface IHelmetModuleLocationCallback {

    void onLocationChange(in Location location);
}
