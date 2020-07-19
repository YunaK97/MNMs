<?php
    $con = mysqli_connect("localhost", "jennyk97", "a2743275!", "jennyk97");
    mysqli_query($con,'SET NAMES utf8');

    $memID = $_POST["memID"];
    $memPW = $_POST["memPW"];

    $statement = mysqli_prepare($con, "SELECT * FROM MEMBER WHERE memID = ? AND memPW = ?");
    mysqli_stmt_bind_param($statement, "ss", $memID, $memPW);
    mysqli_stmt_execute($statement);

    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $memID, $memPW, $memName, $memEmail, $accountNum);

    $response = array();
    $response["success"] = false;
    $accountNum2=$accountNum;
    $statement2 = mysqli_prepare($con, "SELECT accountBalance FROM MEMBER,ACCOUNT WHERE MEMBER.memID=? AND MEMBER.accountNum=ACCOUNT.accountNum");
    mysqli_stmt_bind_param($statement2,"s", $memID);
    mysqli_stmt_execute($statement2);

    mysqli_stmt_store_result($statement2);
    mysqli_stmt_bind_result($statement2,$accountBalance);

    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["memID"] = $memID;
        $response["memPW"] = $memPW;
        $response["memName"] = $memName;
        $response["memEmail"] = $memEmail;
        $response["accountNum"]=$accountNum;
    }
   while(mysqli_stmt_fetch($statement2)){
       $response["accountBalance"]=$accountBalance;
   }
    echo json_encode($response);


?>