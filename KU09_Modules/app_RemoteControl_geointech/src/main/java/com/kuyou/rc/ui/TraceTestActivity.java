package com.kuyou.rc.ui;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.kuyou.rc.R;
import com.kuyou.rc.basic.location.filter.FilterManager;
import com.kuyou.rc.basic.location.filter.IFilterCallBack;
import com.kuyou.rc.basic.location.provider.HMLocationProvider;
import com.kuyou.rc.basic.location.provider.NormalFilterLocationProvider;

import java.util.Timer;

public class TraceTestActivity extends Activity {

    protected final static String TAG = "com.kuyou.rc.handler.location.trace > TraceTestActivity";
    private int posCount = 0;

    private final Timer timer = new Timer();

    private MapView mMapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_simple);
        init(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        timer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    private PolylineOptions mPolyoptions;
    private int tracesize = 30;
    private Polyline mpolyline;

    private void init(Bundle savedInstanceState) {
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();

        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
        mPolyoptions.color(Color.GRAY);

        LatLng mylocation = new LatLng(22.550020545869813, 113.90872557625059);
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mylocation, 18, 30, 0));
        aMap.moveCamera(mCameraUpdate);
        aMap.invalidate();
    }

    public void onLocationChange(Location location) {
        Log.d(TAG, "onLocationChange > ");
        if (null == aMap || !isDraw) {
            return;
        }
        addMarker(location);
        addPolylineBase(location);
        mNormalFilterLocationProvider.dispatchLocation(location);

//        //record.addpoint(amapLocation);
//        mPolyoptions.add(mylocation);
//        mTracelocationlist.add(parseTraceLocation(location));
//        redrawline();
//        if (mTracelocationlist.size() > tracesize - 1) {
//            trace();
//        }
    }

    MarkerOptions mMarkerOptions;
    long mMarkerIndex = 0;

    private void addMarker(Location location) {
        if (null == mMarkerOptions) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(this.getResources(),
                            R.drawable.marker_blue));
            mMarkerOptions = new MarkerOptions();
            mMarkerOptions.icon(icon);
            mMarkerOptions.anchor(0.1f, 0.1f);

        }

        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        mMarkerOptions.position(latLng);
        mMarkerOptions.title(String.valueOf(mMarkerIndex++));
        aMap.addMarker(mMarkerOptions);
    }

    private LatLng mLatLngOld;

    private void addPolylineBase(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        if (null == mLatLngOld) {
            mLatLngOld = latLng;
            return;
        }
        aMap.addPolyline((new PolylineOptions()).add(mLatLngOld, latLng).color(Color.RED));
        mLatLngOld = latLng;
    }

    /**
     * 实时轨迹画线
     */
    private void redrawline() {
        if (mPolyoptions.getPoints().size() > 1) {
            if (mpolyline != null) {
                mpolyline.setPoints(mPolyoptions.getPoints());
            } else {
                mpolyline = aMap.addPolyline(mPolyoptions);
            }
        }
    }

    private boolean isDraw = false;

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_bt:
                isDraw = true;
                //initFilter();
                break;
            case R.id.stop_bt:
                isDraw = false;
                break;
            default:
                break;
        }
    }


    NormalFilterLocationProvider mNormalFilterLocationProvider;
    private LatLng mLatLngFilterOld;

    private void initFilter() {
        if (null != mNormalFilterLocationProvider)
            return;
        mNormalFilterLocationProvider = NormalFilterLocationProvider.getInstance(getApplicationContext());
        mNormalFilterLocationProvider.setFilter(new FilterManager.IFilterPolicyCallBack() {
            @Override
            public int getFilterPolicy() {
                int policy = 0;
                policy |= IFilterCallBack.POLICY_FILTER_FLUCTUATION;
                //policy |= IFilterCallBack.POLICY_FILTER_KALMAN;
                return policy;
            }
        });
        mNormalFilterLocationProvider.setLocationChangeListener(new HMLocationProvider.IOnLocationChangeListener() {
            @Override
            public void onLocationChange(Location location) {
                TraceTestActivity.this.addPolylineFilter(location);
            }
        });
    }

    private void addPolylineFilter(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        if (null == mLatLngFilterOld) {
            mLatLngFilterOld = latLng;
            return;
        }
        aMap.addPolyline((new PolylineOptions()).add(mLatLngFilterOld, latLng).color(Color.YELLOW));
        mLatLngFilterOld = latLng;
    }

}
