package com.example.loginregister.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginregister.MainActivity;
import com.example.loginregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SetNicknameActivity extends AppCompatActivity {
    private final static String TAG = "setNIckName_Activity";
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private String user_nick;
    private EditText et_nickname;
    private TextView tv_confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nickname);

        et_nickname = (EditText)findViewById(R.id.et_setNickName);
        tv_confirm = (TextView)findViewById(R.id.tv_confirm);

        et_nickname.setVisibility(View.INVISIBLE);
        tv_confirm.setVisibility(View.INVISIBLE);

        if(mAuth!=null){//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            Log.e("frag1", String.valueOf(mAuth));
            mStore.collection("user").document(mAuth.getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                user_nick = task.getResult().getString("nickname");
                                if(user_nick!=null) {
                                    Log.e(TAG, "닉네임받아오기성공 - "+user_nick);
                                    Intent intent = new Intent(SetNicknameActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // 현재 액티비티 파괴
                                }
                                else{
                                    et_nickname.setVisibility(View.VISIBLE);
                                    tv_confirm.setVisibility(View.VISIBLE);

                                    tv_confirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if(et_nickname.getText().toString()!=null){
                                                Log.e(TAG, "닉네임없음");
                                                HashMap<String,String> map = new HashMap<String,String>();
                                                map.put("nickname",et_nickname.getText().toString());
                                                Log.e("Setnickname", String.valueOf(map));
                                                mStore.collection("user").document(mAuth.getUid()).set(map);
                                                Intent intent = new Intent(SetNicknameActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{
                                                Toast.makeText(SetNicknameActivity.this,"닉네임을 입력해주세요.",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

}
