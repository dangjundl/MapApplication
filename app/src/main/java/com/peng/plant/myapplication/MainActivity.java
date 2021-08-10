package com.peng.plant.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.location.Location;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import net.daum.mf.map.api.CameraUpdate;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapView.MapViewEventListener ,TiltScrollController.ScrollListener {

    private Button zoombtn1, zoombtn2, zoombtn3, zoombtn4, zoombtn5, location_btn,my_direction;
    private double latitude, longitude;
    private boolean location_controll ,direction_controll;
    private MapView mapView;
    private TiltScrollController mTiltScrollController;
    private MapPoint test,test2;
    private double x_location, y_location;
    private MapPOIItem[] poiItems;
    double a,b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission_check();

        mTiltScrollController = new TiltScrollController(getApplicationContext(), this);

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
        test2 = MapPoint.mapPointWithGeoCoord(37.51019, 127.03309);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapView.setMapCenterPointAndZoomLevel(test2, 5, true);
        mapViewContainer.addView(mapView);
        poiItems = new MapPOIItem[2];

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("(주)와트");
        marker.setTag(1);
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(37.43228, 127.17833);
        marker.setMapPoint(mapPoint);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        poiItems[0]=marker;
        
        MapPOIItem marker2 = new MapPOIItem();
        marker2.setItemName("서울");
        marker2.setTag(2);
        MapPoint mapPoint2 = MapPoint.mapPointWithGeoCoord(37.46541, 127.17767);
        marker2.setMapPoint(mapPoint2);
        marker2.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker2.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        poiItems[1]=marker2;
        mapView.addPOIItems(poiItems);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setMapViewEventListener(this);
//        mapView.releaseUnusedMapTileImageResources();
        //지도 타일 이미지 캐쉬 데이터 들을 삭제하여 메모리를 확보할 수 있다.
        mapView.getMapCenterPoint();

         a = poiItems[1].getMapPoint().getMapPointGeoCoord().latitude;
         b = poiItems[1].getMapPoint().getMapPointGeoCoord().longitude;

//        Location mylocation = new Location("");


        Log.d("거리값을 알려달라!!!", "a: " + a);
        Log.d("거리값을 알려달라!!!", "b: " + b);

        double ab = getDistance(latitude, longitude, a, b);
            Log.d("거리값을 알려달라!!!", "onCreate: " + ab);



        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //트래킹속도가 느리기때문에 마지막에 지정되있던 값으로 지도 이동
                if (latitude > 0 && longitude > 0) {
                    mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
                }
                if (location_controll)
                    Toast.makeText(getApplicationContext(), "현재 위치를 갱신중입니다", Toast.LENGTH_SHORT).show();
                     my_location(1);
                direction_controll = true;
            }
        });
        my_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(direction_controll) {
                    my_location(2);
                 direction_controll=false;
             }
            }
        });

        zoombtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(5, true);
                Toast.makeText(getApplicationContext(), "level 1", Toast.LENGTH_SHORT).show();

//                MapPoint mapPoint12 = MapPoint.mapPointWithWCONGCoord( 1, 2);
//                onMapViewCenterPointMoved(mapView,mapPoint12); 내일와서 요부분 테스트

            }
        });
        zoombtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(4, true);
                Toast.makeText(getApplicationContext(), "level 2", Toast.LENGTH_SHORT).show();

            }
        });
        zoombtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(3, true);
                Toast.makeText(getApplicationContext(), "level 3", Toast.LENGTH_SHORT).show();

            }
        });
        zoombtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(2, true);
                Toast.makeText(getApplicationContext(), "level 4", Toast.LENGTH_SHORT).show();

            }
        });
        zoombtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(1, true);
                Toast.makeText(getApplicationContext(), "level 5", Toast.LENGTH_SHORT).show();

            }
        });

    }

    //사용자가 MapView 에 등록된 POI Item 아이콘(마커)를 터치한 경우 호출된다.
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        Log.d("댕댕개발자", "마커 클릭");
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {
        Log.d("댕댕개발자", "눌렀다2");

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

        if(mapPOIItem.getItemName().equals(poiItems[0].getItemName())) {
            Intent intent = new Intent(MainActivity.this, imageView.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        //이코드 부분 다시 살펴보기

        Log.d("플롯값", "onCurrentLocationUpdate: v값" + v);
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        latitude = mapPointGeo.latitude;
        longitude = mapPointGeo.longitude;

    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {}
    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {}
    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {}
    @Override
    public void onMapViewInitialized(MapView mapView) {}


    //지도 중심 좌표가 이동한 경우 호출된다.
    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

        if (location_controll == false) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            location_controll = true;
            direction_controll=false;

        }
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {}
    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    //사용자가 지도 드래그를 시작한 경우 호출된다.
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
//        MapPoint.mapPointWithWCONGCoord( mapPoint.getMapPointScreenLocation().x, mapPoint.getMapPointScreenLocation().y);
//        Log.d("두개 longitude 값", "longitude: "+ mapPoint.getMapPointGeoCoord().longitude);
//        Log.d("두개 latitude 값", "latitude : "+ mapPoint.getMapPointGeoCoord().latitude);
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    /**********************************************************
     화면 이동을 외치면 센서값을 주고 트래킹 off
     화면 고정을 외치면 센서값을 닫고 (boolean으로 제어) 트래킹 on
     목요일 테스트기기 받으면 테스트기기에 맞게 센서값 변동
     불필요한 리스너 , 메소드 삭제
     **********************************************************/

    @Override
    public void onTilt(float x, float y) {

        x_location = x/1000;
        y_location = y/1000;

      double map =  mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
      double maps =  mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;

      mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(map-y_location, maps+x_location),true);
//        Log.d("dsds", "onTilt: map : "+map);
        Log.d("dsds", "onTilt: map : "+maps);

      //        mapView.setMapCenterPoint();
        Log.d("x의값", "onTilt: x값 :"+x);
        Log.d("y의값", "onTilt: y값 : "+y);




    }

    private void my_location(int location) {
        if(location==1){
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            Log.d("오류?", "permission_location: 잘되는데?");
        }
        else{
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        }
        location_controll = false;
    }

    private void permission_check(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) { //포그라운드 위치 권한 확인
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        if (permissionCheck  == PackageManager.PERMISSION_DENIED) { //백그라운드 위치 권한 확인
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 0);
        }
    }

    //위도와 경도로 거리값 계산
    public double getDistance(double lat1 , double lng1 , double lat2 , double lng2 ){
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

}


/*******************************************************************************************************************************************************

 카카오맵 API에 사용할 해쉬 키값 가져오기
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

*******************************************************************************************************************************************************/
