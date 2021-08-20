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

import net.daum.mf.map.api.MapCircle;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static net.daum.mf.map.api.MapPOIItem.ShowAnimationType.SpringFromGround;


public class MainActivity extends AppCompatActivity implements MapView.POIItemEventListener, MapView.CurrentLocationEventListener, MapView.MapViewEventListener, TiltScrollController.ScrollListener {

    private Button[] zoombtn;
    private double latitude, longitude, x_location, y_location;
    private boolean display_controll;
    private MapView mapView;
    private TiltScrollController mTiltScrollController;
    private MapPoint test2;
    private MapPOIItem[] poiItems;
    private ArrayList<String> pic_data, setName;
    private ArrayList<Double> markers;
    private Button distance, display_move, display_stop_img, mylocation, leftMove, rightMove, upMove, downMove;
    private Boolean distance1, distance2, relase, locationControll;
    private ArrayList<MapPOIItem> marker;
    private ArrayList<MapPoint> CustomMapPoint;
    private ArrayList<mapData> mapDatas;
    private ArrayList<Piclist_data> imglist;
    int[] idNum;
    //    select_circle
    private Bitmap mbitmap, resize_bitmap;
    private int SelectNum, zoomNum;
    private Animation fadeInAnim, fadeOutAnim;
    private TextView zoombtn1, zoombtn2, zoombtn3, zoombtn4, zoombtn5, display_lockOn, display_lockOff, Sensor_on, Sensor_off, moveUp, moveDown, moveLeft, moveRight,
            circle1, circle2, circle3, remove_circle, locationMove, locationStop;
    private RelativeLayout container;
    TextView[] createText;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.fade_in, R.anim.none);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        permission_check();

        /************************************
         findviewbyidId 부분은 nitview 함수로 정리
         ************************************/

        /*어레이리스트 */
        setName = new ArrayList<String>();//마커에 지정할 이름
        pic_data = new ArrayList<String>();//이미지 데이터값
        markers = new ArrayList<Double>();//내위치에서 마커까지의 거리
        mapDatas = new ArrayList<mapData>();
        imglist = new ArrayList<Piclist_data>();
        marker = new ArrayList<MapPOIItem>();//마커 목록
        ArrayList<String> test = new ArrayList<String>();//마커 목록

        CustomMapPoint = new ArrayList<MapPoint>();//마커가 화면에 표시될 좌표 목록
        /*어레이리스트 */


        /*애니메이션 */
        fadeInAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOutAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        /*애니메이션 */


        /*bloean */
        distance1 = true;
        distance2 = true;
        display_controll = false;
        locationControll = false;
        relase = false;
        /*bloean */

        mTiltScrollController = new TiltScrollController(getApplicationContext(), this);

        zoombtn = new Button[5];
        int[] zoom = {5, 4, 3, 2, 1};
        Integer[] Rid_button = {R.id.zoomlevel1, R.id.zoomlevel2, R.id.zoomlevel3, R.id.zoomlevel4, R.id.zoomlevel5};

        for (int i = 0; i < zoombtn.length; i++) {
            zoombtn[i] = (Button) findViewById(Rid_button[i]);
        }


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

        /************************************
         findviewbyidId 부분은 nitview 함수로 정리
         ************************************/


        /*이름 , 사진데이터 , 좌표값 담는 부분 */

        setName.add(0, "와트");
        setName.add(1, "서울");
        setName.add(2, "테스트");

        //사진 데이터
        pic_data.add("https://cdn.kado.net/news/photo/202004/1018454_448598_1539.jpg,https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fldsrd%2FbtrcAbqdHnn%2FkOXiUz2YtJdUi0l2xTctv1%2Fimg.jpg,http://image.canon-ci.co.kr/pds/editor/images/000096/20190611140954550_350LWRGN.jpg");
        pic_data.add("https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fldsrd%2FbtrcAbqdHnn%2FkOXiUz2YtJdUi0l2xTctv1%2Fimg.jpg");
        pic_data.add("https://s3.ap-northeast-2.amazonaws.com/st.dangidata/hobby_conects/data/adm/lecture_manage/curriculum/094107fc03e8452647ded805edb0f8c4.png");


        mapView = new MapView(this);
        test2 = MapPoint.mapPointWithGeoCoord(37.43232, 127.17854);
        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapView.setMapCenterPointAndZoomLevel(test2, 5, true);
        mapViewContainer.addView(mapView);

        for (int i = 0; i < setName.size(); i++) {
            marker.add(i, new MapPOIItem());
            CustomMapPoint.add(i, MapPoint.mapPointWithGeoCoord((37.43207 - (i * 0.01)), ((127.17650 - (i * 0.01)))));
        }

        /*이름 , 사진데이터 , 좌표값 담는 부분 */


//        어레이리스트에 맵데이터 추가 예시
//        mapPoint.add(0,MapPoint.mapPointWithGeoCoord(37.43207,27.17650));
//        MapPoint mapPoints = MapPoint.mapPointWithGeoCoord(37.43207, 127.17650);


        container = findViewById(R.id.maplayout);
        createText = new TextView[setName.size()];
        for (int i = 0; i < setName.size(); i++) {

            createText[i] = new TextView(this);
            createText[i].setId(i);
            createText[i].setText(setName.get(i));
            test.add(i, setName.get(i));
            createText[i].setTextSize(100);

            RelativeLayout ll = new RelativeLayout(this);

            ll.addView(createText[i]);
            //부모 뷰에 추가
            container.addView(ll);

            final int positions = i;

            createText[i].setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "나오긴함? : " + positions, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, imageView.class);
                    intent.putExtra("image_data", mapDatas.get(1).getImg_path());
                    startActivity(intent);

                }
            });

        }

        //음성 입력 컨트롤
        voiceControll();

        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);
        mapView.setMapViewEventListener(this);
        mapView.getMapCenterPoint();
        mapView.setMapTilePersistentCacheEnabled(true);
        mapView.setHDMapTileEnabled(false);
        tracking_controls(1);

        /* 객체 배열안에 마커에 지정되어있는 이름의 숫자만큼 사진 데이터, 맵포인트 값을 넣어줌 */
        for (int i = 0; i < setName.size(); i++) {
            mapData mData = new mapData();
            mData.setName(setName.get(i));
            mData.setImg_path(pic_data.get(i));
            mData.setLatitude(CustomMapPoint.get(i).getMapPointGeoCoord().latitude);
            mData.setLongitude(CustomMapPoint.get(i).getMapPointGeoCoord().longitude);
            mapDatas.add(mData);
        }


        /* 필요없는 부분 , 수정해서쓸 부분 */
        /*이름 , 사진데이터 , 좌표값 담는 부분 */
        for (int i = 0; i < mapDatas.size(); i++) {
            String piclist = mapDatas.get(i).getImg_path();
            String[] img = piclist.split(",");
            for (int j = 0; j < img.length; j++) {
                if (img.length > 1) {
                    Piclist_data mPicdata = new Piclist_data();
                    mPicdata.img_path = img[j];
                    imglist.add(mPicdata);
                }
            }
        }
        /* 필요없는 부분 , 수정해서쓸 부분 */


        /* url이미지를 bitmap으로 변환시켜서 지도위에 띄우기 */
        for (int i = 0; i < mapDatas.size(); i++) {
            String piclist = mapDatas.get(i).getImg_path();
            String[] img = piclist.split(",");
            if (img.length == 1) {
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
                            resize_bitmap = Bitmap.createScaledBitmap(mbitmap, 160, 120, true);

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
            }
        }

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


    //사용자가 MapView 에 등록된 POI Item 아이콘(마커)를 터치한 경우 호출된다.
    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {
        imglist.removeAll(imglist);
        for (int i = 0; i < poiItems.length; i++) {
            if (mapPOIItem.getItemName().equals(poiItems[i].getItemName())) {
                String pictures = mapDatas.get(i).getImg_path();
                String[] img = pictures.split(",");
                for (int j = 0; j < img.length; j++) {
                    if (img.length > 1) {
                        Piclist_data mPicdata = new Piclist_data();
                        mPicdata.img_path = img[j];
                        imglist.add(mPicdata);
                    }
                }
                if (img.length == 1) {
                    Intent intent = new Intent(MainActivity.this, imageView.class);
                    intent.putExtra("image_data", mapDatas.get(i).getImg_path());
                    startActivity(intent);
                } else {
                    Intent intents = new Intent(MainActivity.this, DisplayImage.class);
                    intents.putExtra("mapData", imglist);
                    startActivity(intents);

                }
            }
        }
    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {


    }


    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
        //이코드 부분 다시 살펴보기
        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
        latitude = mapPointGeo.latitude;
        longitude = mapPointGeo.longitude;
        marker_distance();

        for (int i = 0; i < setName.size(); i++) {
            mapDatas.get(i).setDistance(markers.get(0));
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

    /**********************************************************
     화면 이동을 외치면 센서값을 주고 트래킹 off
     화면 고정을 외치면 센서값을 닫고 (boolean으로 제어) 트래킹 on
     목요일 테스트기기 받으면 테스트기기에 맞게 센서값 변동
     불필요한 리스너 , 메소드 삭제
     **********************************************************/

    @Override
    public void onTilt(float x, float y) {
        if (display_controll) {
            zoom_sensor(x, y);

            double my_latitude = mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
            double my_longitude = mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;

            Log.d("mappoint", "onTilt: 좌우" + my_latitude);
            Log.d("mappoint", "onTilt: 좌우" + my_longitude);

            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(my_latitude - y_location, my_longitude + x_location), false);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.fade_out);
    }

    //위치 권한 허용
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

        if (latitude > 0 && longitude > 0 && poiItems != null)
            for (int i = 0; i < poiItems.length; i++) {
                double map_test = poiItems[i].getMapPoint().getMapPointGeoCoord().latitude;
                double map_tests = poiItems[i].getMapPoint().getMapPointGeoCoord().longitude;
                markers.add(i, DistanceByDegreeAndroid(latitude, longitude, map_test, map_tests));

            }

        int i = Integer.parseInt(String.valueOf(Math.round(markers.get(0))));
        //double형을 int값으로 형변환

        Log.d("result", "값이뭐냐: " + i + "M");

    }

    private void tracking_controls(int controls) {

        if (controls == 1) {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        } else {
            mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

        }
    }

    private void drawcircle(int position) {
        mapView.removeAllCircles();
//        mapView.removeAllPOIItems();

        int[] radius = {100, 300, 500};
        MapCircle[] circle = new MapCircle[3];

        double my_latitude = mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
        double my_longitude = mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;


        for (int i = 0; i < circle.length; i++) {

            if (position >= i) {
                circle[i] = new MapCircle(
                        MapPoint.mapPointWithGeoCoord(my_latitude, my_longitude), // center
                        radius[i], // radius
                        Color.argb(128, 255, 0, 0), // strokeColor
                        Color.argb(0, 0, 0, 0) // fillColor
                );
                circle[i].setTag(i);
//                drawMarker();
                Log.d("댕댕이의 코딩대작전", "drawcircle: 나몇번호출대?");
                mapView.addCircle(circle[i]);
            }


        }


    }

    /* 사진이 여러장일경우 점으로 표시 , 한장일경우 이미지로 표시 */
    private void drawMarker() {

        poiItems = new MapPOIItem[setName.size()];

        for (int i = 0; i < marker.size(); i++) {
            String piclist = mapDatas.get(i).getImg_path();
            String[] img = piclist.split(",");
            if (img.length > 1) {
                marker.get(i).setTag(i);
                marker.get(i).setMapPoint(CustomMapPoint.get(i));
                marker.get(i).setMarkerType(MapPOIItem.MarkerType.BluePin);
                marker.get(i).setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                poiItems[i] = marker.get(i);
            } else {
                marker.get(i).setTag(i);
                marker.get(i).setMapPoint(CustomMapPoint.get(i));
                marker.get(i).setMarkerType(MapPOIItem.MarkerType.CustomImage);
                marker.get(i).setCustomImageBitmap(mapDatas.get(i).getBitmap_Marker());
                marker.get(i).setCustomImageAutoscale(false);
//                marker.get(i).setCustomImageAnchor(0.5f, 1.0f);
                poiItems[i] = marker.get(i);
            }
        }
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
    private void stopImg_controll(int num) {

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
                    stopImg_controll(1);
                    tracking_controls(2);
                    break;

                case R.id.display_lock_off:
                    stopImg_controll(2);
                    display_controll = true;
                    if (relase == false) {
                        display_controll = false;
                        tracking_controls(1);
                    }
                    break;

                case R.id.displaySensor_on:
                    display_controll = true;
                    relase = true;
                    stopImg_controll(2);
                    tracking_controls(2);
                    display_move.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.displaycontroll_on));
                    break;

                case R.id.displaySensor_off:
                    display_controll = false;
                    relase = false;
                    if (display_stop_img.getVisibility() == View.GONE) {
                        tracking_controls(1);
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
                                my_location();
                            }
                        }, 100);
                    }
                    break;

                case R.id.displayMoveDown:
                    mapMoveControll(2);
                    if (locationControll) {
                        mapView.removeAllPOIItems();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                my_location();
                            }
                        }, 100);
                    }
                    break;


                case R.id.displayMoveRight:
                    mapMoveControll(3);
                    if (locationControll) {
                        mapView.removeAllPOIItems();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                my_location();
                            }
                        }, 100);
                    }
                    break;

                case R.id.displayMoveLeft:
                    mapMoveControll(4);
                    if (locationControll) {
                        mapView.removeAllPOIItems();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                my_location();
                            }
                        }, 100);
                    }
                    break;
                case 0:
                    Intent intent = new Intent(MainActivity.this, imageView.class);
                    intent.putExtra("image_data", mapDatas.get(1).getImg_path());
                    startActivity(intent);
                    break;

                case R.id.circle_radius_100:
                    drawcircle(0);
                    distance.setText("100");
                    distance.setTextColor(Color.parseColor("#000000"));
                    distance.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.circle_choice));
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
                    break;

                case R.id.move_location:
                    locationControll = true;
                    mylocation.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.my_location));
                    mapView.removeAllPOIItems();
                    my_location();

                    break;

                case R.id.stop_location:
                    locationControll = false;
                    mylocation.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.locationstop_icon));
                    mapView.removeAllPOIItems();
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

//        createText.setOnClickListener(mapControill);
    }


    private void zoom_sensor(float x, float y) {
        x_location = x / ((zoomNum + 1) * 1000);
        y_location = y / ((zoomNum + 1) * 1000);

    }

    private void mapMoveControll(int location) {

        double my_latitude = mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
        double my_longitude = mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;

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

    private void my_location() {

        double my_latitude = mapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
        double my_longitude = mapView.getMapCenterPoint().getMapPointGeoCoord().longitude;

        MapPoint map = MapPoint.mapPointWithGeoCoord(my_latitude, my_longitude);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(0);
        marker.setMapPoint(map);
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setShowAnimationType(SpringFromGround);

        mapView.addPOIItem(marker);

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
