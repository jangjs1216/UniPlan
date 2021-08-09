package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {



    /*
    [ 2021-08-06 장준승 Fragment 추가 ]
     */
    BottomNavigationView bottomNavigationView;

    //과목코드 해시함수로 배열화 과목코드넣으면 과목명이랑 학점나옴
    HashMap<String, Object> subjectCode =  new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavi);

        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new Fragment1()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.item_fragment1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment1()).commit();
                        break;
                    case R.id.item_fragment2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment2()).commit();
                        break;
                    case R.id.item_fragment3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment3()).commit();
                        break;
                    case R.id.item_fragment4:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new Fragment4()).commit();
                        break;
                }
                return true;
            }
        });






        //과목코드 받아오기 함수시작
        try {
            readFromAssets("subjectCode.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //과목코드 받아오기 함수
    private void  readFromAssets(String filename) throws Exception {


        BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));

        String line = reader.readLine();
        int counti=0;

        while(line != null) {

            String[] Sarray = line.split("\t");
            String Scode = Sarray[0];
            String[] Sname = {Sarray[1],Sarray[2]};
            subjectCode.put(Sarray[0],Sname);

            ++counti;
            line = reader.readLine();
        }
        reader.close();
    }


}