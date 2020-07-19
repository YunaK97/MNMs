<?php
    $con = mysqli_connect("localhost", "jennyk97", "a2743275!", "jennyk97");
    mysqli_query($con,'SET NAMES utf8');

    $memID = $_POST["memID"];
    
    $statement = mysqli_prepare($con, "SELECT memID FROM MEMBER WHERE memID = ?");
    mysqli_stmt_bind_param($statement, "s", $memID);
    mysqli_stmt_execute($statement);

    $result=mysqli_stmt_get_result($statement);
    
   $response=array();
    if(mysqli_num_rows($result)>0){
         $response["success"]= false;
    }else{
          $response["success"]=true;
    }
    echo json_encode($response);

?>