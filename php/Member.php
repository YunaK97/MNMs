<?php
class Member {

    // 프로퍼티(멤버 변수)
	private $memName;
    private $memID;
    private $memPW;
    private $memEmail;
    private $memSsn;
	
    // 메소드
	public function getMemName() {
        return $memName;
    }
    public function setMemName($memName) {
        $this->memName = memName;
    }
    public function getMemID() {
        return $memID;
    }
    public function setMemID($memID) {
        $this->memID = memID;
    }
    public function getMemPW() {
        return $memPW;
    }
    public function setMemPW($memPW) {
        $this->memPW = memPW;
    }
    public function getMemEmail() {
        return memEmail;
    }
    public function setMemEmail($memEmail) {
        $this->memEmail = memEmail;
    }
    public function getMemSsn() {
        return $memSsn;
    }
    public function setMemSsn($memSsn) {
        $this->memSsn = memSsn;
    }
}
?>
