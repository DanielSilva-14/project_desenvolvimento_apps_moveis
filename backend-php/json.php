<?php

function error($message)
{
    $response = array(
        "success" => false,
        "message" => $message
    );

    echo (json_encode($response));
}

function success($message)
{
    $response = array(
        "success" => true,
        "message" => $message
    );

    echo (json_encode($response));
}

function successJSON($json)
{
    $response = array(
        "success" => true,
        "response" => $json
    );

    echo (json_encode($response));
}
