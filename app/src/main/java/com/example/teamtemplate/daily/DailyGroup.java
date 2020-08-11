package com.example.teamtemplate.daily;

import java.io.Serializable;

public class DailyGroup implements Serializable {
    String DID;
    String money;
    String dutchPay;
    String GID;

    public String getDID() {
        return DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDutchPay() {
        return dutchPay;
    }

    public void setDutchPay(String dutchPay) {
        this.dutchPay = dutchPay;
    }

    public String getGID() {
        return GID;
    }

    public void setGID(String GID) {
        this.GID = GID;
    }

}
