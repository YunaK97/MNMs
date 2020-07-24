package com.example.teamtemplate;

import java.io.Serializable;

public class Transaction implements Serializable {
    String transactID;
    String transactHistroy;
    String transactMoney;
    String transactVersion;
    String since;
    String accountNum;

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

    public String getTransactVersion() {
        return transactVersion;
    }

    public void setTransactVersion(String transactVersion) {
        this.transactVersion = transactVersion;
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
}
