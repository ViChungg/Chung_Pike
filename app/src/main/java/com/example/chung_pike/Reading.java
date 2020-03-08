package com.example.chung_pike;

import java.time.LocalDateTime;

public class Reading {
    String userId;
    String userName;
    String dateTime;
    float systolicReading;
    float diastolicReading;
    String condition;

    public Reading() {}

    public Reading(String userId, String userName, String dateTime, float systolicReading, float diastolicReading,
                   String condition) {
        this.userId = userId;
        this.userName = userName;
        this.dateTime = dateTime;
        this.systolicReading = systolicReading;
        this.diastolicReading = diastolicReading;
        this.condition = condition;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public float getSystolicReading() {
        return systolicReading;
    }

    public void setSystolicReading(float systolicReading) {
        this.systolicReading = systolicReading;
    }

    public float getDiastolicReading() {
        return diastolicReading;
    }

    public void setDiastolicReading(float diastolicReading) {
        this.diastolicReading = diastolicReading;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
