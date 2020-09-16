package kr.hongik.mnms;

import java.io.Serializable;

public class Group implements Serializable {
    private String groupName; //그룹이름
    private int GID; //그룹 ID

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGID() {
        return GID;
    }

    public void setGID(int GID) {
        this.GID = GID;
    }
}