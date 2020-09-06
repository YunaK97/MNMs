package kr.hongik.mnms;

import java.io.Serializable;

public class Transaction implements Serializable {
    String transactID; // primary key, 순서대로 생성
    String transactHistroy; // 거래 내역 (회비, 밥,,,)
    String transactMoney; // 돈 (+,-)
    String since; //사용한 날짜
    String accountNum; //사용한 계좌 - 개인 계좌번호, membership계좌번호
    String MID; // MembershipGroup의 ID
    String DID; // DailyGroup의 ID


    public String getTransactID() {
        return transactID;
    }

    public void setTransactID(String transactID) {
        this.transactID = transactID;
    }

    public String getTransactHistroy() {
        return transactHistroy;
    }

    public void setTransactHistroy(String transactHistroy) {
        this.transactHistroy = transactHistroy;
    }

    public String getTransactMoney() {
        return transactMoney;
    }

    public void setTransactMoney(String transactMoney) {
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

    public String getMID() {
        return MID;
    }

    public void setMID(String MID) {
        this.MID = MID;
    }

    public String getDID() {
        return DID;
    }

    public void setDID(String DID) {
        this.DID = DID;
    }

}
