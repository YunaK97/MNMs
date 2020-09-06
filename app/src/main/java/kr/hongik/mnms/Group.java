package kr.hongik.mnms;

import java.io.Serializable;

public class Group implements Serializable {
    private String groupName; //그룹이름
    private String GID; //그룹 ID
    private String time; //그룹 생성 시간

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getGID() {
        return GID;
    }

    public void setGID(String GID) {
        this.GID = GID;
    }
}