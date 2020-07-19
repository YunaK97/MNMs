<?php 
    $con = mysqli_connect("localhost", "jennyk97", "a2743275!", "jennyk97");
    mysqli_query($con,'SET NAMES utf8');

    $memID = $_POST["memID"];
    $memPW = $_POST["memPW"];
    $memName = $_POST["memName"];
    $memEmail = $_POST["memEmail"];
    $accountNum=$_POST["accountNum"];
    $accountBank=$_POST["accountBank"];
    $accountBalance=$_POST["accountBalance"];
    $accountPassword=$_POST["accountPassword"];
    $statement1 = mysqli_prepare($con, "INSERT INTO 'MEMBER'('memID','memPW','memName','memEmail','accountNum') VALUES (?,?,?,?,?)");
    $statement2 = mysqli_prepare($con, "INSERT INTO 'ACCOUNT'('accountNum','accountBank','accountBalance','accountPassword') VALUES (?,?,?,?)");
    mysqli_stmt_bind_param($statement2, "ssis", $accountNum, $accountBank, $accountBalance, $accountPassword);
    mysqli_stmt_execute($statement2);


  mysqli_stmt_bind_param($statement1, "sssss", $memID, $memPW, $memName, $memEmail,$accountNum);
    mysqli_stmt_execute($statement1);
    $response = array();
    $response["success"] = true;
 
   
    echo json_encode($response);
?>