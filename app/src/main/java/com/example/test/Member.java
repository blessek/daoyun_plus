package com.example.test;

public class Member {
    private String ranking;
    private String iconFilePath = "";
    private int imageId = -1;
    private String memberName;
    private String stu_id;
    private String experience_score;

    public Member(String ranking, int imageId, String memberName, String stu_id, String experience_score){
        this.ranking = ranking;
        this.imageId = imageId;
        this.memberName = memberName;
        this.stu_id = stu_id;
        this.experience_score = experience_score;
    }

    public Member(String ranking, String iconFilePath, String memberName, String stu_id, String experience_score){
        this.ranking = ranking;
        this.iconFilePath = iconFilePath;
        this.memberName = memberName;
        this.stu_id = stu_id;
        this.experience_score = experience_score;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getIconFilePath() {
        return iconFilePath;
    }

    public String getRanking() {
        return ranking;
    }

    public int getImageId(){
        return imageId;
    }

    public String getMemberName() {
        return memberName;
    }

    public String getStu_id() {
        return stu_id;
    }

    public String getExperience_score() {
        return experience_score;
    }
}
