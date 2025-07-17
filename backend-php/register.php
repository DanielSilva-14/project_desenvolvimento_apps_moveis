<?php
if(!empty($_POST['fullname']) && !empty($_POST['email']) && !empty($_POST['username']) && !empty($_POST['password'])) {
    $con = mysqli_connect("localhost", "root", "", "summithub");
    $fullname = $_POST['fullname'];
    $email = $_POST['email'];
    $username = $_POST['username'];
    $password = $_POST['password'];

        // Hash the password before storing it in the database
        $hashed_password = password_hash($password, PASSWORD_DEFAULT);

    if ($con) {
        $sql = "INSERT INTO users (fullname, email, username, password) VALUES ('".$fullname."', '".$email."', '".$username."', '".$hashed_password."')";
        if(mysqli_query($con, $sql)){
            echo "success";
        } else echo "Registration failed";
    } else echo "Database connection failed";
} else echo "All fields are required";
?>