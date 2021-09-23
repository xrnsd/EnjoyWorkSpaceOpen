package com.kuyou.rc.basic.location.filter.geohash;

import android.location.Location;

import com.kuyou.rc.basic.location.filter.geohash.Coordinates;
import com.kuyou.rc.basic.location.filter.geohash.GeoHash;
import com.kuyou.rc.basic.location.filter.geohash.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class GeoHashRTFilter {
    
    protected final static String TAG = "com.kuyou.rc.location.filter > GeohashRTFilter";

    public static String PROVIDER_NAME = "GeoHashFiltered";

    private double m_distanceGeoFiltered = 0.0;
    private double m_distanceGeoFilteredHP = 0.0;
    private double m_distanceAsIs = 0.0;
    private double m_distanceAsIsHP = 0.0;

    private static final double COORD_NOT_INITIALIZED = 361.0;
    private int ppCompGeoHash = 0;
    private int ppReadGeoHash = 1;

    private long geoHashBuffers[];
    private int pointsInCurrentGeohashCount;

    private GeoPoint currentGeoPoint;
    private GeoPoint lastApprovedGeoPoint;
    private GeoPoint lastGeoPointAsIs;

    private List<Location> m_geoFilteredTrack;
    public List<Location> getGeoFilteredTrack() {
        return m_geoFilteredTrack;
    }


    private boolean isFirstCoordinate = true;
    private int m_geohashPrecision;
    private int m_geohashMinPointCount;

    public GeoHashRTFilter(int geohashPrecision,
                           int geohashMinPointCount) {
        m_geohashPrecision = geohashPrecision;
        m_geohashMinPointCount = geohashMinPointCount;
        m_geoFilteredTrack = new ArrayList<>();
        reset();
    }

    public double getDistanceGeoFiltered() {
        return m_distanceGeoFiltered;
    }
    public double getDistanceGeoFilteredHP() {
        return m_distanceGeoFilteredHP;
    }
    public double getDistanceAsIs() {
        return m_distanceAsIs;
    }
    public double getDistanceAsIsHP() {
        return m_distanceAsIsHP;
    }

    public void reset() {
        m_geoFilteredTrack.clear();
        geoHashBuffers = new long[2];
        pointsInCurrentGeohashCount = 0;
        lastApprovedGeoPoint = new GeoPoint(COORD_NOT_INITIALIZED, COORD_NOT_INITIALIZED);
        currentGeoPoint = new GeoPoint(COORD_NOT_INITIALIZED, COORD_NOT_INITIALIZED);

        lastGeoPointAsIs = new GeoPoint(COORD_NOT_INITIALIZED, COORD_NOT_INITIALIZED);
        m_distanceGeoFilteredHP = m_distanceGeoFiltered = 0.0;
        m_distanceAsIsHP = m_distanceAsIs = 0.0;
        isFirstCoordinate = true;
    }

    private float hpResBuffAsIs[] = new float[3];
    private float hpResBuffGeo[] = new float[3];

    public void filter(Location loc) {

        GeoPoint pi = new GeoPoint(loc.getLatitude(), loc.getLongitude());
        if (isFirstCoordinate) {
            geoHashBuffers[ppCompGeoHash] = GeoHash.encode_u64(pi.Latitude, pi.Longitude, m_geohashPrecision);
            currentGeoPoint.Latitude = pi.Latitude;
            currentGeoPoint.Longitude = pi.Longitude;
            pointsInCurrentGeohashCount = 1;

            isFirstCoordinate = false;
            lastGeoPointAsIs.Latitude = pi.Latitude;
            lastGeoPointAsIs.Longitude = pi.Longitude;
            return;
        }

        m_distanceAsIs += Coordinates.distanceBetween(
                lastGeoPointAsIs.Longitude,
                lastGeoPointAsIs.Latitude,
                pi.Longitude,
                pi.Latitude);

        Location.distanceBetween(
                lastGeoPointAsIs.Latitude,
                lastGeoPointAsIs.Longitude,
                pi.Latitude,
                pi.Longitude,
                hpResBuffAsIs);


        m_distanceAsIsHP += hpResBuffAsIs[0];
        lastGeoPointAsIs.Longitude = loc.getLongitude();
        lastGeoPointAsIs.Latitude = loc.getLatitude();

        geoHashBuffers[ppReadGeoHash] = GeoHash.encode_u64(pi.Latitude, pi.Longitude, m_geohashPrecision);
        if (geoHashBuffers[ppCompGeoHash] != geoHashBuffers[ppReadGeoHash]) {
            if (pointsInCurrentGeohashCount >= m_geohashMinPointCount) {
                currentGeoPoint.Latitude /= pointsInCurrentGeohashCount;
                currentGeoPoint.Longitude /= pointsInCurrentGeohashCount;

                if (lastApprovedGeoPoint.Latitude != COORD_NOT_INITIALIZED) {
                    double dd1 = Coordinates.distanceBetween(
                            lastApprovedGeoPoint.Longitude,
                            lastApprovedGeoPoint.Latitude,
                            currentGeoPoint.Longitude,
                            currentGeoPoint.Latitude);
                    m_distanceGeoFiltered += dd1;
                    Location.distanceBetween(
                            lastApprovedGeoPoint.Latitude,
                            lastApprovedGeoPoint.Longitude,
                            currentGeoPoint.Latitude,
                            currentGeoPoint.Longitude,
                            hpResBuffGeo);
                    double dd2 = hpResBuffGeo[0];
                    m_distanceGeoFilteredHP += dd2;
                }
                lastApprovedGeoPoint.Longitude = currentGeoPoint.Longitude;
                lastApprovedGeoPoint.Latitude = currentGeoPoint.Latitude;
                Location laLoc = new Location(PROVIDER_NAME);
                laLoc.setLatitude(lastApprovedGeoPoint.Latitude);
                laLoc.setLongitude(lastApprovedGeoPoint.Longitude);
                laLoc.setAltitude(loc.getAltitude()); //hack.
                laLoc.setTime(loc.getTime()); //hack2
                m_geoFilteredTrack.add(laLoc);
                currentGeoPoint.Latitude = currentGeoPoint.Longitude = 0.0;
            }

            pointsInCurrentGeohashCount = 1;
            currentGeoPoint.Latitude = pi.Latitude;
            currentGeoPoint.Longitude = pi.Longitude;
            //swap buffers
            int swp = ppCompGeoHash;
            ppCompGeoHash = ppReadGeoHash;
            ppReadGeoHash = swp;
            return;
        }

        currentGeoPoint.Latitude += pi.Latitude;
        currentGeoPoint.Longitude += pi.Longitude;
        ++pointsInCurrentGeohashCount;
    }

    public void stop() {
        if (pointsInCurrentGeohashCount >= m_geohashMinPointCount) {
            currentGeoPoint.Latitude /= pointsInCurrentGeohashCount;
            currentGeoPoint.Longitude /= pointsInCurrentGeohashCount;

            if (lastApprovedGeoPoint.Latitude != COORD_NOT_INITIALIZED) {
                double dd1 = Coordinates.distanceBetween(
                        lastApprovedGeoPoint.Longitude,
                        lastApprovedGeoPoint.Latitude,
                        currentGeoPoint.Longitude,
                        currentGeoPoint.Latitude);
                m_distanceGeoFiltered += dd1;
                Location.distanceBetween(
                        lastApprovedGeoPoint.Latitude,
                        lastApprovedGeoPoint.Longitude,
                        currentGeoPoint.Latitude,
                        currentGeoPoint.Longitude,
                        hpResBuffGeo);
                double dd2 = hpResBuffGeo[0];
                m_distanceGeoFilteredHP += dd2;
            }
            lastApprovedGeoPoint.Longitude = currentGeoPoint.Longitude;
            lastApprovedGeoPoint.Latitude = currentGeoPoint.Latitude;
            Location laLoc = new Location(PROVIDER_NAME);
            laLoc.setLatitude(lastApprovedGeoPoint.Latitude);
            laLoc.setLongitude(lastApprovedGeoPoint.Longitude);
            m_geoFilteredTrack.add(laLoc);
            currentGeoPoint.Latitude = currentGeoPoint.Longitude = 0.0;
        }
    }
}
