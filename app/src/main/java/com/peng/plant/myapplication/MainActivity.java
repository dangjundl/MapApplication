package com.peng.plant.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
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
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapView.MapViewEventListener, TiltScrollController.ScrollListener {

    private Button zoombtn1, zoombtn2, zoombtn3, zoombtn4, zoombtn5, display_move, display_stop,release_btn;
    private double latitude, longitude, x_location, y_location;
    private boolean display_controll;
    private MapView mapView;
    private TiltScrollController mTiltScrollController;
    private MapPoint test2;
    private MapPOIItem[] poiItems;
    private ArrayList<String> pic_data, setName;
    private ArrayList<Double> markers;
    private Button distance_test, distance_test2;
    private Boolean distance1, distance2;
    private ArrayList <MapPOIItem> marker;
    private ArrayList <MapPoint> mapPoint;
    private  ArrayList <mapData> mapDatas;
    private  ArrayList <Piclist_data> imglist;
    private int select_circle;
    String[] img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        permission_check();

        /************************************
         findviewbyidId 부분은 nitview 함수로 정리
         ************************************/

        setName = new ArrayList<String>();//마커에 지정할 이름
        pic_data = new ArrayList<String>();//이미지 데이터값
        markers = new ArrayList<Double>();//내위치에서 마커까지의 거리
        mapDatas = new ArrayList<mapData>();
        imglist = new ArrayList<Piclist_data>();

        mTiltScrollController = new TiltScrollController(getApplicationContext(), this);

        distance1 = true;
        distance2 = true;
        display_controll = false;

        zoombtn1 = findViewById(R.id.zoomlevel1);
        zoombtn2 = findViewById(R.id.zoomlevel2);
        zoombtn3 = findViewById(R.id.zoomlevel3);
        zoombtn4 = findViewById(R.id.zoomlevel4);
        zoombtn5 = findViewById(R.id.zoomlevel5);

        display_move = findViewById(R.id.display_move_on);
        display_stop = findViewById(R.id.display_move_off);
        release_btn = findViewById(R.id.display_release);

        distance_test = findViewById(R.id.distance1);
        distance_test2 = findViewById(R.id.distance2);

        //마커 목록
        marker = new ArrayList<MapPOIItem>();
       //마커가 화면에 표시될 좌표 목록
        mapPoint = new ArrayList<MapPoint>();

        //사용자가 입력한 사진데이터 , 이름데이터를 가져와서 각각 setName , pic_data에 넣어줌 -> 요부분은 내생각에 mapData를 이용해야 하지않을까

        setName.add(0, "(주)와트");
        setName.add(1, "서울");
        setName.add(2, "하아하아아");
        
        //사진 데이터
        pic_data.add("https://cdn.kado.net/news/photo/202004/1018454_448598_1539.jpg,http://image.canon-ci.co.kr/pds/editor/images/000062/20180404151051869_70ODYPB7.png,http://image.canon-ci.co.kr/pds/editor/images/000096/20190611140954550_350LWRGN.jpg");
        pic_data.add("https://thumbnews.nateimg.co.kr/view610///news.nateimg.co.kr/orgImg/mt/2021/07/11/mt_1625955064430_659172_0.jpg");
        pic_data.add("https://s3.ap-northeast-2.amazonaws.com/st.dangidata/hobby_conects/data/adm/lecture_manage/curriculum/094107fc03e8452647ded805edb0f8c4.png");

        mapView = new MapView(this);
        test2 = MapPoint.mapPointWithGeoCoord(37.51019, 127.03309);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapView.setMapCenterPointAndZoomLevel(test2, 5, true);
        mapViewContainer.addView(mapView);


        for (int i=0; i<setName.size(); i++){
            marker.add(i,new MapPOIItem());
            mapPoint.add(i,MapPoint.mapPointWithGeoCoord((37.43207-(i*0.01)),((127.17650-(i*0.01)))));
        }

        Log.d("마커사이즈", "onCreate: "+marker.size());

//        mapPoint.add(0,MapPoint.mapPointWithGeoCoord(37.43207,27.17650));
//        MapPoint mapPoints = MapPoint.mapPointWithGeoCoord(37.43207, 127.17650);


//        MapPOIItem marker = new MapPOIItem();
//        marker.setItemName("(주)와트");
//        marker.get(0).setTag(1);
//        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(37.43228, 127.17833);
//        marker.get(0).setMapPoint(mapPoint);
//        marker.get(0).setMarkerType(MapPOIItem.MarkerType.CustomImage);
//        marker.get(0).setCustomImageResourceId(R.drawable.test2);
////        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
//        marker.get(0).setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setMapViewEventListener(this);
        mapView.getMapCenterPoint();
        // mapView.releaseUnusedMapTileImageResources(); 지도 타일 이미지 캐쉬 데이터 들을 삭제하여 메모리를 확보할 수 있다.
        tracking_controll(1);
        drawMarker();


        for(int i=0; i<setName.size(); i++){
            mapData mData = new mapData();
            mData.setName(setName.get(i));
            mData.setImg_path(pic_data.get(i));
            mData.setLatitude((37.43207-(i*0.01)));
            mData.setLongitude((127.17650-(i*0.01)));
            mapDatas.add(mData);
        }

        for(int i=0; i<mapDatas.size(); i++) {
            String piclist = mapDatas.get(i).getImg_path();
            String[] img = piclist.split(",");
            for(int j =0; j<img.length; j++) {
                if (img.length > 1) {
                    Piclist_data mPicdata = new Piclist_data();
                    mPicdata.img_path = img[j];
                    Log.d("호롤롤ㄹ롤", "onCalloutBalloonOfPOIItemTouched: i값 : " + i);
                    Log.d("이미지값", "onCalloutBalloonOfPOIItemTouched: img :" + img[i]);
                    imglist.add(mPicdata);
                }
            }
        }


//        for (int i=0; i<img.length; i++){
//            Log.d("값", "이미지주소 값 : "+ i + img[i]);
//
//        }


        Log.d("사이즈", "onCreate: mapDatasSize : "+mapDatas.size());
        Log.d("사이즈", "onCreate: mapDatasSize1 : "+mapDatas.get(0).getName());
        Log.d("사이즈", "onCreate: mapDatasSize2 : "+mapDatas.get(1).getName());
        Log.d("사이즈", "onCreate: mapDatasSize3 : "+mapDatas.get(2).getName());
        Log.d("ddd", "onCreate: dsds"+mapDatas.get(0).getDistance());



        distance_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude > 0 && longitude > 0 && distance1 &&1<select_circle) {
                    drawcircle(500);
                    distance2 = true;
                    Log.d("daeng", "onClick: marker_show 크기 : " + markers.size());
                    for (int i = 0; i < poiItems.length; i++) {
                        if (markers.get(i) < 500) {
                            poiItems[i].setItemName(setName.get(i));
                            Log.d("daeng", "onClick: " + poiItems[i].getItemName());
                        }
                        else{
                            poiItems[i].setItemName(null);
                        }
                    }
                    mapView.addPOIItems(poiItems);
                    distance1 = false;
                    select_circle = 1;

                }
            }
        });

        distance_test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (latitude > 0 && longitude > 0 && distance2) {
                    drawcircle(4000);
                    distance1 = true;

                    for (int i = 0; i < poiItems.length; i++) {
                        if (markers.get(i) < 4000) {
                            poiItems[i].setItemName(setName.get(i));
                        }
                        else{
                            poiItems[i].setItemName(null);

                        }
                    }
                    mapView.addPOIItems(poiItems);
                    distance2 = false;
                    select_circle = 2;
                }
            }
        });

        display_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_controll = true;
                tracking_controll(2);
            }
        });

        display_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_controll = false;
                tracking_controll(2);
            }
        });
        release_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_controll = false;
                tracking_controll(1);

            }
        });


        zoombtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.setZoomLevel(5, true);
                Toast.makeText(getApplicationContext(), "level 1", Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {


        for (int i = 0; i < poiItems.length; i++) {
            if (mapPOIItem.getItemName().equals(poiItems[i].getItemName())) {
                String piclist = mapDatas.get(i).getImg_path();
                String[] img = piclist.split(",");
                if(img.length==1) {
                    Intent intent = new Intent(MainActivity.this, imageView.class);
                    intent.putExtra("image_data", pic_data.get(i));
                    startActivity(intent);
                }
                else{
                    Intent intents = new Intent(MainActivity.this, DisplayImage.class);
                    intents.putExtra("mapData", imglist);
                    startActivity(intents);

                }
            }
        }
    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        //이코드 부분 다시 살펴보기
//        Log.d("플롯값", "onCurrentLocationUpdate: v값" + v);
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        latitude = mapPointGeo.latitude;
        longitude = mapPointGeo.longitude;
        marker_distance();

        for(int i=0; i<setName.size(); i++){
            mapDatas.get(i).setDistance(markers.get(0));
        }
        Log.d("하하하하", "onCurrentLocationUpdate: " + mapDatas.get(0).getDistance());

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
    }

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

    //사용자가 지도 드래그를 시작한 경우 호출된다.
    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
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

        if (display_controll) {
            x_location = x / 1000;
            y_location = y / 1000;

            double my_latitude = mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
            double my_longitude = mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;

            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(my_latitude - y_location, my_longitude + x_location), true);

        }
    }


    //위치 권한 허용
    private void permission_check() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) { //포그라운드 위치 권한 확인
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) { //백그라운드 위치 권한 확인
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 0);
        }
    }

    //위도와 경도로 거리값 계산
    public double DistanceByDegreeAndroid(double _latitude1, double _longitude1, double _latitude2, double _longitude2) {
        Location startPos = new Location("PointA");
        Location endPos = new Location("PointB");

        startPos.setLatitude(_latitude1);
        startPos.setLongitude(_longitude1);
        endPos.setLatitude(_latitude2);
        endPos.setLongitude(_longitude2);

        double distance = startPos.distanceTo(endPos);

        return distance;
    }

    private void marker_distance() {

        if (latitude > 0 && longitude > 0&& poiItems !=null)
            for (int i = 0; i < poiItems.length; i++) {
                double map_test = poiItems[i].getMapPoint().getMapPointGeoCoord().latitude;
                double map_tests = poiItems[i].getMapPoint().getMapPointGeoCoord().longitude;
                markers.add(i, DistanceByDegreeAndroid(latitude, longitude, map_test, map_tests));

                Log.d("result", "marker_distance: " + markers.get(i)+"m");

            }

       int i = Integer.parseInt(String.valueOf(Math.round(markers.get(0))));
            //double형을 int값으로 형변환

        Log.d("result", "값이뭐냐: " + i+"M");


    }

    private void tracking_controll(int controll) {

        if (controll == 1) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        } else {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

        }
    }


    private void drawcircle(int radius) {
        mapView.removeAllCircles();
        mapView.removeAllPOIItems();
        MapCircle circle1 = new MapCircle(
                MapPoint.mapPointWithGeoCoord(latitude, longitude), // center
                radius, // radius
                Color.argb(128, 255, 0, 0), // strokeColor
                Color.argb(0, 0, 0, 0) // fillColor
        );
        circle1.setTag(1234);
        mapView.addCircle(circle1);

    }

    private void drawMarker(){

        poiItems = new MapPOIItem[setName.size()];

        for(int i=0; i<marker.size(); i++) {
            marker.get(i).setTag(i);
            marker.get(i).setMapPoint(mapPoint.get(i));
            marker.get(i).setMarkerType(MapPOIItem.MarkerType.BluePin);
            marker.get(i).setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
            poiItems[i] = marker.get(i);
        }
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
 Log.e("name not found", e.toString());
 }
 }
 *******************************************************************************************************************************************************/
