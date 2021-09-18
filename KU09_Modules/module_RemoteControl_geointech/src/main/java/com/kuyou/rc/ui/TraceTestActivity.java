package com.kuyou.rc.ui;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.kuyou.rc.R;
import com.kuyou.rc.basic.location.filter.TrackPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * 轨迹纠偏功能 示例 使用起来更简单
 */
public class TraceTestActivity extends Activity implements TraceListener {
    private List<TraceLocation> mListPoint = new ArrayList<>();
    private List<TraceLocation> originPosList;
    private LBSTraceClient lbsTraceClient;

    protected final String TAG = "com.kuyou.rc.handler.location.trace > TraceTestActivity";
    private int posCount = 0;
    private TraceLocation posTraceLocation;

    private final Timer timer = new Timer();

    private MapView mMapView;
    private AMap aMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_simple);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
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

    List<TrackPoint> mTrackPointList = new ArrayList<>();
    final List<LatLng> latLngs2 = new ArrayList<>();

    private void drawTraceLine(List<TrackPoint> list) {
        {
            mTrackPointList.addAll(list);
            for (TrackPoint point : list) {
                latLngs2.add(new LatLng(point.getLatitude(), point.getLongitude()));
            }
            Log.d("Trajectory2", "===================  latLngs2.sze()=" + latLngs2.size());
            if (latLngs2.size() >= 0) {
                final List<LatLng> latLngs = new ArrayList<>();
                latLngs.addAll(latLngs2);
                latLngs2.clear();

                aMap.addPolyline(new PolylineOptions().
                        addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));

                int index = 0;
                MarkerOptions markerOption;
                for (LatLng startLocation_gps : latLngs) {
                    markerOption = new MarkerOptions();
                    markerOption.position(startLocation_gps);
                    markerOption.title(new StringBuilder("点")
                            .append(index).append(":").append(mTrackPointList.get(index).getAngle())
                            .toString());
                    markerOption.draggable(false);//设置Marker可拖动
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.drawable.marker_blue)));
                    // 将Marker设置为贴地显示，可以双指下拉地图查看效果
                    markerOption.setFlat(true);//设置marker平贴地图效果
                    aMap.addMarker(markerOption);
                    index += 1;
                }

                latLngs.clear();
                mTrackPointList.clear();
            }
        }
    }

    public void initView() {
        if (originPosList == null) {
            originPosList = new ArrayList<>();
            LatLng positionLatLng = new LatLng(22.624991f, 113.865306f);
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, 16));
        }
    }

    private PolylineOptions mPolyoptions;
    private List<TraceLocation> mTracelocationlist = new ArrayList<TraceLocation>();
    private int tracesize = 30;
    private Polyline mpolyline;

    private void init() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(10f);
        mPolyoptions.color(Color.GRAY);
    }

    public void onLocationChange(Location location) {
        Log.d(TAG, "onLocationChange > ");
        LatLng mylocation = new LatLng(location.getLatitude(),
                location.getLongitude());
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(mylocation));

        //record.addpoint(amapLocation);
        mPolyoptions.add(mylocation);
        mTracelocationlist.add(parseTraceLocation(location));
        redrawline();
        if (mTracelocationlist.size() > tracesize - 1) {
            trace();
        }
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

    private void trace() {
        List<TraceLocation> locationList = new ArrayList<>(mTracelocationlist);
        LBSTraceClient mTraceClient = new LBSTraceClient(getApplicationContext());
        mTraceClient.queryProcessedTrace(1, locationList, LBSTraceClient.TYPE_AMAP, this);
        TraceLocation lastlocation = mTracelocationlist.get(mTracelocationlist.size() - 1);
        mTracelocationlist.clear();
        mTracelocationlist.add(lastlocation);
    }

    @Override
    public void onRequestFailed(int i, String s) {

    }

    @Override
    public void onTraceProcessing(int i, int i1, List<LatLng> list) {

    }

    @Override
    public void onFinished(int i, List<LatLng> list, int i1, int i2) {

    }

    public static TraceLocation parseTraceLocation(Location amapLocation) {
        TraceLocation location = new TraceLocation();
        location.setBearing(amapLocation.getBearing());
        location.setLatitude(amapLocation.getLatitude());
        location.setLongitude(amapLocation.getLongitude());
        location.setSpeed(amapLocation.getSpeed());
        location.setTime(amapLocation.getTime());
        return location;
    }
}
