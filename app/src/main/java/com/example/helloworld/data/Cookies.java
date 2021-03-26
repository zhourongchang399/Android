package com.example.helloworld.data;

import java.util.List;

public class Cookies {
    private static Cookies cookies = new Cookies();
    private void Cookies(){}
    public static Cookies getCookies(){
        return cookies;
    }

    public static void refresh(){
        cookies = new Cookies();
    }

    int userId;
    String username;
    String city;
    String personalProfile;
    String sex;
    String name;
    int age;
    String img;
    List<UserInfo> UserInfos;
    UserInfo userInfo;
    List<Info> infoList;
    List<Integer> ints;

    public List<Integer> getInts() {
        return ints;
    }

    public void setInts(List<Integer> ints) {
        this.ints = ints;
    }

    public List<Info> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<Info> infoList) {
        this.infoList = infoList;
    }

    public static void setCookies(Cookies cookies) {
        Cookies.cookies = cookies;
    }

    public UserInfo getUserInfo() {
        userInfo = new UserInfo();
        userInfo.setAge(age);
        userInfo.setUserId(userId);
        userInfo.setName(name);
        userInfo.setUsername(username);
        userInfo.setSex(sex);
        userInfo.setPersonalProfile(personalProfile);
        userInfo.setCity(city);
        userInfo.setImg(img);
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public List<UserInfo> getUserInfos() {
        return UserInfos;
    }

    public void setUserInfos(List<UserInfo> userInfos) {
        UserInfos = userInfos;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPersonalProfile() {
        return personalProfile;
    }

    public void setPersonalProfile(String personalProfile) {
        this.personalProfile = personalProfile;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void reset(){
        refresh();
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }
}
