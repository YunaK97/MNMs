package kr.hongik.mnms.daily;

import java.io.Serializable;

import kr.hongik.mnms.Group;

public class DailyGroup extends Group implements Serializable {
    private int DID; //daily ID

    public int getDID() {
        return DID;
    }

    public void setDID(int DID) {
        this.DID = DID;
    }
}