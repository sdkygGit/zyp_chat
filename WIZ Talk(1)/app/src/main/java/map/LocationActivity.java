package map;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.wiz.dev.wiztalk.R;

//缩放按钮，AMap.moveCamera(CameraUpdateFactory. zoomIn())可以实现地图级别增大1级。缩小一级看下zoomOut()方法。
public class LocationActivity extends Activity implements AMapLocationListener,
        LocationSource, OnPoiSearchListener, OnMarkerDragListener {

    // 声明mLocationOption对象
    AMapLocationClientOption mLocationOption = null;

    private MapView mapView;
    private AMapLocationClient mlocationClient;
    private AMap aMap;

    private Query query;

    private ListView lvAroundPoi;
    private List<PoiItem> aroundPoiList;
    private AroundPoiAdapter mAroundPoiAdapter;
    private PoiItem selectPoiInfo;

    private boolean isDWing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mapview_location_poi);
        mapView = (MapView) findViewById(R.id.mMapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        initAmap();
        Intent intent = getIntent();
        if (!TextUtils.isEmpty(intent.getStringExtra("latitude"))
                && !TextUtils.isEmpty(intent.getStringExtra("longitude"))) {
            double latitude = Double.parseDouble(intent
                    .getStringExtra("latitude"));
            double longitude = Double.parseDouble(intent
                    .getStringExtra("longitude"));

            LatLng center = new LatLng(
                    latitude, longitude);
            aMap.animateCamera(CameraUpdateFactory.changeLatLng(center));

            findViewById(R.id.lvPoiList).setVisibility(View.GONE);
            findViewById(R.id.ok).setVisibility(View.GONE);
            aMap.addMarker(new MarkerOptions()
                    .position(center)
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.drawable.point))).anchor(0.5f, 0.5f));

        } else {
            // 开启定位
            initMapLocation();
        }
        initSearch();
    }

    private void initSearch() {
        query = new Query("", "", "");
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
    }

    boolean isOnce = true;

    Marker centerMarker;

    void initAmap() {
        aMap = mapView.getMap();
        aMap.setOnMarkerDragListener(this);
        aMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(arg0));
            }
        });
        aMap.setOnMapLoadedListener(new OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {

            }
        });
        aMap.setOnCameraChangeListener(new OnCameraChangeListener() {

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                LatLng mLatLng = cameraPosition.target;
                poiSearch(mLatLng, 0);

                centerMarker = aMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.icon_gcoding))));
                Point point = aMap.getProjection().toScreenLocation(mLatLng);

                centerMarker.setPositionByPixels(point.x, point.y);
                centerMarker.showInfoWindow();// 设置默认显示一个infowinfow
            }

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }
        });
    }

    public void onLocation(View v) {

        dw();
    }

    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.ok:
                if (selectPoiInfo != null) {
                    Intent intent = new Intent();
                    LocationBean locationBean = new LocationBean();
                    locationBean.locName = selectPoiInfo.getTitle();
                    locationBean.street = selectPoiInfo.getSnippet();
                    locationBean.latitude = selectPoiInfo.getLatLonPoint()
                            .getLatitude();
                    locationBean.longitude = selectPoiInfo.getLatLonPoint()
                            .getLongitude();
                    intent.putExtra("POS", locationBean);
                    setResult(RESULT_OK, intent);
                }
                finish();
                break;
        }
    }

    void poiSearch(LatLng latLng, int currentPage) {
        query.setPageNum(currentPage);// 设置查第一页
        PoiSearch poiSearch = new PoiSearch(LocationActivity.this, query);
        poiSearch.setBound(new SearchBound(new LatLonPoint(latLng.latitude,
                latLng.longitude), 2000));// 设置周边搜索的中心点以及区域
        poiSearch.setOnPoiSearchListener(this);// 设置数据返回的监听器
        poiSearch.searchPOIAsyn();
    }

    void initMapLocation() {
        lvAroundPoi = (ListView) findViewById(R.id.lvPoiList);
        lvAroundPoi.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                try {
                    for (int i = 0; i < arg0.getCount(); i++) {
                        View view = arg0.getChildAt(i);
                        ImageView tmp = (ImageView) view
                                .findViewById(R.id.ivMLISelected);
                        if (tmp != null)
                            tmp.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                }
                selectPoiInfo = aroundPoiList.get(arg2);

                // 移动至中心点
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(new LatLng(
                        selectPoiInfo.getLatLonPoint().getLatitude(),
                        selectPoiInfo.getLatLonPoint().getLongitude())));

                ImageView select = (ImageView) arg1
                        .findViewById(R.id.ivMLISelected);

                if (select.getVisibility() != View.VISIBLE)
                    select.setVisibility(View.VISIBLE);
                mAroundPoiAdapter.setSelected(arg2);

            }
        });
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示


        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        aMap.getUiSettings().setCompassEnabled(true);// 设置指南针是否可用

        aMap.setOnMarkerClickListener(new OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (arg0 == centerMarker)
                    return true;
                return false;
            }
        });// 设置点击marker事件监听器

        dw();
    }

    private void dw() {
        if (!isDWing) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
            isDWing = true;
        }

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) { // 定位成功
                aMap.clear();
                LatLng center = new LatLng(amapLocation.getLatitude(),
                        amapLocation.getLongitude());

                aMap.addMarker(new MarkerOptions()
                        .position(center)

                        .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.point))).anchor(0.5f, 0.5f));

                aMap.addCircle(new CircleOptions().center(center).radius(50)
                        .strokeColor(Color.BLUE)
                        .fillColor(Color.parseColor("#44FF0000"))
                        .strokeWidth(3));

                // 移动至中心点
                aMap.animateCamera(CameraUpdateFactory.changeLatLng(center));
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;
                centerMarker = null;
            } else {
                // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError",
                        "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
            }
            isDWing = false;
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int rCode) {

    }

    @Override
    public void onPoiSearched(PoiResult result, int rCode) {
        if (rCode == 1000) {
            if (aroundPoiList == null) {
                aroundPoiList = new ArrayList<PoiItem>();
            }
            if (result.getPois() != null && result.getPois().size() > 0) {
                aroundPoiList.clear();
                aroundPoiList.addAll(result.getPois());
                if (mAroundPoiAdapter == null) {
                    mAroundPoiAdapter = new AroundPoiAdapter(LocationActivity.this,
                            aroundPoiList);
                    lvAroundPoi.setAdapter(mAroundPoiAdapter);
                } else {
                    mAroundPoiAdapter.setNewList(result.getPois(), 0);
                }

                if (aroundPoiList != null && aroundPoiList.size() > 0) {
                    selectPoiInfo = aroundPoiList.get(0);
                    centerMarker.setTitle(selectPoiInfo.getTitle());
                    centerMarker.showInfoWindow();
                    lvAroundPoi.setSelection(0);
                }

            } else {
                Toast.makeText(getApplicationContext(), "获取热点失败",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMarkerDrag(Marker arg0) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    }

    @Override
    public void onMarkerDragStart(Marker arg0) {
    }
}
