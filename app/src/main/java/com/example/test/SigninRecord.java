package com.example.test;

public class SigninRecord {
    private int rank;
    private String time;

    public SigninRecord(int rank, String time){
        this.rank = rank;
        this.time = time;
    }

    public int getRank() {
        return rank;
    }

    public String getTime() {
        return time;
    }
}
