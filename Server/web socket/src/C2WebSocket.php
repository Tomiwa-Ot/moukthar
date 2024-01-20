<?php

namespace C2;

use Ratchet\ConnectionInterface;
use Ratchet\RFC6455\Messaging\MessageInterface;
use Ratchet\WebSocket\MessageComponentInterface;

class C2WebSocket implements MessageComponentInterface {

    protected $clients;

    public function __construct()
    {
        $this->clients = new \SplObjectStorage();
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
                case "call":
                    echo "here";
                    break;
                case "text":
                    echo "here";
                    break;
                case "notification":
                    echo "here";
                    break;
                case "image":
                    echo "here";
                    break;
                case "video":
                    echo "here";
                    break;
                case "contact":
                    echo "here";
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
}