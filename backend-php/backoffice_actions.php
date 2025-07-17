<?php

require_once("db.php");

// Set default http code to error, and only set 200 on success
http_response_code(400);

//var_dump($_REQUEST);
//var_dump($_FILES);
//var_dump($_SERVER['PHP_SELF']);

//
// Handle Requests
//
if ($_SERVER['REQUEST_METHOD'] === 'POST' && str_contains($_SERVER['PHP_SELF'], "backoffice_actions.php")) {
    if (array_key_exists("action", $_REQUEST)) {
        if ($_REQUEST["action"] == "getEntries") {
            if (isset($_REQUEST["table"])) {
                echo json_encode(getEntries($_REQUEST["table"]));
            } else {
                echo "Missing table";
            }
        } elseif ($_REQUEST["action"] == "getTable") {
            if (isset($_REQUEST["table"])) {
                echo entriesToTable(getEntries($_REQUEST["table"]), $_REQUEST["table"]);
            } else {
                echo "Missing table";
            }
        } elseif ($_REQUEST["action"] == "insert" || $_REQUEST["action"] == "update") {
            if (isset($_REQUEST["table"])) {
                switch ($_REQUEST["table"]) {
                    case 'sessions':
                        if (isset($_REQUEST["title"]) && isset($_REQUEST["datetime_start"]) && isset($_REQUEST["datetime_end"]) && (($_REQUEST["action"] == "update" && isset($_REQUEST["id"])) || $_REQUEST["action"] == "insert")) {
                            if ($_REQUEST["action"] == "insert") echo insertEntry($_REQUEST["table"]);
                            else if ($_REQUEST["action"] == "update") echo updateEntry($_REQUEST["table"]);
                        } else
                            echo "Missing fields";
                        break;
                    case 'articles':
                        if (isset($_REQUEST["title"]) && isset($_REQUEST["date_published"]) && isset($_REQUEST["session_id"]) && isset($_REQUEST["abstract"]) && isset($_FILES["pdf"]) && (($_REQUEST["action"] == "update" && isset($_REQUEST["id"])) || $_REQUEST["action"] == "insert")) {
                            if ($_REQUEST["action"] == "insert") echo insertEntry($_REQUEST["table"]);
                            else if ($_REQUEST["action"] == "update") echo updateEntry($_REQUEST["table"]);
                        } else
                            echo "Missing fields";
                        break;
                    case 'rooms':
                        if (isset($_REQUEST["name"]) && (($_REQUEST["action"] == "update" && isset($_REQUEST["id"])) || $_REQUEST["action"] == "insert")) {
                            if ($_REQUEST["action"] == "insert") echo insertEntry($_REQUEST["table"]);
                            else if ($_REQUEST["action"] == "update") echo updateEntry($_REQUEST["table"]);
                        } else
                            echo "Missing fields";
                        break;
                    case 'authors':
                        if (isset($_REQUEST["fullname"]) && (($_REQUEST["action"] == "update" && isset($_REQUEST["id"])) || $_REQUEST["action"] == "insert")) {
                            if ($_REQUEST["action"] == "insert") echo insertEntry($_REQUEST["table"]);
                            else if ($_REQUEST["action"] == "update") echo updateEntry($_REQUEST["table"]);
                        } else
                            echo "Missing fields";
                        break;
                    default:
                        return "Table not found";
                        break;
                }
            } else {
                echo "Missing table";
            }
        } elseif ($_REQUEST["action"] == "delete") {
            if (isset($_REQUEST["type"]) && isset($_REQUEST["id"])) {
                echo deleteEntry($_REQUEST["type"], $_REQUEST["id"]);
            } else {
                echo "Missing fields";
            }
        } elseif ($_REQUEST["action"] == "approve") {
            if (isset($_REQUEST["id"])) {
                echo approveQuestion($_REQUEST["id"], true);
            } else {
                echo "Missing fields";
            }
        } elseif ($_REQUEST["action"] == "disapprove") {
            if (isset($_REQUEST["id"])) {
                echo approveQuestion($_REQUEST["id"], false);
            } else {
                echo "Missing fields";
            }
        } else {
            echo "No valid action specified";
        }
    } else echo "No action specified";
}



function getEntries($table)
{
    // Connect to database
    $db_connection = db_connect();
    mysqli_set_charset($db_connection, "utf8mb4");

    // Sanitize table name
    $table = mysqli_real_escape_string($db_connection, $table);

    // Check if table is valid
    if (!in_array($table, ["sessions", "articles", "rooms", "authors", "article_questions", "conferences"])) return "No table found";

    // SQL query
    if ($table == "articles") $sql = "SELECT articles.*, GROUP_CONCAT(fullname SEPARATOR ', ') as 'authors' from articles LEFT JOIN article_authors ON articles.id = article_authors.article_id LEFT JOIN authors on authors.id = article_authors.author_id GROUP BY articles.id";
    else $sql = "SELECT * FROM $table";

    // Get all entries from database
    $query = $db_connection->query($sql);
    if ($query) {
        if ($query->num_rows != 0) {
            $entries = [];
            while ($row = $query->fetch_assoc()) {
                array_push($entries, $row);
            }
            $db_connection->close();
            http_response_code(200);
            return $entries;
        } else {
            return "No $table to display";
        }
    } else {
        $error = "Error: " . $sql . "<br>" . $db_connection->error;
        $db_connection->close();
        return $error;
    }
}



function entriesToTable($entries, $table)
{
    // Check if a message is to be shown
    if (is_string($entries)) {
        return "
        <tr><td class=\"empty-table\">$entries</td></tr>
        <tr>
            <td style=\"padding: 0;\" colspan=\"100%\">
                <div class=\"item-actions-container\">
                    <button class=\"insert-button table-button button\" onclick=\"openInsertOrUpdatePopup('$table', 'insert')\"><i class=\"bi bi-plus\"></i></button>
                </div>
            </td>
        </tr>";
    }

    // Set table header
    $responseHTML = "<tr>";
    foreach (array_keys($entries[0]) as $key) {
        $formattedTitle = ucwords(str_replace("_", " ", $key));
        if (str_contains($formattedTitle, "Id")) $formattedTitle = str_replace("Id", "ID", $formattedTitle);
        if (str_contains($formattedTitle, "Pdf")) $formattedTitle = str_replace("Pdf", "PDF", $formattedTitle);
        $responseHTML .= "<th>$formattedTitle</th>";
    }
    $responseHTML .= "<th>Actions</th></tr>";

    // Reverse sort the array (to start from most recent entry)
    arsort($entries);

    foreach ($entries as $row) {
        $id = $row["id"];
        $responseHTML .= "<tr>";
        foreach ($row as $data) {
            $responseHTML .= "<td><div class=\"table-content\">$data</div></td>";
        }
        if ($table == "article_questions") {
            $responseHTML .= "
                <td style=\"padding: 0;\">
                    <div class=\"item-actions-container\">
                        <button class=\"insert-button table-button button\" onclick=\"openApprovePopup('$table', '$id', true)\"><i class=\"bi bi-check-lg\"></i></button>
                        <button class=\"edit-button table-button button\" onclick=\"openApprovePopup('$table', '$id', false)\"><i class=\"bi bi-x-lg\"></i></button>
                        <button class=\"delete-button table-button button\" onclick=\"openDeletePopup('$table', '$id')\"><i class=\"bi bi-trash\"></i></button>
                    </div>
                </td>
            </tr>";
        } else {
            $responseHTML .= "
                <td style=\"padding: 0;\">
                    <div class=\"item-actions-container\">
                        <button class=\"edit-button table-button button\" onclick=\"openInsertOrUpdatePopup('$table', 'update', '$id')\"><i class=\"bi bi-pencil\"></i></button>
                        <button class=\"delete-button table-button button\" onclick=\"openDeletePopup('$table', '$id')\"><i class=\"bi bi-trash\"></i></button>
                    </div>
                </td>
            </tr>";
        }
    }

    if ($table == "article_questions") {
        $responseHTML .=
            "<tr>
            <td style=\"padding: 0;\" colspan=\"100%\">
                <div class=\"item-actions-container\">
                    <button class=\"insert-button table-button button\" onclick=\"openInsertOrUpdatePopup('$table', 'insert')\"><i class=\"bi bi-plus\"></i></button>
                </div>
            </td>
        </tr>";
    }

    http_response_code(200);
    return $responseHTML;
}



function insertEntry($table)
{
    // Connect to database
    $db_connection = db_connect();
    mysqli_set_charset($db_connection, "utf8mb4");

    // Validate and sanitize fields
    if (isset($_REQUEST["datetime_start"]) && isset($_REQUEST["datetime_end"]) && $_REQUEST["datetime_start"] == $_REQUEST["datetime_end"]) {
        return "Session has 0 duration";
    }
    if (isset($_REQUEST["title"]) && !(strlen($_REQUEST["title"]) < 255 && strlen($_REQUEST["title"]) > 0)) {
        return "Title too long or too short";
    }
    for ($i = 0; $i < count($_REQUEST); $i++) {
        if (is_string($_REQUEST[$i])) $_REQUEST[$i] = mysqli_real_escape_string($db_connection, $_REQUEST[$i]);
        elseif (is_array($_REQUEST[$i])) for ($j = 0; $j < count($_REQUEST[$i]); $j++) $_REQUEST[$i][$j] = mysqli_real_escape_string($db_connection, $_REQUEST[$i][$j]);
    }

    foreach ($_FILES as $key => $file) {
        if ($key == "pdf") {
            $fileStatus = uploadPDF($file, "./articles/", true);
            if (is_string($fileStatus)) {
                http_response_code(400);
                return $fileStatus;
            }
        }
    }

    // Get array of fields (exclude action, table and id for insert)
    $fields = array_diff_key($_REQUEST, ["action" => NULL, "table" => NULL, "id" => NULL, "author_id" => NULL]);
    $files = $_FILES;

    // Insert into table
    if (count($files) == 1) $sql = "INSERT INTO `$table` (`" . implode("`, `", array_keys($fields)) . "`, `" . array_key_first($files) . "`) VALUES ('" . implode("', '", array_values($fields)) . "', '" . $files[array_key_first($files)]["name"] . "')";
    else $sql = "INSERT INTO `$table` (`" . implode("`, `", array_keys($fields)) . "`) VALUES ('" . implode("', '", array_values($fields)) . "')";
    $query = $db_connection->query($sql);
    if (!$query) return $db_connection->error;

    // Insert into relation tables
    if ($table == "articles") {
        $values = [];
        foreach ($_REQUEST["author_id"] as $author_id) array_push($values, "('" . $db_connection->insert_id . "','$author_id')"); // use id from last insert query
        $sql = "INSERT INTO article_authors VALUES " . implode(", ", $values);
        $query = $db_connection->query($sql);
        if (!$query) return $db_connection->error;
    }

    // Success
    http_response_code(200);
    return "Entry inserted in $table table and any relation tables";
}



function updateEntry($table)
{
    // Connect to database
    $db_connection = db_connect();
    mysqli_set_charset($db_connection, "utf8mb4");

    // Validate and sanitize fields
    if (isset($_REQUEST["datetime_start"]) && isset($_REQUEST["datetime_end"]) && $_REQUEST["datetime_start"] == $_REQUEST["datetime_end"]) {
        return "Session has 0 duration";
    }
    if (isset($_REQUEST["title"]) && !(strlen($_REQUEST["title"]) < 255 && strlen($_REQUEST["title"]) > 0)) {
        return "Title too long or too short";
    }
    for ($i = 0; $i < count($_REQUEST); $i++) {
        if (is_string($_REQUEST[$i])) $_REQUEST[$i] = mysqli_real_escape_string($db_connection, $_REQUEST[$i]);
        elseif (is_array($_REQUEST[$i])) for ($j = 0; $j < count($_REQUEST[$i]); $j++) $_REQUEST[$i][$j] = mysqli_real_escape_string($db_connection, $_REQUEST[$i][$j]);
    }

    // Get array of fields (exclude action, table and id for insert)
    $fields = array_diff_key($_REQUEST, ["action" => NULL, "table" => NULL, "id" => NULL, "author_id" => NULL]);

    $sqlQueries = [];

    // Create update SQL query
    $sqlFields = [];
    foreach ($fields as $key => $value) array_push($sqlFields, "$key = '$value'");
    array_push($sqlQueries, "UPDATE `$table` SET " . implode(", ", $sqlFields) . " WHERE `id`='" . $_REQUEST["id"] . "'");

    // Create relation tables update query
    if ($table == "articles") {
        $values = [];
        foreach ($_REQUEST["author_id"] as $author_id) array_push($values, "('" . $_REQUEST["id"] . "','$author_id')");
        array_push($sqlQueries, "DELETE FROM article_authors WHERE article_id='" . $_REQUEST["id"] . "'");
        array_push($sqlQueries, "INSERT INTO article_authors VALUES " . implode(", ", $values));
    }

    // Execute queries
    foreach ($sqlQueries as $sql) {
        $query = $db_connection->query($sql);
        if ($query) {
            //$db_connection->close();
        } else {
            $error = "Error: " . $sql . "<br>" . $db_connection->error;
            $db_connection->close();
            return $error;
        }
    }

    // Success
    http_response_code(200);
    return "Entry updated in $table table and any relation tables";
}



function deleteEntry($type, $entryID)
{
    // Connect to database
    $db_connection = db_connect();
    mysqli_set_charset($db_connection, "utf8mb4");

    // Sanitize info
    $type = mysqli_real_escape_string($db_connection, $type);
    $entryID = mysqli_real_escape_string($db_connection, $entryID);

    // Delete entry from table
    $sql = "DELETE FROM $type WHERE id='$entryID'";
    $query = $db_connection->query($sql);
    if ($query === TRUE) {
        http_response_code(200);
        return "Entry deleted";
    } else {
        $db_connection->close();
        return "Error: " . $sql . "<br>" . $db_connection->error;
    }
}



function approveQuestion($id, $approve = true)
{
    // Connect to database
    $db_connection = db_connect();
    mysqli_set_charset($db_connection, "utf8mb4");

    // Sanitize info
    $id = mysqli_real_escape_string($db_connection, $id);

    // Approve or disapprove question  in table
    if ($approve) $sql = "UPDATE article_questions SET approved = true WHERE id=$id";
    else $sql = "UPDATE article_questions SET approved = false WHERE id=$id";
    $query = $db_connection->query($sql);
    if ($query === TRUE) {
        http_response_code(200);
        return "Question approved status updated";
    } else {
        $db_connection->close();
        return "Error: " . $sql . "<br>" . $db_connection->error;
    }
}



function uploadPDF($file, $targetDir, $overwrite = false)
{
    $targetFile = $targetDir . basename($file["name"]);
    $fileType = strtolower(pathinfo($targetFile, PATHINFO_EXTENSION));

    // Check if file already exists
    if (file_exists($targetFile) && !$overwrite) {
        return "Sorry, file with the same name already on the server";
    }

    // Check file size
    if ($file["size"] > 10000000) {
        return "Sorry, your file is too large (max 10MB)";
    }

    // Allow certain file formats
    if ($fileType != "pdf") {
        return "Sorry, only PDF files are allowed";
    }

    // Upload files if no errors
    if (move_uploaded_file($file["tmp_name"], $targetFile)) {
        //return "The file ". htmlspecialchars(basename($file["name"])). " has been uploaded.";
    } else {
        return "Sorry, there was an error uploading your file";
    }
}
