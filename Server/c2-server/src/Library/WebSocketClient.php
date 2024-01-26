<?php

namespace Server\Library;

use WebSocket\Client;
use Server\Library\Env;

/**
 * C2 Web socket client
 */
class WebSocketClient
{
    /** @var Client $client Web socket connection instance */
    private Client $client;
    
    public function __construct()
    {
        $this->client = new Client($_ENV["WS_SERVER"]);
    }

    /**
     * Send data to web socket
     * 
     * @param array $msg
     */
    public function send(array $msg): void
    {
        if ($this->client->isConnected())
            $this->client->send(json_encode($msg));
    }

    public function __destruct()
    {
        if (!$this->client->isConnected()) 
            $this->client->disconnect();
    }
}