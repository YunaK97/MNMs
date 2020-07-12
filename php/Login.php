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

<?php
    $con = mysqli_connect("localhost", "jennyk97", "a2743275!", "jennyk97");
    mysqli_query($con,'SET NAMES utf8');

    $memID = $_POST["memID"];
    $memPW = $_POST["memPW"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM MEMBER WHERE memID = ? AND memPW = ?");
    mysqli_stmt_bind_param($statement, "ss", $memID, $memPW);
    mysqli_stmt_execute($statement);


    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $memID, $memPW, $memName, $memEmail);

    $response = array();
	$member=new Member;
	
    while(mysqli_stmt_fetch($statement)) {
		$member->setMemName(memName);
		$member->setMemID(memID);
		$member->setMemPW(memPW);
		$member->setMemEmail(memEmail);
		$response["member"]=member;
    }

    echo json_encode($response);



?>
