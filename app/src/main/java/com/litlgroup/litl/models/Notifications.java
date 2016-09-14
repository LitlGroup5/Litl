package com.litlgroup.litl.models;

/**
 * Created by monusurana on 9/12/16.
 */
public class Notifications {
    private String id;
    private Boolean accepted;
    private String taskId;

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
