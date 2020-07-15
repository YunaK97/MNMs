<?php
    $con = mysqli_connect("localhost", "jennyk97", "a2743275!", "jennyk97");
    mysqli_query($con,'SET NAMES utf8');

    $memID = $_POST["memID"];
    $memPW = $_POST["memPW"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM MEMBER WHERE memID = ? AND memPW = ?");
    mysqli_stmt_bind_param($statement, "ss", $memID, $memPW);
    mysqli_stmt_execute($statement);


    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $memID, $memPW, $memName, $userEmail);

    $response = array();
    $response["success"] = false;
 
    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["memID"] = $memID;
        $response["memPW"] = $memPW;
        $response["memName"] = $memName;
        $response["memEmail"] = $memEmail;        
    }
if (mysqli_connect_errno($conn)) {

echo "데이터베이스 연결 실패: " . mysqli_connect_error();

} else {

echo "데이터베이스 연결 성공";

}

    echo json_encode($response);



?>