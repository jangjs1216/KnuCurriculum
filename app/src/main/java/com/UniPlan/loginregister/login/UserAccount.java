package com.UniPlan.loginregister.login;

import com.UniPlan.loginregister.Table;


import java.util.ArrayList;
import java.util.Map;

// 사용자 계정 정보 모델 클래스
public class UserAccount {
    private String idToken; // Firebase Uid (고유 토큰 정보)
    private String emailId; // 이메일 아이디
    private String password; // 비밀번호
    private String nickname;
    private ArrayList<String> Liked_Post; // 좋아요 누른 게시물의 Post_Id 경무 내가누른 좋아요 보기위해 새로운 변수생성
    private ArrayList<String> Mypost;
    private ArrayList<String> Subscribed_Post;
    private ArrayList<Table> tables;
    private ArrayList<String> tableNames;
    private ArrayList<String> specs;
    private String Major;
    private String taked;
    private String basicTableName;
    private Map<String, String> takenSubject;

    public UserAccount() {
    }

    public UserAccount(String idToken, String emailId, String password, String nickname, ArrayList<String> liked_Post, ArrayList<String> mypost, ArrayList<String> subscribed_Post, ArrayList<Table> tables, ArrayList<String> tableNames, ArrayList<String> specs, String major, String taked, String basicTableName, Map<String, String> takenSubject) {
        this.idToken = idToken;
        this.emailId = emailId;
        this.password = password;
        this.nickname = nickname;
        Liked_Post = liked_Post;
        Mypost = mypost;
        Subscribed_Post = subscribed_Post;
        this.tables = tables;
        this.tableNames = tableNames;
        this.specs = specs;
        Major = major;
        this.taked = taked;
        this.basicTableName = basicTableName;
        this.takenSubject = takenSubject;
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

    public ArrayList<String> getMypost() {
        return Mypost;
    }

    public void setMypost(ArrayList<String> mypost) {
        Mypost = mypost;
    }

    public ArrayList<String> getSubscribed_Post() {
        return Subscribed_Post;
    }

    public void setSubscribed_Post(ArrayList<String> subscribed_Post) {
        Subscribed_Post = subscribed_Post;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }

    public ArrayList<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(ArrayList<String> tableNames) {
        this.tableNames = tableNames;
    }

    public ArrayList<String> getSpecs() {
        return specs;
    }

    public void setSpecs(ArrayList<String> specs) {
        this.specs = specs;
    }

    public String getMajor() {
        return Major;
    }

    public void setMajor(String major) {
        Major = major;
    }

    public String getTaked() {
        return taked;
    }

    public void setTaked(String taked) {
        this.taked = taked;
    }

    public String getBasicTableName() {
        return basicTableName;
    }

    public void setBasicTableName(String basicTableName) {
        this.basicTableName = basicTableName;
    }

    public Map<String, String> getTakenSubject() {
        return takenSubject;
    }

    public void setTakenSubject(Map<String, String> takenSubject) {
        this.takenSubject = takenSubject;
    }
}
