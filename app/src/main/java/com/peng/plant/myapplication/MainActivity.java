package com.peng.plant.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterInside;

import net.daum.android.map.MapEngineManager;
import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.n.api.internal.NativePOIItemMarkerManager;
import net.daum.mf.map.task.MapTaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static net.daum.mf.map.api.MapPOIItem.ShowAnimationType.SpringFromGround;


public class MainActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener, TiltScrollController.ScrollListener {

    private Button[] zoombtn;
    private double latitude, longitude, x_location, y_location;
    private boolean display_controll;
    private MapView mapView;
    private TiltScrollController mTiltScrollController;
    private MapPOIItem[] poiItems;
    private ArrayList<String> pic_data, setName;
    private ArrayList<Double> distances;
    private Button distance, display_move, display_stop_img, mylocation, leftMove, rightMove, upMove, downMove, trackingMod_btn;
    private Boolean distance1, distance2, relase, locationControll;
    private ArrayList<MapPOIItem> marker;
    private ArrayList<MapPoint> CustomMapPoint;
    private ArrayList<mapData> mapDatas;
    private ArrayList<Piclist_data> imglist;
    private Bitmap mbitmap, resize_bitmap;
    private int SelectNum, zoomNum, markerNum;
    private Animation fadeInAnim, fadeOutAnim;
    private TextView zoombtn1, zoombtn2, zoombtn3, zoombtn4, zoombtn5, display_lockOn, display_lockOff, Sensor_on, Sensor_off, moveUp, moveDown, moveLeft, moveRight,
            circle1, circle2, circle3, remove_circle, locationMove, locationStop, next_btn, before_btn, select_btn, trackingOn, trackingOff;
    private RelativeLayout container;
    private TextView[] createText;
    private double my_latitude, my_longitude;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.fade_in, R.anim.none);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        permission_check();

        /**************************************** 맵뷰 기본 셋팅  *****************************************/
        mapView = new MapView(this);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(37.43232, 127.17854), 5, true);
        mapViewContainer.addView(mapView);

        /**************************************** 맵뷰 기본 셋팅 *****************************************/


        /*******************************************************************************************************************************************************************
         *                                                                      데이터 넣는 부분 array 선언
         *******************************************************************************************************************************************************************/

        setName = new ArrayList<String>(); //사용자가 지정한 사진의 이름
        pic_data = new ArrayList<String>();//사용자가 올린 사진 데이터
        distances = new ArrayList<Double>();//내위치에서 마커까지의 거리
        CustomMapPoint = new ArrayList<MapPoint>();// 위도 경도 값

        marker = new ArrayList<MapPOIItem>();//마커 객체들

        mapDatas = new ArrayList<mapData>(); //사용자가 지정한 사진의 이름 , 사용자가 올린 사진데이터 , 위도 경도, 현재 내위치에서의 거리등을 총괄적으로 저장
        imglist = new ArrayList<Piclist_data>();


        /*******************************************************************************************************************************************************************
         *                                                                      데이터 넣는 부분 (이름 , 사진데이터 , 새로운 마커 객체 , 위도 경도값 지정)
         *******************************************************************************************************************************************************************/

        /*************     서버에서 가져온 이미지 데이터   ************/
        String[] imgdata = {
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fldsrd%2FbtrcAbqdHnn%2FkOXiUz2YtJdUi0l2xTctv1%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbQz2vJ%2FbtrcStRNHn6%2Fyt0XdKPaL5kFHVUnZlk8c0%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fbt61DA%2Fbtrc13KIRmG%2FQkFcwnoY0ZZdefSRhg3HH0%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbuvDfw%2Fbtrc6Q40Bjc%2FLsEKtk4pyk14fDMLXNpdQK%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FTbfLD%2Fbtrc7lX8Shf%2FJ9niLA1oArpckhdKN0qilK%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcjSGvy%2Fbtrc3YoDmi7%2FywWkVTJpIX6MDIgfrNeu5K%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbHcp21%2FbtrcRpnPIsK%2FVvMgwDZMPTkoLO67evHD3K%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fqz104%2Fbtrc2n3z1x2%2FP1GEkVnMrr1gOquN1kajVk%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fcccfs5%2FbtrcUlk5GuH%2FUJIkjOpLxKuFO4XhxFXDM0%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FmwbVo%2Fbtrc4FbzYAU%2F1T6i7BH4rdKjbb8aJc7du1%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fdy6GsY%2FbtrcZsYUoha%2FqYuoNhFMUptgmCARz2NTY0%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbCBfQA%2Fbtrc6RQqfAU%2FzYG2kYkd7Z2U8km9csrT50%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FDrXNR%2Fbtrc7lX8SCS%2FDyK8bnUGYgjKwTk3ef6WoK%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FdBYPwI%2Fbtrc1vHskyA%2FNK0Cc6nGP1H0kvCKwJkbx1%2Fimg.jpg",
                "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FWGhZq%2Fbtrc1eswbIF%2FIzsgEKV7agvauKP3BBpHwk%2Fimg.jpg"
        };


        for (int i = 0; i < imgdata.length; i++) {
            setName.add(i, "마커" + " " + (i + 1));
        }

        for (int i = 0; i < imgdata.length; i++) {
            pic_data.add(i, imgdata[i]);
        }

        for (int i = 0; i < setName.size(); i++) {
            marker.add(i, new MapPOIItem());
            CustomMapPoint.add(i, MapPoint.mapPointWithGeoCoord((37.43232 - (i * 0.01)), ((127.17854 - (i * 0.01)))));
        }

        initDistanceCalculate();
        addMapdata();
        urlImgConvert();
//        drawMarker();

        for (int i = 0; i < mapDatas.size(); i++) {
            Log.d("하하", "onCreate: mapdatas : " + i + "번 " + mapDatas.get(i).getDistance());
        }


        /*******************************************************************************************************************************************************************
         *                                                                      데이터 넣는 부분 Mapdata에 데이터들 전부 담기
         *******************************************************************************************************************************************************************/

        /************************************
         findviewbyidId 부분은 nitview 함수로 정리
         ************************************/

        mTiltScrollController = new TiltScrollController(getApplicationContext(), this);

        /*애니메이션 */
        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        /*애니메이션 */

        /*bloean */
        distance1 = true;
        distance2 = true;
        display_controll = false; // 화면 이동 제어
        locationControll = false; // 현재 내위치 이동 제어
        relase = false; // 화면 고정 해제 , 이동 해제 등등 제어
        /*bloean */

        zoombtn = new Button[5];
        int[] zoom = {5, 4, 3, 2, 1};
        Integer[] Rid_button = {R.id.zoomlevel1, R.id.zoomlevel2, R.id.zoomlevel3, R.id.zoomlevel4, R.id.zoomlevel5};

        for (int i = 0; i < zoombtn.length; i++) {
            zoombtn[i] = (Button) findViewById(Rid_button[i]);
        }

        trackingMod_btn = findViewById(R.id.trackingmod);

        display_move = findViewById(R.id.display_move_on);
        display_stop_img = findViewById(R.id.display_lock_img);
        mylocation = findViewById(R.id.displayLocation);

        zoombtn1 = findViewById(R.id.zoomlevel_1);
        zoombtn2 = findViewById(R.id.zoomlevel_2);
        zoombtn3 = findViewById(R.id.zoomlevel_3);
        zoombtn4 = findViewById(R.id.zoomlevel_4);
        zoombtn5 = findViewById(R.id.zoomlevel_5);

        display_lockOn = findViewById(R.id.display_lock);
        display_lockOff = findViewById(R.id.display_lock_off);

        Sensor_on = findViewById(R.id.displaySensor_on);
        Sensor_off = findViewById(R.id.displaySensor_off);


        moveUp = findViewById(R.id.displayMoveUp);
        moveDown = findViewById(R.id.displayMoveDown);
        moveLeft = findViewById(R.id.displayMoveLeft);
        moveRight = findViewById(R.id.displayMoveRight);

        leftMove = findViewById(R.id.left);
        rightMove = findViewById(R.id.right);
        upMove = findViewById(R.id.up);
        downMove = findViewById(R.id.down);

        distance = findViewById(R.id.distanceCircle);

        circle1 = findViewById(R.id.circle_radius_100);
        circle2 = findViewById(R.id.circle_radius_300);
        circle3 = findViewById(R.id.circle_radius_500);
        remove_circle = findViewById(R.id.circle_remove);

        locationMove = findViewById(R.id.move_location);
        locationStop = findViewById(R.id.stop_location);

        next_btn = findViewById(R.id.next_marker);
        before_btn = findViewById(R.id.before_marker);
        select_btn = findViewById(R.id.select_marker);

        trackingOn = findViewById(R.id.trackingmodOn);
        trackingOff = findViewById(R.id.trackingmodOff);


        mapView.setCurrentLocationEventListener(this);
        mapView.setMapViewEventListener(this);
        mapView.getMapCenterPoint();
        mapView.setMapTilePersistentCacheEnabled(true);
        mapView.setHDMapTileEnabled(false);
        trackingMod(1);

        voiceControll();


        /************************************
         findviewbyidId 부분은 nitview 함수로 정리
         ************************************/


//        어레이리스트에 맵데이터 추가 예시
//        mapPoint.add(0,MapPoint.mapPointWithGeoCoord(37.43207,27.17650));
//        MapPoint mapPoints = MapPoint.mapPointWithGeoCoord(37.43207, 127.17650);

//        /* 필요없는 부분 , 수정해서쓸 부분 */
//        /*이름 , 사진데이터 , 좌표값 담는 부분 */
//        for (int i = 0; i < mapDatas.size(); i++) {
//            String piclist = mapDatas.get(i).getImg_path();
//            String[] img = piclist.split(",");
//            for (int j = 0; j < img.length; j++) {
//                if (img.length > 1 && img != null) {
//                    Piclist_data mPicdata = new Piclist_data();
//                    mPicdata.img_path = img[j];
//                    imglist.add(mPicdata);
//                }
//            }
//        }
//        /* 필요없는 부분 , 수정해서쓸 부분 */



        /* zoomlevel controll */
        for (int i = 0; i < zoombtn.length; i++) {
            final int INDEX;
            INDEX = i;

            zoombtn[INDEX].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapView.setZoomLevel(zoom[INDEX], true);
                    SelectNone(SelectNum);
                    zoomSelect(INDEX);
                    SelectNum = INDEX;
                }
            });
        }

        /********************************텍스트뷰 동적 생성 ********************************/

        container = findViewById(R.id.maplayout);
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        createText = new TextView[setName.size()];
        for (int i = 0; i < setName.size(); i++) {

            createText[i] = new TextView(this);
            createText[i].setId(i);
            createText[i].setText("마커 " + (i + 1) + "선택");
            createText[i].setTextSize((float) 0.01);

            createText[i].setLayoutParams(layoutParams);

            createText[i].setGravity(Gravity.CENTER);

            RelativeLayout ll = new RelativeLayout(this);
            ll.addView(createText[i]);

            container.addView(ll);

            final int positions = i;

            createText[i].setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    imglist.removeAll(imglist);
                    String pictures = mapDatas.get(positions).getImg_path();
                    String[] img = pictures.split(",");
                    for (int i = 0; i < img.length; i++) {
                        if (img.length > 1) {
                            Piclist_data mPicdata = new Piclist_data();
                            mPicdata.img_path = img[i];
                            imglist.add(mPicdata);
                        }
                    }
                    if (imglist.size() == 0) {
                        Intent intent = new Intent(MainActivity.this, imageView.class);
                        intent.putExtra("image_data", mapDatas.get(positions).getImg_path());
                        startActivity(intent);
                    } else {
                        Intent intents = new Intent(MainActivity.this, DisplayImage.class);
                        intents.putExtra("mapData", imglist);
                        startActivity(intents);

                    }
                }
            });

        }

        /********************************텍스트뷰 동적 생성 ********************************/


        //반경 50 미터
//        distance_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (latitude > 0 && longitude > 0 && distance1) {
//                    drawcircle(500);
//                    distance2 = true;
//                    Log.d("daeng", "onClick: marker_show 크기 : " + markers.size());
//                    for (int i = 0; i < poiItems.length; i++) {
//                        if (markers.get(i) < 500) {
//                            poiItems[i].setItemName(setName.get(i));
//                            poiItems[i].setShowAnimationType(SpringFromGround);
//                            poiItems[i].setShowCalloutBalloonOnTouch(false);
//
//                            Log.d("daeng", "onClick: " + poiItems[i].getItemName());
//                        } else {
//                            poiItems[i].setItemName(null);
//                        }
//                    }
//                    mapView.addPOIItems(poiItems);
//
//                    distance1 = false;
////                    select_circle = 1;
//                }
//                poiItems[1].setItemName(setName.get(1));
//                poiItems[1].setShowAnimationType(SpringFromGround);
//                mapView.addPOIItems(poiItems);
//            }
//        });

        //반경 4천미터
//        distance_test2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (latitude > 0 && longitude > 0 && distance2) {
//                    drawcircle(4000);
//                    distance1 = true;
//
//                    for (int i = 0; i < poiItems.length; i++) {
//                        if (markers.get(i) < 4000) {
//                            poiItems[i].setItemName(setName.get(i));
//                            poiItems[i].setShowCalloutBalloonOnTouch(false);
//                            poiItems[i].setShowAnimationType(SpringFromGround);
//                        } else {
//                            poiItems[i].setItemName(null);
//
//                        }
//                    }
//                    mapView.addPOIItems(poiItems);
//                    distance2 = false;
////                    select_circle = 2;
//                }
//            }
//        });


    }


    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        //이코드 부분 다시 살펴보기
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        latitude = mapPointGeo.latitude;
        longitude = mapPointGeo.longitude;
        realTimeDistance();

        for (int i = 0; i < setName.size(); i++) {
            mapDatas.get(i).setDistance(distances.get(i));
        }
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


    @Override
    public void onTilt(float x, float y) {
        if (display_controll) {
            zoom_sensor(x, y);

            displayLocation();

            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(my_latitude - y_location, my_longitude + x_location), false);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.fade_out);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
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


    private void realTimeDistance() {

        /* 전에 들어있던값 모두 제거 */
        if (latitude > 0 && longitude > 0 && poiItems != null)
            for (int i = 0; i < poiItems.length; i++) {
                double markerLatitude = poiItems[i].getMapPoint().getMapPointGeoCoord().latitude;
                double markerLongitude = poiItems[i].getMapPoint().getMapPointGeoCoord().longitude;
                distances.add(i, DistanceByDegreeAndroid(latitude, longitude, markerLatitude, markerLongitude));
            }
//        int i = Integer.parseInt(String.valueOf(Math.round(distances.get(0)))); 인트값 변환
    }

    private void trackingMod(int controls) {

        if (controls == 1) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
            trackingMod_btn.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.trackingmod_on));
        } else {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            trackingMod_btn.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.trackingmod_off));
        }
    }

    private void drawcircle(int position) {
        mapView.removeAllCircles();
//        mapView.removeAllPOIItems();
        int[] radius = {100, 300, 500};
        MapCircle[] circle = new MapCircle[3];

        displayLocation();


        for (int i = 0; i < circle.length; i++) {

            if (position >= i) {
                circle[i] = new MapCircle(
                        MapPoint.mapPointWithGeoCoord(my_latitude, my_longitude), // center
                        radius[i], // radius
                        Color.argb(128, 255, 0, 0), // strokeColor
                        Color.argb(0, 0, 0, 0) // fillColor
                );
                circle[i].setTag(i);
                mapView.addCircle(circle[i]);
            }
        }
    }

    /* 사진이 여러장일경우 점으로 표시 , 한장일경우 이미지로 표시 */
    private void drawMarker(ArrayList<MapPOIItem> marker) {
        ArrayList<MapPOIItem> markers = marker;
//        urlImgConvert();
        poiItems = new MapPOIItem[markers.size()];

        for (int i = 0; i < marker.size(); i++) {

            poiItems[i] = markers.get(i);
            poiItems[i].setShowAnimationType(SpringFromGround);
            poiItems[i].setShowDisclosureButtonOnCalloutBalloon(false);

//            String piclist = mapDatas.get(i).getImg_path();
//            String[] img = piclist.split(",");
//            if (img.length > 1) {

//                poiItems[i].setShowAnimationType(SpringFromGround);
//                poiItems[i].setShowDisclosureButtonOnCalloutBalloon(false);
//
//            } else {
//                marker.get(i).setTag(i);
//                marker.get(i).setMapPoint(MapPoint.mapPointWithGeoCoord(mapDatas.get(i).latitude, mapDatas.get(i).longitude));
//                marker.get(i).setMarkerType(MapPOIItem.MarkerType.CustomImage);
//                marker.get(i).setCustomImageBitmap(mapDatas.get(i).getBitmap_Marker());
//                marker.get(i).setCustomImageAutoscale(false);
//                marker.get(i).setItemName(mapDatas.get(i).getName());
////                marker.get(i).setCustomImageAnchor(0.5f, 1.0f);
//                poiItems[i] = marker.get(i);

//                poiItems[i].setShowAnimationType(SpringFromGround);
//                poiItems[i].setShowDisclosureButtonOnCalloutBalloon(false);
//            }
        }
        mapView.addPOIItems(poiItems);
        mapView.selectPOIItem(poiItems[0], true);
    }


    private void zoomSelect(int zoomlevel) {

        zoomNum = zoomlevel;

        for (int i = 0; i < zoombtn.length; i++) {
            if (zoomlevel == i) {
                zoombtn[i].setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border_check));
            }
        }
    }

    private void SelectNone(int zoomlevel) {

        for (int i = 0; i < zoombtn.length; i++) {
            if (zoomlevel == i) {
                zoombtn[i].setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_border));
            }
        }
    }

    //화면 고정할때 나오는 이미지 제어
    private void stopImg(int num) {

        if (num == 1) {
            if (display_stop_img.getVisibility() == View.GONE) {
                display_stop_img.setVisibility(View.VISIBLE);
                display_stop_img.startAnimation(fadeInAnim);
            }

        } else {
            if (display_stop_img.getVisibility() == View.VISIBLE) {
                display_stop_img.startAnimation(fadeOutAnim);
                display_stop_img.setVisibility(View.GONE);
            }
        }
    }

    private View.OnClickListener mapControill = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.zoomlevel_1:
                    mapView.setZoomLevel(5, true);
                    SelectNone(SelectNum);
                    zoomSelect(0);
                    SelectNum = 0;
                    break;

                case R.id.zoomlevel_2:
                    mapView.setZoomLevel(4, true);
                    SelectNone(SelectNum);
                    zoomSelect(1);
                    SelectNum = 1;
                    break;

                case R.id.zoomlevel_3:
                    mapView.setZoomLevel(3, true);
                    SelectNone(SelectNum);
                    zoomSelect(2);
                    SelectNum = 2;
                    break;

                case R.id.zoomlevel_4:
                    mapView.setZoomLevel(2, true);
                    SelectNone(SelectNum);
                    zoomSelect(3);
                    SelectNum = 3;
                    break;

                case R.id.zoomlevel_5:
                    mapView.setZoomLevel(1, true);
                    SelectNone(SelectNum);
                    zoomSelect(4);
                    SelectNum = 4;
                    break;

                case R.id.display_lock:
                    display_controll = false;
                    stopImg(1);
                    trackingMod(2);
                    break;

                case R.id.display_lock_off:
                    stopImg(2);
                    display_controll = true;
                    if (relase == false) {
                        display_controll = false;
                        trackingMod(1);
                    }
                    break;

                case R.id.displaySensor_on:
                    display_controll = true;
                    relase = true;
                    stopImg(2);
                    trackingMod(2);
                    display_move.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.displaycontroll_on));
                    break;

                case R.id.displaySensor_off:
                    display_controll = false;
                    relase = false;
                    if (display_stop_img.getVisibility() == View.GONE) {
                        trackingMod(1);
                    }
                    display_move.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.displaycontroll_off));
                    break;

                case R.id.displayMoveUp:
                    mapMoveControll(1);
                    if (locationControll) {
                        mapView.removeAllPOIItems();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                locationDesignate();
                            }
                        }, 100);
                    }
                    DistanceCalculate();
                    urlImgConvert();
                    break;

                case R.id.displayMoveDown:
                    mapMoveControll(2);
                    if (locationControll) {
                        mapView.removeAllPOIItems();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                locationDesignate();
                            }
                        }, 100);
                    }
                    DistanceCalculate();
                    urlImgConvert();
                    break;


                case R.id.displayMoveRight:
                    mapMoveControll(3);

                    if (locationControll) {
                        mapView.removeAllPOIItems();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                locationDesignate();
                            }
                        }, 100);
                    }
                    DistanceCalculate();
                    urlImgConvert();
                    break;


                case R.id.displayMoveLeft:
                    mapMoveControll(4);
                    if (locationControll) {
                        mapView.removeAllPOIItems();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                locationDesignate();
                            }
                        }, 100);
                    }
                    DistanceCalculate();
                    urlImgConvert();
                    break;

                case R.id.circle_radius_100:
                    drawcircle(0);
                    distance.setText("100");
                    distance.setTextColor(Color.parseColor("#000000"));
                    distance.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_choice));

                    mapView.removeAllPOIItems();

                    for (int i = 0; i < pic_data.size(); i++) {
                        marker.add(i, new MapPOIItem());

//                        if (mapDatas.get(i).getDistance() < 100) {
                        Log.d("sdsd", "onClick: 반경값" + mapDatas.get(i).getDistance());
                        marker.get(i).setTag(i);
                        marker.get(i).setItemName("마커 " + (i + 1));
                        marker.get(i).setMapPoint(MapPoint.mapPointWithGeoCoord(mapDatas.get(i).latitude, mapDatas.get(i).longitude));
//                        marker.get(i).setMarkerType(MapPOIItem.MarkerType.BluePin);
                        marker.get(i).setMarkerType(MapPOIItem.MarkerType.CustomImage);
                        marker.get(i).setCustomImageBitmap(mapDatas.get(i).getBitmap_Marker());
                        marker.get(i).setCustomImageAutoscale(false);
//                        marker.get(i).setItemName(mapDatas.get(i).getName());
//                        } else {
//                            poiItems[i].setItemName(null);
//                        }

                    }

                    drawMarker(marker);


                    break;

                case R.id.circle_radius_300:
                    drawcircle(1);
                    distance.setText("300");
                    distance.setTextColor(Color.parseColor("#000000"));
                    distance.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_choice));
                    break;

                case R.id.circle_radius_500:
                    drawcircle(2);
                    distance.setText("500");
                    distance.setTextColor(Color.parseColor("#000000"));
                    distance.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_choice));
                    break;

                case R.id.circle_remove:
                    distance.setTextColor(Color.parseColor("#40000000"));
                    distance.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_default));
                    mapView.removeAllCircles();
                    mapView.removeAllPOIItems();
                    break;

                case R.id.move_location:
                    trackingMod(2);
                    locationControll = true;
                    mylocation.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.my_location));
                    mapView.removeAllPOIItems();
                    locationDesignate();

                    break;

                case R.id.stop_location:
                    trackingMod(1);
                    locationControll = false;
                    mylocation.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.locationstop_icon));
                    mapView.removeAllPOIItems();
                    break;

                case R.id.next_marker:

                    if (markerNum < poiItems.length - 1) {
                        markerNum = markerNum + 1;
                        mapView.selectPOIItem(poiItems[markerNum], true);
                    }
                    break;

                case R.id.before_marker:

                    if (markerNum > 0) {
                        markerNum = markerNum - 1;
                        mapView.selectPOIItem(poiItems[markerNum], true);
                    }

                    break;

                case R.id.select_marker:

                    imglist.removeAll(imglist);
                    String pictures = mapDatas.get(markerNum).getImg_path();
                    String[] img = pictures.split(",");
                    for (int i = 0; i < img.length; i++) {
                        if (img.length > 1) {
                            Piclist_data mPicdata = new Piclist_data();
                            mPicdata.img_path = img[i];
                            imglist.add(mPicdata);
                        }
                    }
                    if (imglist.size() == 0) {
                        Intent intent = new Intent(MainActivity.this, imageView.class);
                        intent.putExtra("image_data", mapDatas.get(markerNum).getImg_path());
                        startActivity(intent);
                    } else {
                        Intent intents = new Intent(MainActivity.this, DisplayImage.class);
                        intents.putExtra("mapData", imglist);
                        startActivity(intents);

                    }

                    break;

                case R.id.trackingmodOn:
                    trackingMod(1);
                    break;


                case R.id.trackingmodOff:
                    trackingMod(2);
                    break;

            }
        }
    };

    private void voiceControll() {

        zoombtn1.setOnClickListener(mapControill);
        zoombtn2.setOnClickListener(mapControill);
        zoombtn3.setOnClickListener(mapControill);
        zoombtn4.setOnClickListener(mapControill);
        zoombtn5.setOnClickListener(mapControill);

        display_lockOn.setOnClickListener(mapControill);
        display_lockOff.setOnClickListener(mapControill);

        Sensor_on.setOnClickListener(mapControill);
        Sensor_off.setOnClickListener(mapControill);

        moveUp.setOnClickListener(mapControill);
        moveDown.setOnClickListener(mapControill);
        moveLeft.setOnClickListener(mapControill);
        moveRight.setOnClickListener(mapControill);

        circle1.setOnClickListener(mapControill);
        circle2.setOnClickListener(mapControill);
        circle3.setOnClickListener(mapControill);
        remove_circle.setOnClickListener(mapControill);

        locationMove.setOnClickListener(mapControill);
        locationStop.setOnClickListener(mapControill);

        next_btn.setOnClickListener(mapControill);
        before_btn.setOnClickListener(mapControill);
        select_btn.setOnClickListener(mapControill);

        trackingOn.setOnClickListener(mapControill);
        trackingOff.setOnClickListener(mapControill);

    }

    /****** 줌레벨에따라 감도 조절 ******/
    private void zoom_sensor(float x, float y) {
        x_location = x / ((zoomNum + 1) * 1000);
        y_location = y / ((zoomNum + 1) * 1000);

    }

    private void mapMoveControll(int location) {

        displayLocation();

        if (display_controll != true) {

            switch (location) {
                case 1:
                    for (int i = 0; i < 10; i++) {
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(my_latitude + 0.010, my_longitude), true);
                        upMove.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.up_click));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                upMove.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.up_icon));
                            }
                        }, 300);

                    }
                    break;

                case 2:
                    for (int i = 0; i < 10; i++) {
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(my_latitude - 0.010, my_longitude), true);
                        downMove.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.down_click));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                downMove.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.down_icon));
                            }
                        }, 300);

                    }
                    break;
                case 3:
                    for (int i = 0; i < 10; i++) {
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(my_latitude, my_longitude + 0.010), true);
                        rightMove.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.right_click));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                rightMove.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.right_icon));
                            }
                        }, 300);
                    }
                    break;
                case 4:
                    for (int i = 0; i < 10; i++) {
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(my_latitude, my_longitude - 0.010), true);
                        leftMove.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.left_click));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                leftMove.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.left_icon));
                            }
                        }, 300);
                    }
                    break;


            }
        }
    }

    private void locationDesignate() {

        displayLocation();

        MapPoint map = MapPoint.mapPointWithGeoCoord(my_latitude, my_longitude);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("내위치 지정");
        marker.setTag(0);
        marker.setMapPoint(map);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setShowAnimationType(SpringFromGround);

        mapView.addPOIItem(marker);

    }

    private void displayLocation() {

        my_latitude = mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
        my_longitude = mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;

    }

    private void addMapdata() {

        for (int i = 0; i < pic_data.size(); i++) {
            mapData mData = new mapData();
            mData.setName(setName.get(i));
            mData.setImg_path(pic_data.get(i));
            mData.setLatitude(CustomMapPoint.get(i).getMapPointGeoCoord().latitude);
            mData.setLongitude(CustomMapPoint.get(i).getMapPointGeoCoord().longitude);
            mData.setDistance(distances.get(i));
            mapDatas.add(mData);
        }

    }

    private void initDistanceCalculate() {

        /********* 내위치와 마커간의 거리를 계산 ***/
        for (int i = 0; i < CustomMapPoint.size(); i++) {
            double markerLatitude = CustomMapPoint.get(i).getMapPointGeoCoord().latitude;
            double markerLongitude = CustomMapPoint.get(i).getMapPointGeoCoord().longitude;
            distances.add(i, DistanceByDegreeAndroid(37.43232, 127.17854, markerLatitude, markerLongitude));
        }
    }

    private void DistanceCalculate() {

        /********* 내위치와 마커간의 거리를 계산 ***/
        for (int i = 0; i < CustomMapPoint.size(); i++) {
            double markerLatitude = CustomMapPoint.get(i).getMapPointGeoCoord().latitude;
            double markerLongitude = CustomMapPoint.get(i).getMapPointGeoCoord().longitude;
            distances.set(i, DistanceByDegreeAndroid(my_latitude, my_longitude, markerLatitude, markerLongitude));

            mapData mData = new mapData();
            mData.setDistance(distances.get(i));
            mapDatas.set(i, mData);

        }
    }


    private void urlImgConvert() {

        /* url이미지를 bitmap으로 변환시켜서 지도위에 띄우기 */
        for (int i = 0; i < mapDatas.size(); i++) {
//            mapDatas.remove(i).getBitmap_Marker();

//            String piclist = mapDatas.get(i).getImg_path();
//            String[] img = piclist.split(",");
//            if (img.length == 1) {
                int finalI = i;
                Thread mThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(mapDatas.get(finalI).getImg_path());

                            // Web에서 이미지를 가져온 뒤
                            // ImageView에 지정할 Bitmap을 만든다
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true); // 서버로 부터 응답 수신
                            conn.connect();

                            InputStream is = conn.getInputStream(); // InputStream 값 가져오기
                            mbitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환
                            resize_bitmap = Bitmap.createScaledBitmap(mbitmap, 100, 60, true);

                                mapDatas.get(finalI).setBitmap_Marker(resize_bitmap);


                        } catch (MalformedURLException e) {
                            e.printStackTrace();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                mThread.start(); // Thread 실행

                try {
                    mThread.join();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//            }
        }

    }


}
