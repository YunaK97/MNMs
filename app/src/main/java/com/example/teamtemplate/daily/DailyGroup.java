package com.example.teamtemplate.daily;

import com.example.teamtemplate.Group;

import java.io.Serializable;

public class DailyGroup extends Group implements Serializable {
    String DID; //daily ID
    int money; // 총 사용한 돈 -> 그냥 계좌내역 받아와서 계산할까,,,?

    public String getDID() {
        return DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

}
