package com.example.onlinebookexchangesystem;

import com.google.android.gms.maps.model.LatLng;

public class UsersModel {
    private String UserName;
    private String UserEmail;
    private String UserGander;
    private String UserNumber;
    private String UserID;
    private double UserLongitude;
    private double UserLatitude;

    public UsersModel() {
    }

    public UsersModel(String userName, String userEmail, String userGander, String userNumber, String userID, double userLongitude, double userLatitude) {
        UserName = userName;
        UserEmail = userEmail;
        UserGander = userGander;
        UserNumber = userNumber;
        UserID = userID;
        UserLongitude = userLongitude;
        UserLatitude = userLatitude;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserGander() {
        return UserGander;
    }

    public void setUserGander(String userGander) {
        UserGander = userGander;
    }

    public String getUserNumber() {
        return UserNumber;
    }

    public void setUserNumber(String userNumber) {
        UserNumber = userNumber;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public double getUserLongitude() {
        return UserLongitude;
    }

    public void setUserLongitude(double userLongitude) {
        UserLongitude = userLongitude;
    }

    public double getUserLatitude() {
        return UserLatitude;
    }

    public void setUserLatitude(double userLatitude) {
        UserLatitude = userLatitude;
    }
}
