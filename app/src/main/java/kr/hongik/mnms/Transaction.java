package kr.hongik.mnms;

import java.io.Serializable;

public class Transaction implements Serializable {
    private int transactID; // primary key, 순서대로 생성
    private String transactHistroy; // 거래 내역 (회비, 밥,,,)
    private int transactMoney; // 돈 (+,-)
    private String since; //사용한 날짜
    private String accountNum; //사용한 계좌 - 개인 계좌번호, membership계좌번호
    private int MID; // MembershipGroup의 ID
    private int DID; // DailyGroup의 ID

    public int getTransactID() {
        return transactID;
    }

    public void setTransactID(int transactID) {
        this.transactID = transactID;
    }

    public String getTransactHistroy() {
        return transactHistroy;
    }

    public void setTransactHistroy(String transactHistroy) {
        this.transactHistroy = transactHistroy;
    }

    public int getTransactMoney() {
        return transactMoney;
    }

    public void setTransactMoney(int transactMoney) {
        this.transactMoney = transactMoney;
    }

    public String getSince() {
        return since;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public int getMID() {
        return MID;
    }

    public void setMID(int MID) {
        this.MID = MID;
    }

    public int getDID() {
        return DID;
    }

    public void setDID(int DID) {
        this.DID = DID;
    }

}
