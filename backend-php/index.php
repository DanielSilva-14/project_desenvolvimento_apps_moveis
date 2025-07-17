<?php

require_once("db.php");

function login_admin($username, $password)
{
    // Check username and password
    if ($password == "admin" && $username == "admin") {
        // Redirect to dashboard page
        session_start();
        $_SESSION["dashboard_username"] = $username;
        header("Location: dashboard");
        exit();
    } else {
        return "Wrong credentials";
    }
}

?>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="styles.css">
    <title>SummitHub</title>
</head>

<body>
    <div class="login-container">
        <form method="post">
            <input type="text" name="username" id="username" placeholder="Username" autocomplete="username"><br><br>
            <input type="password" name="password" id="password" placeholder="Password" autocomplete="current-password">
            <a class="password-visibility" onclick="togglePasswordVis()">&nbsp;</a>
            <br><br>
            <?php
            if ($_REQUEST) {
                echo '<span class="auth-error">' . login_admin($_REQUEST['username'], $_REQUEST['password']) . '</span>';
            }
            ?>
            <br><br>
            <input class="button" type="submit" value="Login">
        </form>
    </div>

    <script>
        function togglePasswordVis() {
            var passwordInput = document.getElementById('password');
            if (passwordInput.type === "password") {
                passwordInput.type = "text";
            } else {
                passwordInput.type = "password";
            }
        }
    </script>

</body>

</html>