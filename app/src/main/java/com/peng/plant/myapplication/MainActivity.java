package com.peng.plant.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapView.MapViewEventListener {

    private Button zoombtn1, zoombtn2, zoombtn3, zoombtn4, zoombtn5, location_btn,my_direction;
    private double latitude, longitude;
    private boolean location_controll ,direction_controll;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        direction_controll = false;
        location_controll = true;
        zoombtn1 = findViewById(R.id.zoomlevel1);
        zoombtn2 = findViewById(R.id.zoomlevel2);
        zoombtn3 = findViewById(R.id.zoomlevel3);
        zoombtn4 = findViewById(R.id.zoomlevel4);
        zoombtn5 = findViewById(R.id.zoomlevel5);
        location_btn = findViewById(R.id.my_location);
        my_direction = findViewById(R.id.my_direction);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.51019, 127.03309), 5, true);
        mapViewContainer.addView(mapView);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("(주)와트");
        marker.setTag(0);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(37.43228, 127.17833);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        mapView.addPOIItem(marker);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setMapViewEventListener(this);


        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //트래킹속도가 느리기때문에 마지막에 지정되있던 값으로 지도 이동
                if (latitude > 0 && longitude > 0) {
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
                }
                if (location_controll)
                    Toast.makeText(getApplicationContext(), "현재 위치를 갱신중입니다", Toast.LENGTH_SHORT).show();
                permission_location(1);
                direction_controll = true;
            }
        });

        my_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(direction_controll) {
                 permission_location(2);
                 direction_controll=false;
             }
            }
        });


        zoombtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(5, true);
//                MapPoint mapPoint12 = MapPoint.mapPointWithWCONGCoord( 1, 2);
//                onMapViewCenterPointMoved(mapView,mapPoint12); 내일와서 요부분 테스트

            }
        });

        zoombtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(4, true);
            }
        });
        zoombtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(3, true);
            }
        });
        zoombtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(2, true);
            }
        });

        zoombtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(1, true);
            }
        });



//        getAppKeyHash(); // 키해쉬값 가져오기
    }

    private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        Log.d("댕댕개발자", "눌렀다");
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Log.d("댕댕개발자", "눌렀다2");

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        Log.d("댕댕개발자", "눌렀다3");
        Intent intent = new Intent(MainActivity.this, imageView.class);
        startActivity(intent);

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
        Log.d("댕댕개발자", "눌렀다4");

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        //이코드 부분 다시 살펴보기
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        latitude = mapPointGeo.latitude;
        longitude = mapPointGeo.longitude;

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }


    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        Log.d("맵포인트값", "onMapViewCenterPointMoved: mapPoint" + mapPoint);
        Log.d("lolgo", "onMapViewDragStarted1: mapPoint.getMapPointScreenLocation().x 2: "+ mapPoint.getMapPointCONGCoord().x);
        Log.d("lolgo", "onMapViewDragStarted1: mapPoint.getMapPointScreenLocation().y 3 : "+ mapPoint.getMapPointCONGCoord().y);

        if (location_controll == false) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            location_controll = true;

        }

    }

    //줌레벨 변경시 로그 띄워주기 / 현재레벨
    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        MapPoint.mapPointWithWCONGCoord( mapPoint.getMapPointScreenLocation().x, mapPoint.getMapPointScreenLocation().y);
        Log.d("lolgo", "onMapViewDragStarted: mapPoint.getMapPointScreenLocation().x : "+ mapPoint.getMapPointScreenLocation().x);
        Log.d("lolgo", "onMapViewDragStarted: mapPoint.getMapPointScreenLocation().y : "+ mapPoint.getMapPointScreenLocation().x);

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }




    private void permission_location(int location) {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) { //포그라운드 위치 권한 확인

            //위치 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);


        if (permissionCheck == PackageManager.PERMISSION_DENIED) { //백그라운드 위치 권한 확인

            //위치 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 0);
        }
        if(location==1){
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        }
        else{
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        }
        location_controll = false;
    }
}