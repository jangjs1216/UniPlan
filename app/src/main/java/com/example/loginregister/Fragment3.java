package com.example.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.annotations.NotNull;


public class Fragment3 extends Fragment {
    private View view;
    private Toolbar toolbar;
    private final static String TAG ="Frag3";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_3, container, false);
        toolbar = (androidx.appcompat.widget.Toolbar)view.findViewById(R.id.tb_frag3);
        ((MainActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);//커스텀액션바사용
        actionBar.setDisplayShowTitleEnabled(false);//기본제목을 없애줍니다.
        setHasOptionsMenu(true);


        Button forum1=view.findViewById(R.id.forum1);
        Button forum2=view.findViewById(R.id.forum2);
        Button forum3=view.findViewById(R.id.forum3);
        Button forum4=view.findViewById(R.id.forum4);
        Button forum5=view.findViewById(R.id.forum5);
        Button forum6=view.findViewById(R.id.forum6);
        Button forum7=view.findViewById(R.id.forum7);

        forum1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),NoticeBoardActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_frag3,menu);
        Log.e(TAG,"sex");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_btn_search:
                //검색프래그먼트 오픈
                break;
            case R.id.action_btn_create:
                //글쓰기 프래그먼트로 이동;
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}