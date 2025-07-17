<?php
require_once 'db.php';
require_once 'json.php';

// Handle POST API
if ($_SERVER['REQUEST_METHOD'] === 'POST' && str_contains($_SERVER['PHP_SELF'], "api.php")) {

    $request = json_decode(file_get_contents('php://input'), true);

    if (array_key_exists("action", $request) && $request["action"] == "getEntries" && isset($request["table"])) {
        getEntries($request["table"]);
    } else if (!array_key_exists("action", $request)) {
        error("No specified action");
    } else if (array_key_exists("action", $request) && $request["action"] == "getSchedule") {
        getSchedule();
    } else if (array_key_exists("action", $request) && $request["action"] == "sendQuestion" && isset($request["user_id"]) && isset($request["article_id"])  && isset($request["content"])) {
            sendQuestion($request["user_id"], $request["article_id"], $request["content"]);
    } else if (array_key_exists("action", $request) && $request["action"] == "getQuestions" && isset($request["article_id"])) {
            getQuestions($request["article_id"]);
    } else {
        error("Not enough arguments passed");
    }
}

function getEntries($table)
{
    // Connect to database
    $db_connection = db_connect();
    mysqli_set_charset($db_connection, "utf8mb4");

    // Sanitize table name
    $table = mysqli_real_escape_string($db_connection, $table);

    // Check if table is valid
    if (!in_array($table, ["sessions", "articles", "rooms", "authors", "conferences"])) return "No table found";

    // SQL query
    if ($table == "articles") {
        $sql = "SELECT a.id, a.title, a.date_published, a.session_id, a.abstract, a.pdf, GROUP_CONCAT(au.fullname SEPARATOR ', ') AS 'authors'
                FROM articles a 
                LEFT JOIN article_authors aa ON a.id = aa.article_id 
                LEFT JOIN authors au ON au.id = aa.author_id 
                GROUP BY a.id";
    } elseif ($table == "sessions") {
        $sql = "SELECT s.id, s.title, s.datetime_start, s.datetime_end, r.name AS 'room_name'
                FROM sessions s 
                LEFT JOIN rooms r ON s.room_id = r.id";
    } elseif ($table == "conferences") {
        $sql = "SELECT c.id, c.name, c.information
                FROM conferences c";
    } else {
        $sql = "SELECT * FROM $table";
    }

    // Get all entries from database
    $query = $db_connection->query($sql);
    if ($query) {
        if ($query->num_rows != 0) {
            $entries = [];
            while ($row = $query->fetch_assoc()) {
                array_push($entries, $row);
            }
            $db_connection->close();
            return successJSON($entries);
        } else {
            return error("No $table to display");
        }
    } else {
        $error = "Error: " . $sql . "<br>" . $db_connection->error;
        $db_connection->close();
        return error($error);
    }
}

function getSchedule()
{
    // Connect to database
    $db_connection = db_connect();
    mysqli_set_charset($db_connection, "utf8mb4");

    // Get all articles
    $articles = [];
    $sql = "SELECT articles.*, GROUP_CONCAT(fullname SEPARATOR ', ') as 'authors' from articles LEFT JOIN article_authors ON articles.id = article_authors.article_id LEFT JOIN authors on authors.id = article_authors.author_id GROUP BY articles.id;";
    $query = $db_connection->query($sql);
    if ($query) {
        if ($query->num_rows != 0) {
            while ($row = $query->fetch_assoc()) {
                array_push($articles, $row);
            }
        }
    } else return error($db_connection->error);


    // Get all sessions
    $sessions = [];
    $sql = "SELECT sessions.id, sessions.title, sessions.datetime_start, sessions.datetime_end, rooms.name as 'room_name' from sessions LEFT JOIN rooms ON sessions.room_id = rooms.id";
    $query = $db_connection->query($sql);
    if ($query) {
        if ($query->num_rows != 0) {
            while ($row = $query->fetch_assoc()) {
                array_push($sessions, $row);
            }
        }
    } else return error($db_connection->error);


    // Construct the array to be organized by sessions and articles
    foreach ($articles as $article) {
        for ($i = 0; $i < count($sessions); $i++) {  // don't use foreach so that we change the sessions that really are in the array
            if (!array_key_exists("articles", $sessions[$i])) $sessions[$i]["articles"] = [];
            if ($sessions[$i]["id"] == $article["session_id"]) {
                array_push($sessions[$i]["articles"], $article);
            }
        }
    }
    echo json_encode(["success" => 1, "response" => $sessions]);
} 

function getQuestions($article_id)
{
    // Connect to database
    $db_connection = db_connect();
    mysqli_set_charset($db_connection, "utf8mb4");

    // SQL query
    $sql = "SELECT aq.id, aq.user_id, u.username, aq.article_id, aq.content, aq.approved
    FROM article_questions as aq LEFT JOIN users as u ON aq.user_id = u.id 
    WHERE aq.approved = true and aq.article_id = $article_id";

    // Get all entries from database
    $query = $db_connection->query($sql);
    if ($query) {
        $entries = [];
        if ($query->num_rows != 0) {
            while ($row = $query->fetch_assoc()) {
                array_push($entries, $row);
            }
        }
        $db_connection->close();
        return successJSON($entries);
    } else {
        $error = "Error: " . $sql . "<br>" . $db_connection->error;
        $db_connection->close();
        return error($error);
    }
}

function sendQuestion($user_id, $article_id, $content)
{
    // Connect to database
    $db_connection = db_connect();
    mysqli_set_charset($db_connection, "utf8mb4");

    // Sanitize input
    $user_id = mysqli_real_escape_string($db_connection, $user_id);
    $article_id = mysqli_real_escape_string($db_connection, $article_id);
    $content = mysqli_real_escape_string($db_connection, $content);

    $sql = "INSERT INTO article_questions (user_id, article_id, content, approved) VALUES ('$user_id', '$article_id', '$content', false)";

    $query = $db_connection->query($sql);
    if ($query) {
        success("Question sent, you have to wait for approval");
        return;
    } else {
        $error = "Error: " . $sql . "<br>" . $db_connection->error;
        $db_connection->close();
        error($error);
        return;
    }
}