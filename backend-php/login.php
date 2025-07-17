<?php
if(!empty($_POST['username']) && !empty($_POST['password'])) {
    $username = $_POST['username'];
    $password = $_POST['password'];
    $result = array();
    $con = mysqli_connect("localhost", "root", "", "summithub");
    if ($con) {  
        $sql = "select * from users where username = '".$username."'";
        $res = mysqli_query($con, $sql);
        if(mysqli_num_rows($res) != 0){
            $row = mysqli_fetch_assoc($res);
            if($username == $row['username'] && password_verify($password, $row['password'])){
                try {
                    $apiKey = bin2hex(random_bytes(23));
                } catch (Exception $e) {
                    $apiKey = bin2hex(uniqid($email, true));
                }
                $sqlUpdate = "update users set apiKey = '".$apiKey."' where username = '".$username."'";
                if (mysqli_query($con, $sqlUpdate)){
                    $result = array("status"=>"success", "message"=>"Login successful",
                        "fullname"=>$row['fullname'], "email"=>$row['email'], "username"=>$row['username'], "apiKey"=>$row['apiKey'], "user_id"=>$row['id']);
                } else $result = array("status" => "failed", "message" => "Login failed, try again");
            } else $result = array("status" => "failed", "message" => "Retry with correct email and password");
        } else $result = array("status" => "failed", "message" => "Retry with correct email and password");
    } else $result = array("status" => "failed", "message" => "Database connection failed");
} else $result = array("status" => "failed", "message" => "All fields are required");

echo json_encode($result, JSON_PRETTY_PRINT);
?>