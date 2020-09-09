package kr.hongik.mnms.daily;

import kr.hongik.mnms.Group;

import java.io.Serializable;

public class DailyGroup extends Group implements Serializable {
    String DID; //daily ID

    public String getDID() {
        return DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }
}