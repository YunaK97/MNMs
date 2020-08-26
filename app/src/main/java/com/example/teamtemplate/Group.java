package com.example.teamtemplate;

import java.io.Serializable;
import java.util.Date;

public class Group implements Serializable {
    private String groupName;
    private String gid;
    private String mid;
    private String did;
    private String time;
    private int notSubmit;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNotSubmit() {
        return notSubmit;
    }

    public void setNotSubmit(int notSubmit) {
        this.notSubmit = notSubmit;
    }
}