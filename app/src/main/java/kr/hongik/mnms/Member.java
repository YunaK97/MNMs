package kr.hongik.mnms;

import java.io.Serializable;

public class Member implements Serializable {
    private static final long serialVersionUID = 1L;

    private String memName;
    private String memID;
    private String memPW;
    private String memEmail;
    private String memSsn; //주민번호
    private String accountNum;
    private boolean isChecked;
    private String ip;

    //친구 그룹

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMemName() {
        return memName;
    }

    public void setMemName(String memName) {
        this.memName = memName;
    }

    public String getMemID() {
        return memID;
    }

    public void setMemID(String memID) {
        this.memID = memID;
    }

    public String getMemPW() {
        return memPW;
    }

    public void setMemPW(String memPW) {
        this.memPW = memPW;
    }

    public String getMemEmail() {
        return memEmail;
    }

    public void setMemEmail(String memEmail) {
        this.memEmail = memEmail;
    }

    public String getMemSsn() {
        return memSsn;
    }

    public void setMemSsn(String memSsn) {
        this.memSsn = memSsn;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
