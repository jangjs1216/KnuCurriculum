package com.example.loginregister.Notice_B;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.loginregister.R;
import com.example.loginregister.UserInfo.Fragment_Edit_User_Info;

public class Fragment_Post_Update extends Fragment {


    private View view;
    private EditText et_title;
    private EditText et_content;
    private String title;
    private String content;
    private Button btn_save;
    private ModifyListener modifylistener;
    private FragmentManager fm;
    private FragmentTransaction ft;

    public Fragment_Post_Update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=  inflater.inflate(R.layout.activity_post__update, container, false);
        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();
        et_title.findViewById(R.id.Post_write_title);
        et_content.findViewById(R.id.Post_write_contents);
        btn_save.findViewById(R.id.Post_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title= et_title.getText().toString();
                content =et_content.getText().toString();
                modifylistener.onPositiveClicked(title,content);
                ft.remove(Fragment_Post_Update.this).commit();
                fm.popBackStack();
            }
        });
        return view;
    }

    public interface ModifyListener{
        void onPositiveClicked(String title,String content);
    }

    public void setModifyListener(ModifyListener modifylistener){
        this.modifylistener = modifylistener;
    }
}