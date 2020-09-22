package com.example.test;

public class Course {
    private int imageId = -1;
    private String imgFilePath = "";
    private String classId;
    private String courseName;
    private String teacherName;
    private String className;
    public String teacherPhone;

    public Course(int imageId, String courseName, String teacherName, String className, String classId){
        this.classId = classId;
        this.imageId = imageId;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.className = className;
    }

    public Course(String imgFilePath, String courseName, String teacherName, String className, String classId){
        this.classId = classId;
        this.imgFilePath = imgFilePath;
        this.courseName = courseName;
        this.teacherName = teacherName;
        this.className = className;
    }

    public String getTeacherPhone() {
        return teacherPhone;
    }

    public String getClassId() {
        return classId;
    }

    public String getImgFilePath(){
        return imgFilePath;
    }

    public int getImageId(){
        return imageId;
    }

    public String getCourseName(){
        return courseName;
    }

    public String getTeacherName(){
        return teacherName;
    }

    public String getClassName(){
        return className;
    }
}
