package com.peng.plant.myapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.peng.plant.myapplication.MainActivity;
import com.peng.plant.myapplication.R;

public class MapActivity extends AppCompatActivity {
    private Toast toast;
    private Button mapView_btn, exit_btn;
    private long backKeyPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        initView();

        mapView_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });


        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toast = Toast.makeText(getApplicationContext(), "이용해주셔서 감사합니다", Toast.LENGTH_SHORT);
                showDialog();
            }
        });
    }

    private void initView() {

        exit_btn = (Button) findViewById(R.id.view_exit);
        mapView_btn = (Button) findViewById(R.id.mapView);

    }

    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }
    }


    private void exitProgram() {
        if (Build.VERSION.SDK_INT >= 21) {
            // 액티비티 종료 + 태스크 리스트에서 지우기
            finishAndRemoveTask();
        } else {
            // 액티비티 종료
            finish();
        }
        System.exit(0);
    }


    private void showDialog() {
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(MapActivity.this)
                .setTitle("종료하시겠습니까?").setMessage("버그 및 불편사항 문의는 WATT 공식 홈페이지 참조")
                .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setNegativeButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toast.show();
                        exitProgram();

                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }

}