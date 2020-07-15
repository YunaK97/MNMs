<?php 
    $con = mysqli_connect("localhost", "jennyk97", "a2743275!", "jennyk97");
    mysqli_query($con,'SET NAMES utf8');

    $memID = $_POST["memID"];
    $memPW = $_POST["memPW"];
    $memName = $_POST["memName"];
    $memEmail = $_POST["memEmail"];

    $statement = mysqli_prepare($con, "INSERT INTO MEMBER2 VALUES (?,?,?,?)");
    mysqli_stmt_bind_param($statement, "sssi", $memID, $memPW, $memName, $memEmail);
    mysqli_stmt_execute($statement);

    $response = array();
    $response["success"] = true;
 
   
    echo json_encode($response);


?>