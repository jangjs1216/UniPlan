package com.example.loginregister.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.loginregister.FirebaseID;
import com.example.loginregister.MainActivity;
import com.example.loginregister.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mFirebaseAuth; // 파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd; // 회원가입 입력필드

    SignInButton Google_Login; // 구글 로그인 버튼

    private static final int RC_SIGN_IN = 1000; // 구글 로그인 결과 코드
    private FirebaseAuth mAuth; // 파이어베이스 인증 객체
    private GoogleApiClient mGoogleApiClient; // 구글 API 클라이언트 객체

    String str_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        SharedPreferences sharedPref;
        mFirebaseAuth=FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("LoginRegister");

        mEtEmail=findViewById(R.id.et_email);
        mEtPwd=findViewById(R.id.et_pwd);
        Google_Login=findViewById(R.id.Google_Login);
        Button btn_login = findViewById(R.id.btn_login);
        Button btn_register = findViewById(R.id.btn_register);
        LinearLayout layout_nickname = findViewById(R.id.layout_nickname);

        layout_nickname.setVisibility(View.INVISIBLE);

        btn_login.setOnClickListener(v -> {
            // 로그인 요청
            String strEmail = mEtEmail.getText().toString();
            String strPwd = mEtPwd.getText().toString();

            mFirebaseAuth.signInWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // 로그인 성공
                        SavedSharedPreferences.setUserName(LoginActivity.this,mEtEmail.getText().toString());
                        // 닉네임 설정...해야되는데 들어가지질 않누ㅜㅜㅜ
                        FirebaseFirestore db=FirebaseFirestore.getInstance();

                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        DocumentReference docRef = db.collection("user").document(user.getUid());

                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null && document.exists()) {
                                        str_nickname=document.get("email").toString();
                                        Log.d("@@@",str_nickname);
                                    }
                                }
                            }
                        });

                        if(str_nickname==null) {

                            mEtEmail.setVisibility(View.INVISIBLE);
                            mEtPwd.setVisibility(View.INVISIBLE);
                            Google_Login.setVisibility(View.INVISIBLE);
                            btn_login.setVisibility(View.INVISIBLE);
                            btn_register.setVisibility(View.INVISIBLE);
                            layout_nickname.setVisibility(View.VISIBLE);

                            EditText et_nickname = findViewById(R.id.et_nickname);
                            Button btn_nickname = findViewById(R.id.btn_nickname);
                            btn_nickname.setOnClickListener(view -> {
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put(FirebaseID.nickname,et_nickname);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // 현재 액티비티 파괴
                            });
                        }
                        else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // 현재 액티비티 파괴
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        mAuth=FirebaseAuth.getInstance();

        // 구글 로그인 버튼 클릭했을 때 여기서 수행
        Google_Login.setOnClickListener(view -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent,RC_SIGN_IN);
        });


        btn_register.setOnClickListener(view -> {
            // 회원가입 화면으로 이동
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { // 구글 로그인 인증 요청했을 때 결과 값 돌려받는 곳
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                //구글 로그인 성공해서 파베에 인증
                GoogleSignInAccount account = result.getSignInAccount(); // account라는 데이터 : 구글 로그인 정보를 담고 있음
                firebaseAuthWithGoogle(account); // 로그인 결과 값 출력 수행하라는 메소드
            } else {
                Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // 로그인 성공했을 경우
                            Toast.makeText(LoginActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            SavedSharedPreferences.setUserName(LoginActivity.this,mEtEmail.getText().toString());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            // 여기서 구글계정 정보 받아올 수 있음
                            startActivity(intent);
                        } else { // 로그인 실패했을 경우
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}