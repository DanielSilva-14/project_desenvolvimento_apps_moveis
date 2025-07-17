<?php
$db = new mysqli("localhost", "root", "", "summithub"); 
$db->set_charset("utf8");

$path_params = explode('/', trim($_SERVER['PATH_INFO'] ?? ''));
$input = json_decode(file_get_contents('php://input'), true);

header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

switch ($_SERVER['REQUEST_METHOD']) {
    case 'GET':
        getRooms($db, $path_params);
        break;
    case 'POST':
        addRoom($db, $input);
        break;
    case 'PUT':
        updateRoom($db, $input, $path_params);
        break;
    case 'DELETE':
        deleteRoom($db, $path_params);
        break;
    default:
        echo json_encode(["message" => "HTTP method not supported."]);
}

function getRooms($db, $path_params) {
    $sql = "SELECT * FROM rooms";
    if (isset($path_params[1]) && is_numeric($path_params[1])) {
        $sql .= " WHERE id = " . $db->real_escape_string($path_params[1]);
    }
    $result = $db->query($sql);
    $rooms = [];
    while ($row = $result->fetch_assoc()) {
        $rooms[] = $row;
    }
    echo json_encode($rooms);
}

function addRoom($db, $input) {
    $sql = "INSERT INTO rooms (name, address, latitude, longitude) VALUES (?, ?, ?, ?)";
    $stmt = $db->prepare($sql);
    $stmt->bind_param("ssdd", $input['name'], $input['address'], $input['latitude'], $input['longitude']);
    $stmt->execute();
    echo json_encode(["message" => "Room added successfully.", "id" => $stmt->insert_id]);
    $stmt->close();
}

function updateRoom($db, $input, $path_params) {
    if (!isset($path_params[1]) || !is_numeric($path_params[1])) {
        echo json_encode(["message" => "Invalid room ID."]);
        return;
    }
    $sql = "UPDATE rooms SET name = ?, address = ?, latitude = ?, longitude = ? WHERE id = ?";
    $stmt = $db->prepare($sql);
    $stmt->bind_param("ssddi", $input['name'], $input['address'], $input['latitude'], $input['longitude'], $path_params[1]);
    $stmt->execute();
    echo json_encode(["message" => "Room updated successfully."]);
    $stmt->close();
}

function deleteRoom($db, $path_params) {
    if (!isset($path_params[1]) || !is_numeric($path_params[1])) {
        echo json_encode(["message" => "Invalid room ID."]);
        return;
    }
    $sql = "DELETE FROM rooms WHERE id = ?";
    $stmt = $db->prepare($sql);
    $stmt->bind_param("i", $path_params[1]);
    $stmt->execute();
    echo json_encode(["message" => "Room deleted successfully."]);
    $stmt->close();
}
?>
