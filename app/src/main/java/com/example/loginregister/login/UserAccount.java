package com.example.loginregister.login;

import com.example.loginregister.Table;

import java.util.ArrayList;
import java.util.Map;

// 사용자 계정 정보 모델 클래스
public class UserAccount {
    private String idToken; // Firebase Uid (고유 토큰 정보)
    private String emailId; // 이메일 아이디
    private String password; // 비밀번호
    private String nickname;
    private ArrayList<String> Liked_Post; // 좋아요 누른 게시물의 Post_Id
    private Map<String, Table> tableMap;

    public UserAccount() { }

    public UserAccount(String idToken, String emailId, String password, String nickname, ArrayList<String> liked_Post, Map<String, Table> tableMap) {
        this.idToken = idToken;
        this.emailId = emailId;
        this.password = password;
        this.nickname = nickname;
        this.Liked_Post = liked_Post;
        this.tableMap = tableMap;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ArrayList<String> getLiked_Post() {
        return Liked_Post;
    }

    public void setLiked_Post(ArrayList<String> liked_Post) {
        Liked_Post = liked_Post;
    }

    public Map<String, Table> getTableMap() {
        return tableMap;
    }

    public void setTableMap(Map<String, Table> tableMap) {
        this.tableMap = tableMap;
    }
}
