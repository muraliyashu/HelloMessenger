package com.muraliyashu.hellomessenger;

/**
 * Created by MuraliYashu on 9/1/2017.
 */

public class message {
    String message;
    String userNumber;
    String userID;
    public message()
    {

    }
    public message(String message, String userNumber, String userID)
    {
        this.message = message;
        this.userNumber = userNumber;
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public String getUserID() {
        return userID;
    }
}
class contacts {
    String name;
    String number;
    public contacts()
    {

    }
    public contacts(String name, String number)
    {
        this.name = name;
        this.number = number;
    }
    public String getName() {
        return name;
    }
    public String getNumber() {
        return number;
    }
}


class profiles {
    String name;
    String number;
    public profiles()
    {

    }
    public profiles(String name, String number)
    {
        this.name = name;
        this.number = number;
    }
    public String getName() {
        return name;
    }
    public String getNumber() {
        return number;
    }
}

class onlineStatus {
    String status;
    String number;
    public onlineStatus()
    {

    }
    public onlineStatus(String status, String number)
    {
        this.status = status;
        this.number = number;
    }
    public String getStatus() {
        return status;
    }
    public String getNumber() {
        return number;
    }
}

class imageURL {
    String URL;
    String number;
    String userName;
    String onlineStatus;
    public imageURL()
    {

    }
    public imageURL(String number, String onlineStatus, String URL, String userName)
    {
        this.URL = URL;
        this.number = number;
        this.onlineStatus = onlineStatus;
        this.userName = userName;
    }
    public String getURL() {
        return URL;
    }
    public String getNumber() {
        return number;
    }
    public String getUserName() {
        return userName;
    }
    public String getOnlineStatus() {
        return onlineStatus;
    }
}