package com.UniPlan.loginregister.UserInfo;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.UniPlan.loginregister.MainActivity;
import com.UniPlan.loginregister.R;
import com.UniPlan.loginregister.Request;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class Fragment_Request extends Fragment implements MainActivity.IOnBackPressed {
    private View view;
    private TextView tv_back,tv_save;
    private EditText et_title,et_content;
    private FragmentTransaction ft;
    private FragmentManager fm;
    private AppCompatDialog progressDialog;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private FirebaseUser mAuth = FirebaseAuth.getInstance().getCurrentUser();

    public Fragment_Request() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_framgent__request, container, false);
        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        tv_back = view.findViewById(R.id.btn_back);
        tv_save = view.findViewById(R.id.request_save);
        et_title = view.findViewById(R.id.et_title);
        et_content = view.findViewById(R.id.et_contents);
        ((MainActivity)getActivity()).setBackPressedlistener(this);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.remove(Fragment_Request.this).commit();
                fm.popBackStack();
            }
        });

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new AppCompatDialog(getContext());
                Onprogress(getActivity(),"로딩중...");
                String title = et_title.getText().toString();
                String content = et_content.getText().toString();

                Request request = new Request(title,content);
                String mId = mStore.collection("request").document().getId();
                mStore.collection("request").document(mId).set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        ft.remove(Fragment_Request.this).commit();
                        fm.popBackStack();
                        progressOFF();
                    }
                });
            }
        });
        return view;
    }

    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).setBackPressedlistener(null);
    }

    @Override
    public void onBackPressed() {
        ft.remove(Fragment_Request.this).commit();
        fm.popBackStack();
    }

        void Onprogress(Activity activity, String message){

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {

        } else {
            //이 밑부분 떼서 작업전에 AppcompatDialog 변수선언해주고 progressDialog 먼저 만들고
            // 작업시작할때 Onprogress 넣어주고 작업끝나면 밑에 progressOFF 넣어주면됩니다.
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}