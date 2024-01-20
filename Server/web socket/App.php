<?php

require_once __DIR__ . '/vendor/autoload.php';

use C2\C2WebSocket;
use Ratchet\Http\HttpServer;
use Ratchet\Server\IoServer;
use Ratchet\WebSocket\WsServer;

$server = IoServer::factory(
    new HttpServer(
        new WsServer(
            new C2WebSocket()
        )
    ),
    8080
);

echo "Web sockets started...";
$server->run();
