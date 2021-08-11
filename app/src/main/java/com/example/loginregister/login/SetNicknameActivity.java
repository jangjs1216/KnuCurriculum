package com.example.loginregister.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
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

import static com.example.loginregister.FirebaseID.nickname;
import static com.example.loginregister.FirebaseID.user;

public class SetNicknameActivity extends AppCompatActivity {
    private final static String TAG = "setNIckName_Activity";
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();
    private String user_nick;
    private EditText et_nickname;
    private TextView tv_confirm;
    private View layout_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nickname);
        et_nickname = (EditText)findViewById(R.id.et_setNickName);
        tv_confirm = (TextView)findViewById(R.id.tv_confirm);
        layout_confirm = (View)findViewById(R.id.layout_confirm);
        et_nickname.setVisibility(View.INVISIBLE);
        tv_confirm.setVisibility(View.INVISIBLE);
        layout_confirm.setVisibility(View.INVISIBLE);

        Log.e(TAG,"닉네임설정입장 " );
        if(mAuth!=null){//UserInfo에 등록되어있는 닉네임을 가져오기 위해서
            Log.e("frag1", String.valueOf(mAuth));
            mStore.collection("user").document(mAuth.getUid())// 여기 콜렉션 패스 경로가 중요해 보면 패스 경로가 user로 되어있어서
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.getResult()!=null){
                                user_nick = task.getResult().getString("nickname");
                                if(user_nick!=null&&user_nick.length()!=0) {
                                    Log.e(TAG, "닉네임오기성공 - " + user_nick.length());
                                    Intent intent = new Intent(SetNicknameActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // 현재 액티비티 파괴
                                }
                                else{
                                    layout_confirm.setVisibility(View.VISIBLE);
                                    et_nickname.setVisibility(View.VISIBLE);
                                    tv_confirm.setVisibility(View.VISIBLE);

                                    tv_confirm.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String curNick=et_nickname.getText().toString();
                                            if(curNick!=null||curNick.length()==0){
                                                Log.e(TAG, "닉네임없음");
                                                Map<String,String> map = new HashMap<String,String>();
                                                map.put("nickname",curNick);
                                                Log.e("Setnickname", String.valueOf(map));
                                                mStore.collection("user").document(mAuth.getUid()).update("nickname", curNick);
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
        else {
            Log.e(TAG,"계정정보없음 " );
            SavedSharedPreferences.setUserName(getApplicationContext(),null);
            Intent intent = new Intent(SetNicknameActivity.this, KeepLoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}

