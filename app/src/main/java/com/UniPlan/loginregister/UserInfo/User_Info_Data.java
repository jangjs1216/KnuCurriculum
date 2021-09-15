package com.UniPlan.loginregister.UserInfo;

public class User_Info_Data {
    private String type;
    private String user_info_title;
    private String user_info_content;

    public User_Info_Data(String type, String user_info_title, String user_info_content) {
        this.type = type;
        this.user_info_title = user_info_title;
        this.user_info_content = user_info_content;
    }

    public String getUser_info_title() {
        return user_info_title;
    }

    public void setUser_info_title(String user_info_title) {
        this.user_info_title = user_info_title;
    }

    public String getUser_info_content() {
        return user_info_content;
    }

    public void setUser_info_content(String user_info_content) {
        this.user_info_content = user_info_content;
    }

    public User_Info_Data(){}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

