package com.example.test;

public class TaskActivity {
    private String taskName;
    private String participantNum;
    private String participateOrNot;
    private String activityExperience;
    private String timeLimit;
    public String taskId;
    public String taskContent;

    public TaskActivity(String taskName, String participantNum, String participateOrNot,
                        String activityExperience, String timeLimit){
        this.taskName = taskName;
        this.participantNum = participantNum;
        this.participateOrNot = participateOrNot;
        this.activityExperience = activityExperience;
        this.timeLimit = timeLimit;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setParticipateOrNot(String participateOrNot) {
        this.participateOrNot = participateOrNot;
    }

    public void setParticipantNum(String participantNum) {
        this.participantNum = participantNum;
    }

    public String getTaskId(){
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getParticipantNum() {
        return participantNum;
    }

    public String getParticipateOrNot() {
        return participateOrNot;
    }

    public String getActivityExperience() {
        return activityExperience;
    }

    public String getTimeLimit() {
        return timeLimit;
    }
}
