package kr.hongik.mnms;

import java.io.Serializable;

public class Account implements Serializable {
    private String accountNum; //계좌번호
    private String accountBank; //은행
    private int accountBalance; //잔액
    private String accountPassword; //계좌 비밀번호

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getAccountBank() {
        if(accountBank.equals("AAAA")){
            return "국민";
        }else if(accountBank.equals("AAAB")){
            return "우리";
        }else if(accountBank.equals("AAAC")){
            return "신한";
        }else if(accountBank.equals("AAAD")){
            return "하나";
        }else if(accountBank.equals("AAAE")){
            return "카카오뱅크";
        }else if(accountBank.equals("AAAF")){
            return "농협";
        }else if(accountBank.equals("AAAG")){
            return "IBK";
        }
        return "MNMs";
    }

    public void setAccountBank(String accountBank) {
        if(accountBank.equals("국민")) {
            this.accountBank = "AAAA";
        }else if(accountBank.equals("우리")){
            this.accountBank="AAAB";
        }else if(accountBank.equals("신한")){
            this.accountBank="AAAC";
        }else if(accountBank.equals("하나")){
            this.accountBank="AAAD";
        }else if(accountBank.equals("카카오뱅크")){
            this.accountBank="AAAE";
        }else if(accountBank.equals("농협")){
            this.accountBank="AAAF";
        }else if(accountBank.equals("IBK")){
            this.accountBank="AAAG";
        }else if(accountBank.equals("MNMS")){
            this.accountBank="AAAH";
        }

        this.accountBank = accountBank;
    }

    public int getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(int accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountPassword() {
        return accountPassword;
    }

    public void setAccountPassword(String accountPassword) {
        this.accountPassword = accountPassword;
    }
}