package kr.hongik.mnms.membership;

import java.io.Serializable;

import kr.hongik.mnms.Group;

public class MembershipGroup extends Group implements Serializable {
    private int MID; //membership ID
    private String president; //방장
    private String payDay; //회비날

    //매일,매주,매월,매년
    private String payDuration;

    private int fee; //회비
    private int notSubmit; //최대 미납횟수 (누적개수)
    private String accountNum; //membership 계좌번호

    public int getMID() {
        return MID;
    }

    public void setMID(int MID) {
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

    public String getPayDuration() {
        return payDuration;
    }

    public void setPayDuration(String payDuration) {
        this.payDuration = payDuration;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public int getNotSubmit() {
        return notSubmit;
    }

    public void setNotSubmit(int notSubmit) {
        this.notSubmit = notSubmit;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }
}