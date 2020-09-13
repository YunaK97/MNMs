package kr.hongik.mnms.daily;

import java.io.Serializable;

import kr.hongik.mnms.Group;

public class DailyGroup extends Group implements Serializable {
    String DID; //daily ID

    public String getDID() {
        return DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }
}