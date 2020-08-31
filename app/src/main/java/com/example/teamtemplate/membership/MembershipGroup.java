package com.example.teamtemplate.membership;

import com.example.teamtemplate.Group;

import java.io.Serializable;

public class MembershipGroup extends Group implements Serializable {
    String MID; //membership ID
    String president; //방장
    String payDay; //회비날
    int memberMoney; //회비
    int totalMoney; //총액 -> 계좌내역 받아와서 계산할까?
    int notSubmit; //최대 미납횟수

    public String getMID() {
        return MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }

    public String getPresident() {
        return president;
    }

    public void setPresident(String president) {
        this.president = president;
    }

    public String getPayDay() {
        return payDay;
    }

    public void setPayDay(String payDay) {
        this.payDay = payDay;
    }

    public int getMemberMoney() {
        return memberMoney;
    }

    public void setMemberMoney(int memberMoney) {
        this.memberMoney = memberMoney;
    }

    public int getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getNotSubmit() {
        return notSubmit;
    }

    public void setNotSubmit(int notSubmit) {
        this.notSubmit = notSubmit;
    }

}
