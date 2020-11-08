package kr.hongik.mnms.daily.ui.home;

import kr.hongik.mnms.Member;

public class DutchMember extends Member {
    private int usedMoney, rsMoney,tmpRSMoney;
    private boolean checkSend;

    public boolean isCheckSend() {
        return checkSend;
    }

    public void setCheckSend(boolean checkSend) {
        this.checkSend = checkSend;
    }

    public int getTmpRSMoney() {
        return tmpRSMoney;
    }

    public void setTmpRSMoney(int tmpRSMoney) {
        this.tmpRSMoney = tmpRSMoney;
    }

    public int getUsedMoney() {
        return usedMoney;
    }

    public void setUsedMoney(int usedMoney) {
        this.usedMoney = usedMoney;
    }

    public int getRsMoney() {
        return rsMoney;
    }

    public void setRsMoney(int rsMoney) {
        this.rsMoney = rsMoney;
    }
}