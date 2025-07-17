<?php
function db_connect()
{
    $db_host = 'localhost';
    $db_user = 'root';
    $db_password = '';
    $db_name = 'summithub';
    $db_connection = new mysqli($db_host, $db_user, $db_password, $db_name);
    // Check connection
    if ($db_connection->connect_error) {
        die("Connection failed: " . $db_connection->connect_error);
    }
    return $db_connection;
}
