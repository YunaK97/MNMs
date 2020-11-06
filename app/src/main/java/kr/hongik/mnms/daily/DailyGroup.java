package kr.hongik.mnms.daily;

import java.io.Serializable;

import kr.hongik.mnms.Group;

public class DailyGroup extends Group implements Serializable {
    private int DID; //daily ID
    /*
    * 카운트 - 정산할때 -- -> 0
    *
    * 친구가 되어있어야 송금가능
    *
    * */

    public int getDID() {
        return DID;
    }

    public void setDID(int DID) {
        this.DID = DID;
    }
}