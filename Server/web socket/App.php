<?php

require_once __DIR__ . '/vendor/autoload.php';

use C2\C2WebSocket;
use C2\Database;
use C2\Env;
use Ratchet\Http\HttpServer;
use Ratchet\Server\IoServer;
use Ratchet\WebSocket\WsServer;

Env::load(__DIR__ . '/.env');

$database = new Database();

$server = IoServer::factory(
    new HttpServer(
        new WsServer(
            new C2WebSocket($database)
        )
    ),
    8080
);

echo "Web sockets started...";
$server->run();
