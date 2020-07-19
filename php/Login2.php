<?php
    $con = mysqli_connect("localhost", "jennyk97", "a2743275!", "jennyk97");
    mysqli_query($con,'SET NAMES utf8');

    $memID = $_POST["memID"];
    $memPW = $_POST["memPW"];
    
    $statement = mysqli_prepare($con, "SELECT * FROM MEMBER2 WHERE memID = ? AND memPW = ?");
    mysqli_stmt_bind_param($statement, "ss", $memID, $memPW);
    mysqli_stmt_execute($statement);


    mysqli_stmt_store_result($statement);
    mysqli_stmt_bind_result($statement, $memID, $memPW, $memName, $memEmail);

    $response = array();
    $response["success"] = false;
 
    while(mysqli_stmt_fetch($statement)) {
        $response["success"] = true;
        $response["memID"] = $memID;
        $response["memPW"] = $memPW;
        $response["memName"] = $memName;
        $response["memEmail"] = $memEmail;        
    }


    echo json_encode($response);



?>